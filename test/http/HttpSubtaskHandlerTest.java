package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import http.tokens.SubtaskListTypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

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

public class HttpSubtaskHandlerTest {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private Epic testEpic;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        client = HttpClient.newHttpClient();

        testEpic = new Epic("Test Epic", "D", Status.NEW);
        manager.addEpic(testEpic);
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void shouldGetSubtasksWhenNoSubtasksReturnsEmptyList() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8081/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals("[]", response.body(), "Тело ответа должно быть пустым списком JSON");
    }

    @Test
    void shouldGetSubtasksWhenSubtasksExistReturnsSubtasksList() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask 1", "D", Status.NEW, 0);
        manager.addSubtaskToEpic(subtask);

        URI url = URI.create("http://localhost:8081/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertNotNull(subtasksFromResponse, "Список подзадач не должен быть null");
        assertEquals(1, subtasksFromResponse.size(), "В списке должна быть одна подзадача");
        assertEquals(subtask.getName(), subtasksFromResponse.getFirst().getName(), "Имя подзадачи в ответе не совпадает");
        assertEquals(0, subtasksFromResponse.getFirst().getIdEpic(), "ID эпика подзадачи в ответе не совпадает");
        assertEquals(1, subtasksFromResponse.getFirst().getId(), "ID подзадачи в ответе должен быть 1");
    }

    @Test
    void shouldGetSubtaskByIdWhenSubtaskExistsReturnsSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask 1", "D", Status.NEW, 0);
        manager.addSubtaskToEpic(subtask);

        URI url = URI.create("http://localhost:8081/subtasks/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(subtaskFromResponse, "Подзадача не должна быть null");
        assertEquals(1, subtaskFromResponse.getId(), "ID подзадачи не совпадает");
        assertEquals(subtask.getName(), subtaskFromResponse.getName(), "Имя подзадачи не совпадает");
        assertEquals(0, subtaskFromResponse.getIdEpic(), "ID эпика подзадачи не совпадает");
    }

    @Test
    void shouldCreateSubtaskWhenValidSubtaskPostedReturns201AndSubtaskIsAdded() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("New Sub", "D", Status.NEW, 0);
        String subtaskJson = gson.toJson(newSubtask);

        URI url = URI.create("http://localhost:8081/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа при создании подзадачи должен быть 201");

        List<Subtask> subtasksInManager = manager.getSubtasks();
        assertEquals(1, subtasksInManager.size(), "В менеджере должна быть одна подзадача после добавления");

        Subtask addedSubtask = subtasksInManager.getFirst();
        assertNotNull(addedSubtask, "Добавленная подзадача не должна быть null");
        assertEquals(1, addedSubtask.getId(), "ID добавленной подзадачи должен быть 1");
        assertEquals(newSubtask.getName(), addedSubtask.getName(), "Имя подзадачи не совпадает");
        assertEquals(newSubtask.getDescription(), addedSubtask.getDescription(), "Описание подзадачи не совпадает");
        assertEquals(newSubtask.getStatus(), addedSubtask.getStatus(), "Статус подзадачи не совпадает");
        assertEquals(0, addedSubtask.getIdEpic(), "ID эпика подзадачи не совпадает");
    }

    @Test
    void shouldUpdateSubtaskWhenValidSubtaskPostedReturns200() throws IOException, InterruptedException {
        Subtask originalSubtask = new Subtask("Subtask 1", "D", Status.NEW, 0);
        manager.addSubtaskToEpic(originalSubtask);


        Subtask updatedSubtask = new Subtask("Subtask 1 Updated", "D updated", 1, Status.IN_PROGRESS, 0);
        String subtaskJson = gson.toJson(updatedSubtask);

        URI url = URI.create("http://localhost:8081/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа при обновлении подзадачи должен быть 200");

        List<Subtask> subtasksInManager = manager.getSubtasks();
        assertEquals(1, subtasksInManager.size(), "В менеджере должна остаться одна подзадача после обновления");

        Subtask subtaskFromManager = manager.getSubtaskAnID(1);
        assertNotNull(subtaskFromManager, "Обновленная подзадача не должна быть null");
        assertEquals(updatedSubtask.getName(), subtaskFromManager.getName(), "Имя подзадачи должно обновиться");
        assertEquals(updatedSubtask.getDescription(), subtaskFromManager.getDescription(), "Описание подзадачи должно обновиться");
    }


    @Test
    void shouldDeleteSubtaskWhenValidIdProvidedReturns200AndSubtaskIsRemoved() throws IOException, InterruptedException {
        Subtask subtaskToDelete = new Subtask("SubtaskToDelete", "D", Status.IN_PROGRESS, 0);
        manager.addSubtaskToEpic(subtaskToDelete);

        assertNotNull(manager.getSubtaskAnID(1), "Подзадача должна существовать перед удалением");
        assertEquals(1, manager.getSubtasks().size(), "В менеджере должна быть 1 подзадача перед удалением");

        URI url = URI.create("http://localhost:8081/subtasks/" + 1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа при удалении подзадачи должен быть 200");

        assertNull(manager.getSubtaskAnID(1), "Подзадача должна быть null после удаления");
        assertTrue(manager.getSubtasks().isEmpty(), "Список подзадач в менеджере должен быть пустым после удаления");
    }
}
