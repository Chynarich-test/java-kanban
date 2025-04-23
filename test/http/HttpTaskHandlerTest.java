package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import http.tokens.TaskListTypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskHandlerTest {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void shouldGetTasksWhenNoTasksReturnsEmptyList() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8081/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals("[]", response.body(), "Тело ответа должно быть пустым списком JSON");
    }

    @Test
    void shouldGetTasksWhenTasksExistReturnsTasksList() throws IOException, InterruptedException {

        Task task = new Task("1", "D", 0, Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);

        URI url = URI.create("http://localhost:8081/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");


        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(tasksFromResponse, "Список задач не должен быть null");
        assertEquals(1, tasksFromResponse.size(), "В списке должна быть одна задача");
        assertEquals(task.getName(), tasksFromResponse.getFirst().getName(), "Имя задачи в ответе не совпадает");
    }

    @Test
    void shouldGetTaskByIdWhenTaskExistsReturnsTask() throws IOException, InterruptedException {
        Task task = new Task("1", "D", 0, Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.addTask(task);

        URI url = URI.create("http://localhost:8081/tasks/" + 0);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse, "Задача не должна быть null");
        assertEquals(0, taskFromResponse.getId(), "ID задачи не совпадает");
        assertEquals(task.getName(), taskFromResponse.getName(), "Имя задачи не совпадает");
    }

    @Test
    void shouldCreateTaskWhenValidTaskPostedReturns201AndTaskIsAdded() throws IOException, InterruptedException {
        Task newTask = new Task("1", "D", Status.NEW);
        String taskJson = gson.toJson(newTask);

        URI url = URI.create("http://localhost:8081/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа при создании должен быть 201");

        List<Task> tasksInManager = manager.getTasks();
        assertEquals(1, tasksInManager.size(), "В менеджере должна быть одна задача после добавления");

        Task addedTask = tasksInManager.getFirst();
        assertNotNull(addedTask, "Добавленная задача не должна быть null");
        assertEquals(0, addedTask.getId(), "ID добавленной задачи должен быть 0");
        assertEquals(newTask.getName(), addedTask.getName(), "Имя задачи не совпадает");
        assertEquals(newTask.getDescription(), addedTask.getDescription(), "Описание задачи не совпадает");
        assertEquals(newTask.getStatus(), addedTask.getStatus(), "Статус задачи не совпадает");
    }

    @Test
    void shouldCreateTaskWhenValidTaskPostedReturns201AndTaskIsAddedWithID() throws IOException, InterruptedException {
        Task task1 = new Task("1", "D", 0, Status.NEW);
        Task task2 = new Task("2", "D", 1, Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        task1 = new Task("1 NEW", "D NEW", 0, Status.NEW);
        String taskJson = gson.toJson(task1);


        URI url = URI.create("http://localhost:8081/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа при создании должен быть 200");

        List<Task> tasksInManager = manager.getTasks();
        assertEquals(2, tasksInManager.size(), "В менеджере должна быть две задачи после добавления");

        Task addedTask = manager.getTaskAnID(0);
        assertNotNull(addedTask, "Добавленная задача не должна быть null");
        assertEquals(addedTask.getName(), task1.getName(), "Имя задачи не совпадает");
        assertEquals(addedTask.getDescription(), task1.getDescription(), "Описание задачи не совпадает");
    }

    @Test
    void shouldDeleteTask_whenValidIdProvided_returns200AndTaskIsRemoved() throws IOException, InterruptedException {
        Task taskToDelete = new Task("1", "D", Status.IN_PROGRESS);
        manager.addTask(taskToDelete);


        assertNotNull(manager.getTaskAnID(0), "Задача должна существовать перед удалением");
        assertEquals(1, manager.getTasks().size(), "В менеджере должна быть 1 задача перед удалением");

        URI url = URI.create("http://localhost:8081/tasks/" + 0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа при удалении должен быть 200");

        assertNull(manager.getTaskAnID(0), "Задача должна быть null после удаления");
        assertTrue(manager.getTasks().isEmpty(), "Список задач в менеджере должен быть пустым после удаления");
    }
}


