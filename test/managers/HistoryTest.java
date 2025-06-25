package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryTest {
    static TaskManager taskManager;

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    void historyBeforeDeleteSubTaskTest() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epic);
        SubTask subTask1 = new SubTask("Подзадача 2","Описание подзадачи 2", epic);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask1);

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
