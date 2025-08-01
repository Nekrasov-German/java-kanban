package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{
    File tempFile = null;
    FileBackedTaskManager taskManagerEmpty;

    @BeforeEach
    public void createManager() {
        try {
            tempFile = File.createTempFile("test-", ".txt");
            taskManager = new FileBackedTaskManager(tempFile.getPath());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            tempFile.delete();
        }
    }

    @Test
    void loadEmptyFileManagerTest() {
        try {
            tempFile = File.createTempFile("test-", ".txt");
            taskManagerEmpty = new FileBackedTaskManager(tempFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            tempFile.delete();
        }
        List<Task> tasks = taskManagerEmpty.getTasks();
        List<Epic> epics = taskManagerEmpty.getEpics();
        List<SubTask> subTasks = taskManagerEmpty.getSubTasks();

        assertEquals(0,tasks.size(),"В пустом менеджере не должно быть Task.");
        assertEquals(0,epics.size(),"В пустом менеджере не должно быть Epic.");
        assertEquals(0,subTasks.size(),"В пустом менеджере не должно быть SubTask.");
    }

    @Test
    void loadNotEmptyFileManagerTest() {
        taskManager.createSubTask(subTask2);

        FileBackedTaskManager fileBackedTaskManagerTest = new FileBackedTaskManager(tempFile.getPath());

        List<Task> tasks = fileBackedTaskManagerTest.getTasks();
        List<Epic> epics = fileBackedTaskManagerTest.getEpics();
        List<SubTask> subTasks = fileBackedTaskManagerTest.getSubTasks();

        assertDoesNotThrow(()-> new FileBackedTaskManager(tempFile.getPath()));

        assertEquals(2,tasks.size(),"После загрузки в новом менеджере должно быть две задачи.");
        assertEquals(3,epics.size(),"После загрузки в новом менеджере должно быть три Эпика.");
        assertEquals(5,subTasks.size(),
                "После загрузки в новом менеджере должно быть пять подзадач.");
    }

    @Test
    void loadBeforeDeleteTaskFileManagerTest() {
        taskManager.deleteTaskForId(task.getId());
        taskManager.deleteEpicForId(epic1.getId());
        taskManager.deleteSubTaskForId(subTask.getId());

        FileBackedTaskManager fileBackedTaskManagerTest = new FileBackedTaskManager(tempFile.getPath());

        List<Task> tasks = fileBackedTaskManagerTest.getTasks();
        List<Epic> epics = fileBackedTaskManagerTest.getEpics();
        List<SubTask> subTasks = fileBackedTaskManagerTest.getSubTasks();

        assertEquals(1,tasks.size(),"После загрузки в новом менеджере должно быть одна задача.");
        assertEquals(2,epics.size(),"После загрузки в новом менеджере должно быть два Эпик.");
        assertEquals(3,subTasks.size(),
                "После загрузки в новом менеджере должно быть три подзадачи.");
    }
}
