package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest{
    File tempFile = null;
    static FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void createFileManager() {
        try {
            tempFile = File.createTempFile("test-", ".txt");
            fileBackedTaskManager = new FileBackedTaskManager(tempFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            tempFile.delete();
        }
    }

    @Test
    void loadEmptyFileManagerTest() {
        List<Task> tasks = fileBackedTaskManager.getTasks();
        List<Epic> epics = fileBackedTaskManager.getEpics();
        List<SubTask> subTasks = fileBackedTaskManager.getSubTasks();

        assertEquals(0,tasks.size(),"В пустом менеджере не должно быть Task.");
        assertEquals(0,epics.size(),"В пустом менеджере не должно быть Epic.");
        assertEquals(0,subTasks.size(),"В пустом менеджере не должно быть SubTask.");
    }

    @Test
    void loadNotEmptyFileManagerTest() {
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubTask(subTask);
        fileBackedTaskManager.createSubTask(subTask1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createSubTask(subTask2);

        FileBackedTaskManager fileBackedTaskManagerTest = new FileBackedTaskManager(tempFile.getPath());

        List<Task> tasks = fileBackedTaskManagerTest.getTasks();
        List<Epic> epics = fileBackedTaskManagerTest.getEpics();
        List<SubTask> subTasks = fileBackedTaskManagerTest.getSubTasks();

        assertEquals(2,tasks.size(),"После загрузки в новом менеджере должно быть две задачи.");
        assertEquals(2,epics.size(),"После загрузки в новом менеджере должно быть два Эпика.");
        assertEquals(3,subTasks.size(),
                "После загрузки в новом менеджере должно быть четыре подзадачи.");
    }

    @Test
    void loadBeforeDeleteTaskFileManagerTest() {
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubTask(subTask);
        fileBackedTaskManager.createSubTask(subTask1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createSubTask(subTask2);

        fileBackedTaskManager.deleteTaskForId(task.getId());
        fileBackedTaskManager.deleteEpicForId(epic1.getId());
        fileBackedTaskManager.deleteSubTaskForId(subTask.getId());

        FileBackedTaskManager fileBackedTaskManagerTest = new FileBackedTaskManager(tempFile.getPath());

        List<Task> tasks = fileBackedTaskManagerTest.getTasks();
        List<Epic> epics = fileBackedTaskManagerTest.getEpics();
        List<SubTask> subTasks = fileBackedTaskManagerTest.getSubTasks();

        assertEquals(1,tasks.size(),"После загрузки в новом менеджере должно быть одна задача.");
        assertEquals(1,epics.size(),"После загрузки в новом менеджере должно быть один Эпик.");
        assertEquals(1,subTasks.size(),
                "После загрузки в новом менеджере должно быть одна подзадача.");
    }
}
