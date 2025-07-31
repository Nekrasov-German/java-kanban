package managers;

import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest<T extends TaskManager> {
    static TaskManager taskManager;
    LocalDateTime timeSubtask3 = LocalDateTime.of(2025,7,20,11,0,0);
    Duration durationSubtask3 = Duration.ofMinutes(110);

    LocalDateTime timeSubtask4 = LocalDateTime.of(2025,7,20,9,10,0);
    Duration durationSubtask4 = Duration.ofMinutes(60);

    LocalDateTime timeSubtask5 = LocalDateTime.of(2025,7,20,10,0,0);
    Duration durationSubtask5 = Duration.ofMinutes(40);

    Task task = new Task("Задача 1", "Описание первой задачи");
    Task task1 = new Task("Задача 2","Описание задачи 2");
    Epic epic = new Epic("Эпик 1", "Описание эпика 1");
    SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epic);
    SubTask subTask1 = new SubTask("Подзадача 2","Описание подзадачи 2", epic);
    Epic epic1 = new Epic("Эпик 2","Описание Эпика 2");
    SubTask subTask2 = new SubTask("Подзадача 3","Описание подзадачи 3", epic1);

    Epic epic2 = new Epic("Эпик 1", "Описание эпика 1");
    SubTask subTask3 = new SubTask("Подзадача 1","Описание подзадачи 1", durationSubtask3, timeSubtask3,
            epic2);
    SubTask subTask4 = new SubTask("Подзадача 1","Описание подзадачи 1", durationSubtask4, timeSubtask4,
            epic2);
    SubTask subTask5 = new SubTask("Подзадача 1","Описание подзадачи 1", durationSubtask5, timeSubtask5,
            epic2);

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask1);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask3);
        taskManager.createSubTask(subTask4);
        taskManager.createSubTask(subTask5);
    }
}
