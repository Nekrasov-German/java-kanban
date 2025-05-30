package tasks;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    static TaskManager taskManager;

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }
    @Test
    public void addNewTaskEqualsTask() {
        Task task = new Task("Задача 1", "Описание первой задачи");
        taskManager.createTask(task);
        final Task task1 = taskManager.getTask(task.getId());

        assertNotNull(task1, "Задача не найдена.");
        assertEquals(task,task1, "Задачи не равны.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addTaskHistory() {
        Task task = new Task("Задача 1", "Описание первой задачи");
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "После получения задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После получения задачи, история не должна быть пустой.");
    }
}