import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import controllers.Managers;
import controllers.interfaces.TaskManager;
import http.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.taskManager = Managers.getDefault();
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/History", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        server.start();
        System.out.println("Сервер начал работу на порту " + PORT + "!");
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }
}