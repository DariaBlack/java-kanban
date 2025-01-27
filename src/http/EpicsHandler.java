package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.exceptions.NotFoundException;
import controllers.interfaces.TaskManager;
import model.Epic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(taskManager.getEpics());
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(inputStreamReader, Epic.class);

            if (!taskManager.addTaskPriority(epic)) {
                sendHasInteractions(exchange);
                return;
            }

            taskManager.addEpic(epic);
            sendText(exchange, "Эпик успешно добавлен", 201);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllEpics();
            sendText(exchange, "Все эпики успешно удалены", 200);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetEpics(exchange);
                break;
            case "POST":
                handlePostEpic(exchange);
                break;
            case "DELETE":
                handleDeleteEpics(exchange);
                break;
            default:
                sendText(exchange, "Неизвестный метод", 405);
        }
    }
}