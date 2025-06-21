package tasks;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    static TaskManager taskManager;
    List<Task> history = List.of();

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }
    @Test
    public void addNewTaskTest() {
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
        history = taskManager.getHistory();

        assertNotNull(history, "После получения задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После получения задачи, история не должна быть пустой.");

    }

    @Test
    void addIdenticalTaskTest() {
        Task task = new Task("Задача 1", "Описание первой задачи");
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.getEpic(epic.getId());
        taskManager.getTask(task.getId());

        taskManager.getEpic(epic.getId());
        taskManager.getTask(task.getId());

        history = taskManager.getHistory();

        assertEquals(2, history.size(),
                "После получения одинаковых задач, история должна хранить уникальные задачи.");

    }
}