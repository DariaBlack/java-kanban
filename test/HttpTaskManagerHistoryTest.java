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

public class HttpTaskManagerHistoryTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("task", "description task", Status.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(5);
        manager.addTask(task);
        manager.getTask(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/History");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history, "История не должна быть null");
        assertEquals(1, history.length, "Количество задач в истории должно быть = 1");
        assertEquals("task", history[0].getName(), "Некорректное имя задачи в истории");
    }
}