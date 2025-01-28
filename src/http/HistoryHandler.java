package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.exceptions.NotFoundException;
import controllers.interfaces.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(taskManager.getHistory());
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }
}