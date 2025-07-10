package managers;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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

    @Test
    void fileBackedTaskManagerTest() {
        File tempFile = null;
        FileBackedTaskManager fileBackedTaskManager;
        try {
            tempFile = File.createTempFile("test-", ".txt");
            fileBackedTaskManager = new FileBackedTaskManager(tempFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            tempFile.delete();
        }
        assertNotNull(fileBackedTaskManager,"Менеджер истории равен Null");
    }
}