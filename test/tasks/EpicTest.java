package tasks;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    static TaskManager taskManager;

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void addNewEpicTest() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        int idEpic = taskManager.createEpic(epic);
        final Epic epic1 = taskManager.getEpic(idEpic);

        assertNotNull(epic1, "Эпик не найден.");
        assertEquals(epic,epic1, "Эпики не равны");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }
}