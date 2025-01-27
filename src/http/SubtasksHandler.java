package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.exceptions.NotFoundException;
import controllers.interfaces.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager tasksManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.tasksManager = taskManager;
        this.gson = gson;
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(tasksManager.getSubtasks());
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(inputStreamReader, Subtask.class);

            if (!tasksManager.addTaskPriority(subtask)) {
                sendHasInteractions(exchange);
                return;
            }

            tasksManager.addSubtask(subtask);
            sendText(exchange, "Подзадача успешно добавлена", 201);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        try {
            tasksManager.deleteAllSubtasks();
            sendText(exchange, "Все подзадачи успешно удалены", 200);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetSubtasks(exchange);
                break;
            case "POST":
                handlePostSubtask(exchange);
                break;
            case "DELETE":
                handleDeleteSubtasks(exchange);
                break;
            default:
                sendText(exchange, "Неизвестный метод", 405);
        }
    }
}