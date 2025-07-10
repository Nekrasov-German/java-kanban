package helper;

import tasks.*;

public class TasksToString {
    public static String taskToStringForSave(Task task) {
        String result = "";
        switch (task.getType()) {
            case TASK, EPIC -> result = String.format("%s,%s,%s,%s,%s,%s",
                    task.getId(),task.getType(),task.getName(),task.getStatus(), task.getDescription(),"\n");
            case SUBTASK -> {
                SubTask subTask = (SubTask) task;
                result = String.format("%s,%s,%s,%s,%s,%s", subTask.getId(), subTask.getType(), subTask.getName(),
                        subTask.getStatus(), subTask.getDescription(), subTask.getEpicId() + "\n");
            }
        }
        return result;

    }

    public static Task loadFromFileTasks(String taskString) {
        String[] task = taskString.split(",");
        try {
            int id = Integer.parseInt(task[0]);
            Type type = Type.valueOf(task[1]);
            String name = task[2];
            Status status = Status.valueOf(task[3]);
            String description = task[4];

            if (type == Type.TASK) {
                return new Task(name, description, id, status, type);
            } else if (type == Type.EPIC) {
                return new Epic(name, description, id, status, type);
            } else if (type == Type.SUBTASK) {
                int idEpic = Integer.parseInt(task[5]);
                return new SubTask(name, description, id, status, type, idEpic);
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
