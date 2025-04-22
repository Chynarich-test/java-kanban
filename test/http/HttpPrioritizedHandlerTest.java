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
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class HttpPrioritizedHandlerTest {
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
    void shouldGetPrioritizedTasksWhenListIsEmptyReturnsEmptyList() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8081/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200 для пустого списка приоритетов");
        assertEquals("[]", response.body(), "Тело ответа должно быть пустым списком JSON");
    }

    @Test
    void shouldGetPrioritizedTasksWhenTasksExistReturnsSortedList() throws IOException, InterruptedException {

        LocalDateTime now = LocalDateTime.now();
        Task taskLater = new Task("Task Later", "Starts later", 0, Status.NEW, Duration.ofHours(1), now.plusHours(2));
        Task taskEarlier = new Task("Task Earlier", "Starts earlier", 1, Status.NEW, Duration.ofMinutes(30), now.plusHours(1));
        Task taskNoTime = new Task("Task No Time", "No start time", 2, Status.NEW);
        Epic epic = new Epic("Epic", "Epic Desc", 3, Status.NEW);
        Subtask subWithTime = new Subtask("Sub With Time", "Sub desc", 4, Status.NEW, 3, Duration.ofMinutes(15), now.plusMinutes(90));

        manager.addTask(taskLater);
        manager.addTask(taskEarlier);
        manager.addTask(taskNoTime);
        manager.addEpic(epic);
        manager.addSubtaskToEpic(subWithTime);


        URI url = URI.create("http://localhost:8081/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200 для списка приоритетов");

        List<Task> prioritizedFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(prioritizedFromResponse, "Список приоритетных задач не должен быть null");

        assertEquals(3, prioritizedFromResponse.size(), "В списке должно быть 3 задачи (с временем начала)");


        assertEquals(1, prioritizedFromResponse.get(0).getId(), "Первая задача должна быть taskEarlier (ID 1)");
        assertEquals(4, prioritizedFromResponse.get(1).getId(), "Вторая задача должна быть subWithTime (ID 4)");
        assertEquals(0, prioritizedFromResponse.get(2).getId(), "Третья задача должна быть taskLater (ID 0)");


        assertTrue(prioritizedFromResponse.stream().noneMatch(t -> Objects.equals(t.getId(), 2L)), "Задачи без времени не должно быть в списке");
        assertTrue(prioritizedFromResponse.stream().noneMatch(t -> Objects.equals(t.getId(), 3L)), "Эпика не должно быть в списке");
    }
}
