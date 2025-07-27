package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class InMemoryTaskManagerTest<T extends TaskManager> {
    static TaskManager taskManager;
    LocalDateTime timeSubtask3 = LocalDateTime.of(2025,7,20,11,0,0);
    Duration durationSubtask3 = Duration.ofMinutes(110);

    LocalDateTime timeSubtask4 = LocalDateTime.of(2025,7,20,9,10,0);
    Duration durationSubtask4 = Duration.ofMinutes(160);

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

    @Test
    void getTasksTest() {
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Метод не должен возвращать Null");
        assertEquals(2, tasks.size(), "Список не должен быть пустым после добавления Task");
    }

    @Test
    void deleteTasksTest() {
        taskManager.deleteTasks();
        List<Task> tasks = taskManager.getTasks();

        assertEquals(0, tasks.size(), "В Manager не должно быть Task после удаления.");
    }

    @Test
    void updateTaskTest() {
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        Task taskActual = taskManager.getTask(task.getId());

        assertEquals(task, taskActual, "Метод должен возвращать обновленную Task");
    }

    @Test
    void deleteTaskForIdTest() {
        taskManager.deleteTaskForId(task.getId());

        assertNull(taskManager.getTask(task.getId()), "Метод должен удалять Task по Id");
    }

    @Test
    void getEpicsTest() {
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Метод не должен возвращать Null");
        assertEquals(3, epics.size(), "Список не должен быть пустым после добавления Epic");
    }

    @Test
    void deleteEpicsTest() {
        taskManager.deleteEpics();
        List<Epic> epics = taskManager.getEpics();

        assertEquals(0, epics.size(), "В Manager не должно быть Epic после удаления.");
    }

    @Test
    void updateEpicTest() {
        epic.setStatus(Status.DONE);
        taskManager.updateEpic(epic);
        Epic epicActual = taskManager.getEpic(epic.getId());

        assertEquals(epic, epicActual, "Метод должен возвращать обновленный Epic");
    }

    @Test
    void deleteEpicForId() {
        taskManager.deleteEpicForId(epic1.getId());

        assertNull(taskManager.getEpic(epic1.getId()), "Метод должен удалить Epic по Id.");
        assertNull(taskManager.getSubTask(subTask2.getId()), "Метод должен удалить SubTask вместе с Epic.");
    }

    @Test
    void getSubTasksTest() {
        List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Метод не должен возвращать Null");
        assertEquals(6, subTasks.size(), "Список не должен быть пустым после добавления SubTask");
    }

    @Test
    void deleteSubTasksTest() {
        taskManager.deleteSubTasks();
        List<SubTask> subTasks = taskManager.getSubTasks();

        assertEquals(0, subTasks.size(), "В Manager не должно быть SubTask после удаления.");
    }

    @Test
    void updateSubTaskTest() {
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);
        SubTask subTaskActual = taskManager.getSubTask(subTask2.getId());

        assertEquals(subTask2, subTaskActual, "Метод должен возвращать обновленную SubTask.");
    }

    @Test
    void deleteSubTaskForId() {
        taskManager.deleteSubTaskForId(subTask2.getId());
        List<Integer> subTaskEpic1 = epic1.getSubTasksId();

        assertNull(taskManager.getSubTask(subTask2.getId()), "Метод должен удалять SubTask по Id.");
        assertEquals(0, subTaskEpic1.size(), "Метод должен удалить SubTask из Epic.");
    }

    @Test
    void changeStatusEpicTest() {
        assertEquals(epic.getStatus(),Status.NEW, "У Epic должен быть статус New." );

        subTask.setStatus(Status.IN_PROGRESS);
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask);
        taskManager.updateSubTask(subTask1);
        assertEquals(epic.getStatus(),Status.IN_PROGRESS, "У Epic должен быть статус In-Progress.");

        subTask.setStatus(Status.DONE);
        subTask1.setStatus(Status.NEW);
        taskManager.updateSubTask(subTask);
        taskManager.updateSubTask(subTask1);
        assertEquals(epic.getStatus(),Status.NEW, "У Epic должен быть статус New.");

        subTask.setStatus(Status.DONE);
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        taskManager.updateSubTask(subTask1);
        assertEquals(epic.getStatus(),Status.DONE, "У Epic должен быть статус Done.");
    }

    @Test
    void calculationTimeEpicTest() {
        assertEquals(epic2.getStartTime(), subTask4.getStartTime(),
                "У Epic должно быть начальное время ранней SubTask");
        assertEquals(epic2.getEndTime(), subTask3.getEndTime(),
                "У Epic должно быть конечное время поздней SubTask");
    }

    @Test
    void prioritizedTaskTest() {
        List<Task> prioritizedTaskList = taskManager.getPrioritizedTasks();
        assertEquals(prioritizedTaskList.size(),2,"prioritizedList должен вернуть две Task");
        assertEquals(prioritizedTaskList.get(0),subTask5,"Метод должен сортировать список по времени.");
        assertEquals(prioritizedTaskList.get(1),subTask3,"Метод должен сортировать список по времени.");
    }
}
