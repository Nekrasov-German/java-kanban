package managers;

import exceptions.ManagerSaveException;
import helper.TasksToString;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.Type;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static void main(String[] args) {
        TaskManager fileBackedTaskManager = new FileBackedTaskManager("Test.CSV");

        LocalDateTime time = LocalDateTime.of(2025,7,20,13,0,0);
        Duration duration = Duration.ofMinutes(25);

        LocalDateTime timeSubtask2 = LocalDateTime.of(2025,7,20,13,0,0);
        Duration durationSubtask2 = Duration.ofMinutes(110);

        LocalDateTime timeSubtask1 = LocalDateTime.of(2025,7,20,16,0,0);
        Duration durationSubtask1 = Duration.ofMinutes(40);

        Task task1 = new Task("Задача 1", "Описание первой задачи",duration,time);
        Task task2 = new Task("Задача 2", "Описание первой задачи");
        Epic epicWithSubTasks = new Epic("Эпик с тремя подзадачами", "Содержит три подзадачи.");
        SubTask subTask1 = new SubTask("Подзадача 1","Описание подзадачи 1", durationSubtask1,
                timeSubtask1, epicWithSubTasks);
        SubTask subTask2 = new SubTask("Подзадача 2","Описание подзадачи 2", durationSubtask2,
                timeSubtask2, epicWithSubTasks);
        SubTask subTask3 = new SubTask("Подзадача 3","Описание подзадачи 3", epicWithSubTasks);
        Epic epicWithoutSubTask = new Epic("Эпик без подзадач","Описание Эпика без подзадачю");

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(task2);
        fileBackedTaskManager.createEpic(epicWithSubTasks);
        fileBackedTaskManager.createSubTask(subTask1);
        fileBackedTaskManager.createSubTask(subTask2);
        fileBackedTaskManager.createSubTask(subTask3);
        fileBackedTaskManager.createEpic(epicWithoutSubTask);

        for (Task task : fileBackedTaskManager.getTasks()) {
            System.out.println(task);
        }
        for (SubTask subTask : fileBackedTaskManager.getSubTasks()) {
            System.out.println(subTask);
        }
        for (Epic epic : fileBackedTaskManager.getEpics()) {
            System.out.println(epic);
        }
    }

    private final File file;
    private static final String FILE_HEADER = "id,type,name,status,description,duration,startTime,epic" +
            System.lineSeparator();

    public FileBackedTaskManager(String path) {
        this.file = new File(path);
        loadFromFile(file);
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(FILE_HEADER);
            for (Task task : super.getTasks()) {
                fileWriter.write(TasksToString.taskToStringForSave(task));
            }
            for (Epic epic : super.getEpics()) {
                fileWriter.write(TasksToString.taskToStringForSave(epic));
            }
            for (SubTask subTask : super.getSubTasks()) {
                fileWriter.write(TasksToString.taskToStringForSave(subTask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка менеджера сохранения при сохранении файла.");
        }
    }

    private void loadFromFile(File file) throws ManagerSaveException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String taskString = bufferedReader.readLine();
                Task task = TasksToString.loadFromFileTasks(taskString);
                if (task == null) {
                    continue;
                }
                switch (task.getType()) {
                    case Type.TASK -> createTask(task);
                    case Type.EPIC -> createEpic((Epic) task);
                    case Type.SUBTASK -> createSubTask((SubTask) task);
                }
            }
        } catch (IOException | NullPointerException e) {
            throw new ManagerSaveException("Ошибка менеджера сохранения при загрузке файла.");
        }
    }

    @Override
    public int createTask(Task task) {
        if (task.getId() != ERROR_ONE) {
            super.createTask(task);
            save();
            return task.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public int updateTask(Task task) {
        if (task.getId() != ERROR_ONE) {
            super.updateTask(task);
            save();
            return task.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public void deleteTaskForId(int id) {
        super.deleteTaskForId(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic.getId() != ERROR_ONE) {
            super.createEpic(epic);
            save();
            return epic.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public int updateEpic(Epic epic) {
        if (epic.getId() != ERROR_ONE) {
            super.updateEpic(epic);
            save();
            return epic.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public void deleteEpicForId(int id) {
        super.deleteEpicForId(id);
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public int createSubTask(SubTask subTask) {
        if (subTask.getId() != ERROR_ONE) {
            super.createSubTask(subTask);
            save();
            return subTask.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public int updateSubTask(SubTask subTask) {
        if (subTask.getId() != ERROR_ONE) {
            super.updateSubTask(subTask);
            save();
            return subTask.getId();
        }
        return ERROR_ONE;
    }

    @Override
    public void deleteSubTaskForId(int id) {
        super.deleteSubTaskForId(id);
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }
}
