package managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultTest() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер равен Null");
    }

    @Test
    void getDefaultHistoryTest() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер истории равен Null");
    }
}