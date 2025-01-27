package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.interfaces.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, response, 200);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equals(method)) {
            handleGetPrioritizedTasks(exchange);
        } else {
            sendText(exchange, "Неизвестный метод", 405);
        }
    }
}
