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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpHistoryHandlerTest {
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
    void shouldGetHistoryWhenHistoryIsEmptyReturnsEmptyList() throws IOException, InterruptedException {

        URI url = URI.create("http://localhost:8081/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200 для пустой истории");
        assertEquals("[]", response.body(), "Тело ответа должно быть пустым списком JSON");
    }

    @Test
    void shouldGetHistoryWhenHistoryHasTasksReturnsTasksInOrder() throws IOException, InterruptedException {

        Task task1 = new Task("Task 1", "D1", Status.NEW);
        Epic epic1 = new Epic("Epic 1", "DE1", Status.NEW);
        Subtask sub1 = new Subtask("Sub 1", "DS1", Status.NEW, 1);
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtaskToEpic(sub1);

        manager.getTaskAnID(0);
        manager.getSubtaskAnID(2);
        manager.getEpicAnID(1);


        URI url = URI.create("http://localhost:8081/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200 для истории с задачами");

        List<Task> historyFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(historyFromResponse, "Список истории не должен быть null");
        assertEquals(3, historyFromResponse.size(), "В истории должно быть 3 элемента");


        assertEquals(0, historyFromResponse.get(0).getId(), "Первый элемент истории должен быть Task 1 (ID 0)");
        assertEquals(2, historyFromResponse.get(1).getId(), "Второй элемент истории должен быть Subtask 1 (ID 2)");
        assertEquals(1, historyFromResponse.get(2).getId(), "Третий элемент истории должен быть Epic 1 (ID 1)");

        assertEquals(task1.getName(), historyFromResponse.get(0).getName());
        assertEquals(sub1.getName(), historyFromResponse.get(1).getName());
        assertEquals(epic1.getName(), historyFromResponse.get(2).getName());
    }

}
