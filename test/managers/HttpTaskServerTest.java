package managers;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private static final InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();
    private HttpTaskServer server;
    private static final int PORT = 8080;
    private static Gson gson;

    @BeforeEach
    public void startServerTest() {
        manager.deleteTasks();
        manager.deleteSubTasks();
        manager.deleteEpics();

        try {

            server = new HttpTaskServer(manager);
            gson = server.getJson();
            server.getServer().createContext("/tasks", server);
            server.getServer().createContext("/subtasks", server);
            server.getServer().createContext("/epics", server);
            server.getServer().createContext("/history", server);
            server.getServer().createContext("/prioritized", server);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.startHttpServer();

        // Добавляем задержку для гарантии запуска сервера
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    public void stopServerTest() {
        server.stopHttpServer();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        client.close();
    }

    @Test
    public void getTasksTest() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(10);
        Task task = new Task("Test 2", "Testing task 2",
                Duration.ofMinutes(5), startTime);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/tasks");

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        assertTrue(responseBody.contains("Test 2"), "Ответ сервера не содержит ожидаемых данных");
        client.close();
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "description");
        manager.createEpic(epic);
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(20);
        SubTask subTask = new SubTask("SubTask 1", "Testing subtask 1",
                Duration.ofMinutes(5), startTime, manager.getEpic(epic.getId()).getId());
        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getSubTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("SubTask 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        client.close();
    }

    @Test
    public void getSubTasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "description");
        manager.createEpic(epic);
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);
        SubTask subTask = new SubTask("SubTask 2", "Testing subtask 2",
                Duration.ofMinutes(5), startTime, manager.getEpic(epic.getId()).getId());
        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        assertTrue(responseBody.contains("SubTask 2"), "Ответ сервера не содержит ожидаемых данных");
        client.close();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        client.close();
    }

    @Test
    public void getEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        assertTrue(responseBody.contains("Epic 1"), "Ответ сервера не содержит ожидаемых данных");
        client.close();
    }

    @Test
    public void deleteTasksTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2");

        manager.createTask(task);
        int taskId = task.getId();

        assertTrue(manager.getTasks().contains(task), "Задача не появилась в менеджере");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/tasks/" + taskId);

        Thread.sleep(100);

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode(), "Ошибка при удалении задачи");

        assertFalse(manager.getTasks().contains(task), "Задача не была удалена");
        client.close();
    }

    @Test
    public void deleteSubTasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "description");
        manager.createEpic(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask("SubTask 2", "Testing subtask 2", epicId);

        manager.createSubTask(subTask);
        int subTaskId = subTask.getId();

        assertTrue(manager.getSubTasks().contains(subTask), "Подзадача не была создана");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:" + PORT + "/subtasks/" + subTaskId);

        Thread.sleep(100);

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode(), "Ошибка при удалении подзадачи");

        assertFalse(manager.getSubTasks().contains(subTask), "Подзадача не была удалена");
        client.close();
    }

    @Test
    public void deleteEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "description");
        manager.createEpic(epic);
        int epicId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:" + PORT + "/epics/" + epicId);
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());
        client.close();
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "description");
        int epicId = epic.getId();
        SubTask subTask = new SubTask("SubTask", "Testing subtask 2", epicId);
        Task task = new Task("Task", "Testing task 2");
        manager.createEpic(epic);
        manager.createSubTask(subTask);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:" + PORT + "/epics/" + epic.getId());
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, epicResponse.statusCode());

        url = URI.create("http://localhost:" + PORT + "/subtasks/" + subTask.getId());
        HttpRequest subTaskRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> subTaskResponse = client.send(subTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, subTaskResponse.statusCode());

        url = URI.create("http://localhost:" + PORT + "/tasks/" + task.getId());
        HttpRequest taskRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, taskResponse.statusCode());

        url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        assertTrue(responseBody.contains("Epic"), "Ответ сервера не содержит ожидаемых данных");
        assertTrue(responseBody.contains("SubTask"), "Ответ сервера не содержит ожидаемых данных");
        assertTrue(responseBody.contains("Task"), "Ответ сервера не содержит ожидаемых данных");
        client.close();
    }
}
