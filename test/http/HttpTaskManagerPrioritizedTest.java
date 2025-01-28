package http;

import com.google.gson.Gson;
import controllers.Managers;
import controllers.interfaces.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class HttpTaskManagerPrioritizedTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerPrioritizedTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("task", "description task", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(5);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritizedTasks, "Задачи в приоритете должны существовать");
        assertEquals(1, prioritizedTasks.length, "Количество задач в приоритете должно быть = 1");
        assertEquals("task", prioritizedTasks[0].getName(), "Некорректное имя задачи");
    }
}