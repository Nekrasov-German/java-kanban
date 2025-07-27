package managers;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final int ERROR_ONE = -1;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private static int nextId = 0;

    public static int getNextId() {
        nextId++;
        return nextId;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubTasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean allDone = true;
            boolean anyInProgress = false;

            for (Integer subTaskId : epic.getSubTasksId()) {
                Status statusSubTask = subTasks.get(subTaskId).getStatus();
                if (statusSubTask == Status.NEW) {
                    epic.setStatus(Status.NEW);
                    return;
                }
                if (statusSubTask == Status.IN_PROGRESS) {
                    anyInProgress = true;
                }
                if (statusSubTask != Status.DONE) {
                    allDone = false;
                }
            }

            if (anyInProgress) {
                epic.setStatus(Status.IN_PROGRESS);
            } else if (allDone) {
                epic.setStatus(Status.DONE);
            }
        }
    }

    private void updateEpicStartTimeAndEndTime(Epic epic) {
        if (epic.getSubTasksId().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
        } else {
            for (Integer subTaskId : epic.getSubTasksId()) {
                try {
                    LocalDateTime startTimeSubTask = subTasks.get(subTaskId).getStartTime();
                    LocalDateTime endTimeSubTask = subTasks.get(subTaskId).getEndTime();
                    if (startTimeSubTask != null) {
                        if (epic.getStartTime() == null) {
                            epic.setStartTime(startTimeSubTask);
                            epic.setDuration(Duration.between(startTimeSubTask, endTimeSubTask));
                        }
                        if (startTimeSubTask.isBefore(epic.getStartTime())) {
                            epic.setStartTime(startTimeSubTask);
                        }
                        if (endTimeSubTask.isAfter(epic.getEndTime())) {
                            epic.setDuration(Duration.between(epic.getStartTime(), endTimeSubTask));
                        }
                    }
                } catch (NullPointerException ignored) {

                }
            }
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isNotIntersectionTask(Task task) {
        boolean result = false;
        LocalDateTime startTask = task.getStartTime();
        LocalDateTime endTask = task.getEndTime();

        if (!prioritizedTasks.isEmpty()) {
            result = prioritizedTasks.stream()
                    .anyMatch(taskValid -> taskValid.getStartTime().isBefore(startTask) &&
                            taskValid.getEndTime().isAfter(endTask) || startTask.isBefore(taskValid.getStartTime()) &&
                            endTask.isAfter(taskValid.getEndTime()) || (startTask.isAfter(taskValid.getStartTime()) &&
                            startTask.isBefore(taskValid.getEndTime())) ||
                            (endTask.isBefore(taskValid.getEndTime()) && endTask.isAfter(taskValid.getStartTime())));
        }
        return result;
    }

    private void addPrioritizedTask(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            if (!isNotIntersectionTask(task)) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public List<Task> getTasks() {
        if (!tasks.isEmpty()) {
            List<Task> allTasks = new ArrayList<>(tasks.values());
            allTasks.forEach(historyManager::add);
            return allTasks;
        } else {
            return List.of();
        }
    }

    @Override
    public void deleteTasks() {
        tasks.keySet().forEach(historyManager::remove);
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public int createTask(Task task) {
        if (task != null && task.getType() == Type.TASK) {
            tasks.put(task.getId(), task);
            addPrioritizedTask(task);
            return task.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public int updateTask(Task task) {
        if (task != null && task.getType() == Type.TASK && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(),task);
            addPrioritizedTask(task);
            return task.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public void deleteTaskForId(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Epic> getEpics() {
        if (!epics.isEmpty()) {
            List<Epic> allEpics = new ArrayList<>(epics.values());
            allEpics.forEach(historyManager::add);
            return allEpics;
        } else {
            return List.of();
        }
    }

    @Override
    public void deleteEpics() {
        epics.keySet().stream()
                .peek(keyEpic -> epics.get(keyEpic).getSubTasksId()
                        .forEach(historyManager::remove));
        if (!epics.isEmpty()) {
            epics.clear();
            subTasks.clear();
        }
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic != null && epic.getType() == Type.EPIC) {
            epics.put(epic.getId(),epic);
            return epic.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public int updateEpic(Epic epic) {
        if (epic != null && epic.getType() == Type.EPIC && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(),epic);
            return epic.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public void deleteEpicForId(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).getSubTasksId().forEach(subTasks::remove);
            epics.get(id).getSubTasksId().forEach(historyManager::remove);
            epics.remove(id);
        }
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> getSubTasksForEpic(Epic epic) {
        List<SubTask> result = new ArrayList<>();
        if (!epic.getSubTasksId().isEmpty()) {
            for (Integer subTaskId : epic.getSubTasksId()) {
                if (subTasks.containsKey(subTaskId)) {
                    result.add(subTasks.get(subTaskId));
                    historyManager.add(subTasks.get(subTaskId));
                }
            }
        }
        return result;
    }

    @Override
    public List<SubTask> getSubTasks() {
        if (!subTasks.isEmpty()) {
            List<SubTask> allSubTasks = new ArrayList<>(subTasks.values());
            allSubTasks.forEach(historyManager::add);
            return allSubTasks;
        } else {
            return List.of();
        }
    }

    @Override
    public void deleteSubTasks() {
        subTasks.keySet().forEach(historyManager::remove);
        if (!subTasks.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.getSubTasksId().clear();
                updateEpicStatus(epic);
                updateEpicStartTimeAndEndTime(epic);
            }
            subTasks.clear();
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public int createSubTask(SubTask subTask) {
        if (subTask != null && subTask.getType() == Type.SUBTASK) {
            subTasks.put(subTask.getId(),subTask);
            addPrioritizedTask(subTask);
            epics.get(subTask.getEpicId()).setSubTasksId(subTask);
            updateEpicStatus(epics.get(subTask.getEpicId()));
            updateEpicStartTimeAndEndTime(epics.get(subTask.getEpicId()));
            return subTask.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public int updateSubTask(SubTask subTask) {
        if (subTask != null && subTask.getType() == Type.SUBTASK && subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(),subTask);
            addPrioritizedTask(subTask);
            updateEpicStatus(epics.get(subTask.getEpicId()));
            updateEpicStartTimeAndEndTime(epics.get(subTask.getEpicId()));
            return subTask.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public void deleteSubTaskForId(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.remove(id);
            Epic epic = epics.get(subTask.getEpicId());
            for (int i = 0; i < epic.getSubTasksId().size(); i++) {
                if (epic.getSubTasksId().get(i) == id) {
                    epic.getSubTasksId().remove(i);
                }
            }
            updateEpicStatus(epic);
            updateEpicStartTimeAndEndTime(epic);
        }
        historyManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
