import com.google.gson.Gson;
import controllers.Managers;
import controllers.interfaces.TaskManager;
import model.Epic;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HttpTaskManagerEpicsTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description epic");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertNotNull(epics, "Эпик должен существовать");
        assertEquals(1, epics.length, "Количество эпиков должно быть = 1");
        assertEquals("epic", epics[0].getName(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description epic");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        assertTrue(epicsFromManager.isEmpty(), "Все эпики должны быть удалены");
    }
}