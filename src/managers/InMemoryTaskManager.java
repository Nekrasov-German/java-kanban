package managers;

import tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final int ERROR_ONE = -1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
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

    @Override
    public List<Task> getTasks() {
        if (!tasks.isEmpty()) {
            List<Task> allTasks = new ArrayList<>(tasks.values());
            for (Task task : allTasks) {
                historyManager.add(task);
            }
            return allTasks;
        } else {
            return List.of();
        }
    }

    @Override
    public void deleteTasks() {
        for (Integer keys : tasks.keySet()) {
            historyManager.remove(keys);
        }
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
            return task.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public int updateTask(Task task) {
        if (task != null && task.getType() == Type.TASK && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(),task);
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
            for (Epic epic : allEpics) {
                historyManager.add(epic);
            }
            return allEpics;
        } else {
            return List.of();
        }
    }

    @Override
    public void deleteEpics() {
        for (Integer keyEpic : epics.keySet()) {
            for (Integer keySubTask : epics.get(keyEpic).getSubTasksId()) {
                historyManager.remove(keySubTask);
            }
            historyManager.remove(keyEpic);
        }
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
            for (Integer subTaskId : epics.get(id).getSubTasksId()) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
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
            for (SubTask subTask : allSubTasks) {
                historyManager.add(subTask);
            }
            return allSubTasks;
        } else {
            return List.of();
        }
    }

    @Override
    public void deleteSubTasks() {
        for (Integer keySubTask : subTasks.keySet()) {
            historyManager.remove(keySubTask);
        }
        if (!subTasks.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.getSubTasksId().clear();
                updateEpicStatus(epic);
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
            epics.get(subTask.getEpicId()).setSubTasksId(subTask);
            updateEpicStatus(epics.get(subTask.getEpicId()));
            return subTask.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public int updateSubTask(SubTask subTask) {
        if (subTask != null && subTask.getType() == Type.SUBTASK && subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(),subTask);
            updateEpicStatus(epics.get(subTask.getEpicId()));
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
        }
        historyManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
