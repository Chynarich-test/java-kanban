package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import http.tokens.EpicListTypeToken;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class HttpEpicHandlerTest {
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
    void shouldGetEpicsWhenNoEpicsReturnsEmptyList() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8081/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals("[]", response.body(), "Тело ответа должно быть пустым списком JSON");
    }

    @Test
    void shouldGetEpicsWhenEpicsExistReturnsEpicsList() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "D", 0, Status.NEW);
        manager.addEpic(epic);

        URI url = URI.create("http://localhost:8081/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        List<Epic> epicsFromResponse = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        assertNotNull(epicsFromResponse, "Список эпиков не должен быть null");
        assertEquals(1, epicsFromResponse.size(), "В списке должен быть один эпик");
        assertEquals(epic.getName(), epicsFromResponse.getFirst().getName(), "Имя эпика в ответе не совпадает");
        assertEquals(0, epicsFromResponse.getFirst().getId(), "ID эпика в ответе должен быть 0");
    }

    @Test
    void shouldGetEpicByIdWhenEpicExistsReturnsEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "D", 0, Status.NEW);
        manager.addEpic(epic);

        URI url = URI.create("http://localhost:8081/epics/" + 0);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epicFromResponse, "Эпик не должен быть null");
        assertEquals(0, epicFromResponse.getId(), "ID эпика не совпадает");
        assertEquals(epic.getName(), epicFromResponse.getName(), "Имя эпика не совпадает");
    }

    @Test
    void shouldCreateEpicWhenValidEpicPostedReturns201AndEpicIsAdded() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "D", Status.NEW);
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8081/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа при создании эпика должен быть 201");

        List<Epic> epicsInManager = manager.getEpics();
        assertEquals(1, epicsInManager.size(), "В менеджере должен быть один эпик после добавления");

        Epic addedEpic = epicsInManager.getFirst();
        assertNotNull(addedEpic, "Добавленный эпик не должен быть null");
        assertEquals(0, addedEpic.getId(), "ID добавленного эпика должен быть 0");
        assertEquals(epic.getName(), addedEpic.getName(), "Имя эпика не совпадает");
        assertEquals(epic.getDescription(), addedEpic.getDescription(), "Описание эпика не совпадает");
    }

    @Test
    void shouldUpdateEpicWhenValidEpicPostedReturns200() throws IOException, InterruptedException {
        Epic originalEpic = new Epic("Epic 1", "D", Status.NEW);
        manager.addEpic(originalEpic);

        Epic updatedEpic = new Epic("Epic 1 Updated", "D updated", 0, Status.NEW);
        String epicJson = gson.toJson(updatedEpic);

        URI url = URI.create("http://localhost:8081/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа при обновлении эпика должен быть 200");

        List<Epic> epicsInManager = manager.getEpics();
        assertEquals(1, epicsInManager.size(), "В менеджере должен остаться один эпик после обновления");

        Epic epicFromManager = manager.getEpicAnID(0);
        assertNotNull(epicFromManager, "Обновленный эпик не должен быть null");
        assertEquals(updatedEpic.getName(), epicFromManager.getName(), "Имя эпика должно обновиться");
        assertEquals(updatedEpic.getDescription(), epicFromManager.getDescription(), "Описание эпика должно обновиться");
    }

    @Test
    void shouldDeleteEpicWhenValidIdProvidedReturns200AndEpicIsRemoved() throws IOException, InterruptedException {
        Epic epicToDelete = new Epic("Epic 1", "D", Status.NEW);
        manager.addEpic(epicToDelete);

        Subtask subtask = new Subtask("Sub under epic", "D", Status.NEW, 0);
        manager.addSubtaskToEpic(subtask);

        assertNotNull(manager.getEpicAnID(0), "Эпик должен существовать перед удалением");
        assertNotNull(manager.getSubtaskAnID(1), "Сабтаск должен существовать перед удалением эпика");
        assertEquals(1, manager.getEpics().size(), "В менеджере должен быть 1 эпик перед удалением");
        assertEquals(1, manager.getSubtasks().size(), "В менеджере должен быть 1 сабтаск перед удалением");


        URI url = URI.create("http://localhost:8081/epics/" + 0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, response.statusCode(), "Код ответа при удалении эпика должен быть 200");

        assertNull(manager.getEpicAnID(0), "Эпик должен быть null после удаления");
        assertTrue(manager.getEpics().isEmpty(), "Список эпиков в менеджере должен быть пустым после удаления");
        assertNull(manager.getSubtaskAnID(1), "Сабтаск эпика должен быть null после удаления эпика");
        assertTrue(manager.getSubtasks().isEmpty(), "Список сабтасков в менеджере должен быть пустым после удаления эпика");
    }

    @Test
    void shouldGetEpicSubtasksWhenEpicAndSubtasksExistReturnsSubtaskList() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic 1", "D", Status.NEW);
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub 1 of Epic", "D1", Status.NEW, 0);
        Subtask sub2 = new Subtask("Sub 2 of Epic", "D2", Status.IN_PROGRESS, 0);
        manager.addSubtaskToEpic(sub1);
        manager.addSubtaskToEpic(sub2);

        URI url = URI.create("http://localhost:8081/epics/" + 0 + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа для подзадач эпика должен быть 200");

        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertNotNull(subtasksFromResponse, "Список подзадач эпика не должен быть null");
        assertEquals(2, subtasksFromResponse.size(), "Должно быть две подзадачи");

        assertTrue(subtasksFromResponse.stream().anyMatch(s -> Objects.equals(s.getId(), 1L) && s.getName().equals(sub1.getName())));
        assertTrue(subtasksFromResponse.stream().anyMatch(s -> Objects.equals(s.getId(), 2L) && s.getName().equals(sub2.getName())));
    }

    @Test
    void shouldReturn404WhenGettingSubtasksForNonExistentEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8081/epics/999/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа должен быть 404");
    }

    @Test
    void shouldReturn400WhenGettingSubtasksWithInvalidEpicIdFormat() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8081/epics/abc/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа должен быть 404");
    }

    @Test
    void shouldReturn200AndEmptyListWhenGettingSubtasksForEpicWithNoSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "D", Status.NEW);
        manager.addEpic(epic);
        int epicId = 0;

        URI url = URI.create("http://localhost:8081/epics/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа для эпика без подзадач должен быть 200");
        assertEquals("[]", response.body(), "Тело ответа должно быть пустым списком JSON");
    }

}
