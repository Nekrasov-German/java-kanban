import java.util.HashMap;

public class Manager implements InManager {
    private HashMap<Integer,Task> tasks = new HashMap<>();
    private HashMap<Integer,Epic> epics = new HashMap<>();
    private HashMap<Integer,SubTask> subTasks = new HashMap<>();
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
                if (subTasks.get(subTaskId).getStatus() == Status.NEW) {
                    epic.setStatus(Status.NEW);
                    return;
                }
                if (subTasks.get(subTaskId).getStatus() == Status.IN_PROGRESS) {
                    anyInProgress = true;
                }
                if (subTasks.get(subTaskId).getStatus() != Status.DONE) {
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
    public HashMap<Integer, Task> getTasks() {
        if (!tasks.isEmpty()) {
            return tasks;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void deleteTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    @Override
    public Task getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public void createTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())){
            tasks.put(task.getId(),task);
        }
    }

    @Override
    public void deleteTaskForId(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        if (!epics.isEmpty()) {
            return epics;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void deleteEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
            subTasks.clear();
        }
    }

    @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(),epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(),epic);
        }
    }

    @Override
    public void deleteEpicForId(int id) {
        if (epics.containsKey(id)) {
            for (Integer subTaskId : epics.get(id).getSubTasksId()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(id);
        }
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasksForEpic(Epic epic) {
        HashMap<Integer, SubTask> result = new HashMap<>();
        for (Integer subTaskId : epic.getSubTasksId()) {
            result.put(subTaskId, subTasks.get(subTaskId));
        }
        return result;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        if (!subTasks.isEmpty()) {
            return subTasks;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void deleteSubTasks() {
        if (!subTasks.isEmpty()) {
            subTasks.clear();
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.put(subTask.getId(),subTask);
            epics.get(subTask.getEpicId()).setSubTasksId(subTask.getId());
            updateEpicStatus(epics.get(subTask.getEpicId()));
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null && subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(),subTask);
            epics.get(subTask.getEpicId()).setSubTasksId(subTask.getId());
            updateEpicStatus(epics.get(subTask.getEpicId()));
        }
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
    }
}
