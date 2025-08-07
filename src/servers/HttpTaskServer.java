package servers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HttpTaskServer implements HttpHandler {
    protected static final int ERROR_ONE = -1;
    private static final int PORT = 8080;
    protected HttpServer server;
    protected TaskManager manager;
    private final Gson json = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(Duration.class, new DurationSerializer())
            .create();

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.manager = manager;
    }

    static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return new JsonPrimitive(src.format(FORMATTER));
        }
    }

    static class DurationSerializer implements JsonSerializer<Duration> {
        @Override
        public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return new JsonPrimitive(src.toMinutes());
        }
    }

    public void startHttpServer() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        server.start();
    }

    public void stopHttpServer() {
        server.stop(1);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }

    public HttpServer getServer() {
        return server;
    }

    public Gson getJson() {
        return json;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);

        server.getServer().createContext("/tasks", server);
        server.getServer().createContext("/subtasks", server);
        server.getServer().createContext("/epics", server);
        server.getServer().createContext("/history", server);
        server.getServer().createContext("/prioritized", server);

        server.startHttpServer(); // запускаем сервер

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String metod = httpExchange.getRequestMethod();
        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        switch (metod) {
            case "GET": {
                if (pathParts.length == 2) {
                    switch (pathParts[1]) {
                        case "prioritized" -> getPrioritized(httpExchange);
                        case "history" -> getHistory(httpExchange);
                        case "tasks", "subtasks", "epics" -> getTasks(httpExchange,pathParts[1]);
                        default -> BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                    }
                } else if (pathParts.length == 3) {
                    try {
                        int idTask = Integer.parseInt(pathParts[2]);
                        getTasksForId(httpExchange,idTask,pathParts[1]);
                    } catch (NumberFormatException e) {
                        BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                    }
                }
                break;
            }
            case "POST": {
                if (pathParts.length == 2) {
                    switch (pathParts[1]) {
                        case "tasks", "subtasks", "epics" -> createTask(httpExchange,pathParts[1]);
                        default -> BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                    }
                } else if (pathParts.length == 3) {
                    try {
                        int idTask = Integer.parseInt(pathParts[2]);
                        switch (pathParts[1]) {
                            case "tasks", "subtasks" -> updateTask(httpExchange,idTask,pathParts[1]);
                            default -> BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                        }
                    } catch (NumberFormatException e) {
                        BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                    }
                }
                break;
            }
            case "DELETE": {
                if (pathParts.length == 3) {
                    try {
                        int idTask = Integer.parseInt(pathParts[2]);
                        deleteTaskForId(httpExchange,idTask,pathParts[1]);
                    } catch (NumberFormatException e) {
                        BaseHttpHandler.sendNotFound(httpExchange, "Not Found!");
                    }
                }
            }
            default: {
                BaseHttpHandler.sendHasOverlaps(httpExchange,"Not Acceptable");
            }
        }
    }

    public void createTask(HttpExchange httpExchange, String type) throws IOException {
        String contentType = httpExchange.getRequestHeaders().getFirst("Content-Type");
        if (!"application/json".equals(contentType)) {
            BaseHttpHandler.sendHasOverlaps(httpExchange,"Not Acceptable");
        }
        try (InputStream inputStream = httpExchange.getRequestBody()) {

            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            if (requestBody.isEmpty()) {
                BaseHttpHandler.sendHasOverlaps(httpExchange,"Not Acceptable");
            }
            switch (type) {
                case "tasks" -> {
                    try {
                        Task task;
                        JsonObject jsonObject = json.fromJson(requestBody, JsonObject.class);

                        String name = jsonObject.get("name").getAsString();
                        String description = jsonObject.get("description").getAsString();
                        try {
                            Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
                            LocalDateTime localDateTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
                            task = new Task(name, description, duration, localDateTime);
                        } catch (NullPointerException | DateTimeParseException e) {
                            task = new Task(name, description);
                        }

                        int id = manager.createTask(task);
                        if (id == ERROR_ONE) {
                            BaseHttpHandler.sendHasOverlaps(httpExchange, "Not Acceptable");
                        } else {
                            BaseHttpHandler.send(httpExchange);
                        }
                    } catch (NullPointerException e) {
                        BaseHttpHandler.sendHasOverlaps(httpExchange, "Not Acceptable");
                        throw new NullPointerException();
                    }
                }
                case "subtasks" -> {
                    try {
                        SubTask subTask;
                        JsonObject jsonObject = json.fromJson(requestBody, JsonObject.class);

                        String name = jsonObject.get("name").getAsString();
                        String description = jsonObject.get("description").getAsString();
                        int epicId = jsonObject.get("epicId").getAsInt();
                        try {
                            Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
                            LocalDateTime localDateTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
                            subTask = new SubTask(name, description, duration, localDateTime, epicId);
                        } catch (NullPointerException | DateTimeParseException e) {
                            subTask = new SubTask(name, description, epicId);
                        }

                        int id = manager.createSubTask(subTask);
                        if (id == ERROR_ONE) {
                            BaseHttpHandler.sendHasOverlaps(httpExchange, "Not Acceptable");
                        } else {
                            BaseHttpHandler.send(httpExchange);
                        }
                    } catch (NullPointerException e) {
                        BaseHttpHandler.sendHasOverlaps(httpExchange, "Not Acceptable");
                        throw new NullPointerException();
                    }
                }
                case "epics" -> {
                    try {
                        Epic epic;
                        JsonObject jsonObject = json.fromJson(requestBody, JsonObject.class);

                        String name = jsonObject.get("name").getAsString();
                        String description = jsonObject.get("description").getAsString();
                        epic = new Epic(name, description);
                        int id = manager.createEpic(epic);
                        if (id == ERROR_ONE) {
                            BaseHttpHandler.sendHasOverlaps(httpExchange, "Not Acceptable");
                        } else {
                            BaseHttpHandler.send(httpExchange);
                        }
                    } catch (NullPointerException e) {
                        BaseHttpHandler.sendHasOverlaps(httpExchange, "Not Acceptable");
                        throw new NullPointerException();
                    }
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            BaseHttpHandler.sendHasOverlaps(httpExchange,"Not Acceptable");
        }
    }

    public void updateTask(HttpExchange httpExchange, int id, String type) throws IOException {
        String contentType = httpExchange.getRequestHeaders().getFirst("Content-Type");
        if (!"application/json".equals(contentType)) {
            BaseHttpHandler.sendHasOverlaps(httpExchange,"Not Acceptable");
        }
        try (InputStream inputStream = httpExchange.getRequestBody()) {

            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            if (requestBody.isEmpty()) {
                BaseHttpHandler.sendHasOverlaps(httpExchange,"Not Acceptable");
            }
            switch (type) {
                case "tasks" -> {
                    try {
                        if (manager.getTask(id) != null) {
                            Task task;
                            JsonObject jsonObject = json.fromJson(requestBody, JsonObject.class);

                            String name = jsonObject.get("name").getAsString();
                            String description = jsonObject.get("description").getAsString();
                            try {
                                Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
                                LocalDateTime localDateTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
                                task = new Task(name, description, duration, localDateTime);
                            } catch (NullPointerException | DateTimeParseException e) {
                                task = new Task(name, description);
                            }

                            int idTask = manager.updateTask(task);
                            if (idTask == ERROR_ONE) {
                                BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                            } else {
                                BaseHttpHandler.send(httpExchange);
                            }
                        } else {
                            BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                        }
                    } catch (NullPointerException e) {
                        BaseHttpHandler.sendHasOverlaps(httpExchange, "Not Acceptable");
                        throw new NullPointerException();
                    }
                }
                case "subtasks" -> {
                    try {
                        if (manager.getSubTask(id) != null) {
                            SubTask subTask;
                            JsonObject jsonObject = json.fromJson(requestBody, JsonObject.class);

                            String name = jsonObject.get("name").getAsString();
                            String description = jsonObject.get("description").getAsString();
                            int epicId = jsonObject.get("epicId").getAsInt();
                            try {
                                Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
                                LocalDateTime localDateTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
                                subTask = new SubTask(name, description, duration, localDateTime, epicId);
                            } catch (NullPointerException | DateTimeParseException e) {
                                subTask = new SubTask(name, description, epicId);
                            }

                            int idSubTask = manager.createSubTask(subTask);
                            if (idSubTask == ERROR_ONE) {
                                BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                            } else {
                                BaseHttpHandler.send(httpExchange);
                            }
                        } else {
                            BaseHttpHandler.sendNotFound(httpExchange, "Not Found");
                        }
                    } catch (NullPointerException e) {
                        BaseHttpHandler.sendHasOverlaps(httpExchange, "Not Acceptable");
                        throw new NullPointerException();
                    }
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            BaseHttpHandler.sendHasOverlaps(httpExchange,"Not Acceptable");
        }
    }

    public void getTasks(HttpExchange httpExchange, String type) throws IOException {
        switch (type) {
            case "tasks" -> {
                StringBuilder jsonTask = new StringBuilder();
                for (Task task : manager.getTasks()) {
                    jsonTask.append(getJson().toJson(task));
                }
                BaseHttpHandler.sendText(httpExchange, jsonTask.toString());
            }
            case "subtasks" -> {
                StringBuilder jsonSubTask = new StringBuilder();
                for (Task task : manager.getSubTasks()) {
                    jsonSubTask.append(getJson().toJson(task));
                }
                BaseHttpHandler.sendText(httpExchange, jsonSubTask.toString());
            }
            case "epics" -> {
                StringBuilder jsonEpics = new StringBuilder();
                for (Task task : manager.getEpics()) {
                    jsonEpics.append(getJson().toJson(task));
                }
                BaseHttpHandler.sendText(httpExchange, jsonEpics.toString());
            }
            default -> BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
        }

    }

    public void getTasksForId(HttpExchange httpExchange, int id, String type) throws IOException {
        switch (type) {
            case "tasks" -> {
                if (manager.getTask(id) != null) {
                    BaseHttpHandler.sendText(httpExchange, getJson().toJson(manager.getTask(id)));
                } else {
                    BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
                }
            }
            case "subtasks" -> {
                if (manager.getSubTask(id) != null) {
                    BaseHttpHandler.sendText(httpExchange, getJson().toJson(manager.getSubTask(id)));
                } else {
                    BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
                }
            }
            case "epics" -> {
                if (manager.getEpic(id) != null) {
                    BaseHttpHandler.sendText(httpExchange, getJson().toJson(manager.getEpic(id)));
                } else {
                    BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
                }
            }
            default -> BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
        }
    }

    public void deleteTaskForId(HttpExchange httpExchange, int id, String type) throws IOException {
        switch (type) {
            case "tasks" -> {
                if (manager.getTask(id) != null) {
                    manager.deleteTaskForId(id);
                    BaseHttpHandler.sendText(httpExchange,"Task " + id + " is deleted.");
                } else {
                    BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
                }
            }
            case "subtasks" -> {
                if (manager.getSubTask(id) != null) {
                    manager.deleteSubTaskForId(id);
                    BaseHttpHandler.sendText(httpExchange,"Task " + id + " is deleted.");
                } else {
                    BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
                }
            }
            case "epics" -> {
                if (manager.getEpic(id) != null) {
                    manager.deleteEpicForId(id);
                    BaseHttpHandler.sendText(httpExchange,"Task " + id + " is deleted.");
                } else {
                    BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
                }
            }
            default -> BaseHttpHandler.sendNotFound(httpExchange,"Not Found");
        }
    }

    public void getPrioritized(HttpExchange httpExchange) throws IOException {
        StringBuilder jtask = new StringBuilder();
        for (Task task : manager.getPrioritizedTasks()) {
            jtask.append(getJson().toJson(task));
        }
        BaseHttpHandler.sendText(httpExchange, jtask.toString());
    }

    public void getHistory(HttpExchange httpExchange) throws IOException {
        StringBuilder jtask = new StringBuilder();
        for (Task task : manager.getHistory()) {
            jtask.append(getJson().toJson(task));
        }
        BaseHttpHandler.sendText(httpExchange, jtask.toString());
    }
}
