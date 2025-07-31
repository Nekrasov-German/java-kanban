package managers;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryTest extends TaskManagerTest{
    @Test
    void getEmptyHistoryTest() {
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size(), "История должна быть пустой.");
    }

    @Test
    void historyAfterRepeatingSubTaskTest() {
        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getSubTask(subTask1.getId());

        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getSubTask(subTask1.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "После повторных добалений не должно быть повторов задач.");
    }

    @Test
    void historyAfterDeleteSubTaskTest() {
        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getSubTask(subTask1.getId());

        taskManager.deleteSubTaskForId(subTask.getId());
        taskManager.deleteSubTaskForId(subTask1.getId());

        List<Task> history = taskManager.getHistory();

        List<Integer> epicSubTaskId = epic.getSubTasksId();

        assertEquals(0, epicSubTaskId.size(), "После удаления SubTask у Epic не должно быть SubTask");

        assertEquals(1, history.size(), "После удаления в истории не должно быть удаленных задач");

    }
}
