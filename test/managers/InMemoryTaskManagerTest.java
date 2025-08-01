package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void createManager() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
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
