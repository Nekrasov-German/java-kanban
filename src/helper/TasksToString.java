package helper;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class TasksToString {
    public static String taskToStringForSave(Task task) {
        String result = "";
        switch (task.getType()) {
            case TASK, EPIC -> result = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    task.getType(),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    task.getDuration(),
                    task.getStartTime(),
                    System.lineSeparator());
            case SUBTASK -> {
                SubTask subTask = (SubTask) task;
                result = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        subTask.getId(),
                        subTask.getType(),
                        subTask.getName(),
                        subTask.getStatus(),
                        subTask.getDescription(),
                        subTask.getDuration(),
                        subTask.getStartTime(),
                        subTask.getEpicId(),
                        System.lineSeparator());
            }
        }
        return result;

    }

    public static Task loadFromFileTasks(String taskString) throws DateTimeParseException {
        String[] task = taskString.split(",");
        try {
            int id = Integer.parseInt(task[0]);
            Type type = Type.valueOf(task[1]);
            String name = task[2];
            Status status = Status.valueOf(task[3]);
            String description = task[4];

            switch (type) {
                case Type.TASK -> {
                    if ("null".equals(task[5]) && "null".equals(task[6])) {
                        return new Task(name, description, id, status, type);
                    } else {
                        Duration duration = Duration.parse(task[5]);
                        LocalDateTime startTime = LocalDateTime.parse(task[6]);
                        return new Task(name, description, id, status, type, duration, startTime);
                    }
                }
                case Type.EPIC -> {
                    return new Epic(name, description, id, status, type);
                }
                case Type.SUBTASK -> {
                    int idEpic = Integer.parseInt(task[7]);
                    if ("null".equals(task[5]) && "null".equals(task[6])) {
                        return new SubTask(name, description, id, status, type, idEpic);
                    } else {
                        Duration duration = Duration.parse(task[5]);
                        LocalDateTime startTime = LocalDateTime.parse(task[6]);
                        return new SubTask(name, description, id, status, type, duration, startTime, idEpic);
                    }
                }
                default -> {
                    return null;
                }
            }
        } catch (NumberFormatException e) {
           return null;
        }
    }
}
