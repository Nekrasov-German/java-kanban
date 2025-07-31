package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest {

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
        assertEquals(5, subTasks.size(), "Список не должен быть пустым после добавления SubTask");
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
        assertEquals(prioritizedTaskList.get(0),subTask4,"Метод должен сортировать список по времени.");
        assertEquals(prioritizedTaskList.get(1),subTask3,"Метод должен сортировать список по времени.");

        assertDoesNotThrow(() ->taskManager.getPrioritizedTasks());
    }
}
