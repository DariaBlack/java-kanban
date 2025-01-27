package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.interfaces.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getHistory());
        sendText(exchange, response, 200);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            handleGetHistory(exchange);
        } else {
            sendText(exchange, "Неизвестный метод", 405);
        }
    }
}
