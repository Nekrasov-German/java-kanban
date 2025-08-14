package tasks;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    static TaskManager taskManager;

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void addNewSubTaskTest() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epic.getId());
        int idSubTask = taskManager.createSubTask(subTask);
        final SubTask subTask1 = taskManager.getSubTask(idSubTask);

        assertNotNull(subTask1, "Подзадача не найдена.");
        assertEquals(subTask,subTask1, "Подзадачи не равны");

        final List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Эпики не совпадают.");
    }
}