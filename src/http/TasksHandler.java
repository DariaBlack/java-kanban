package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.exceptions.NotFoundException;
import controllers.interfaces.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(taskManager.getTasks());
            sendText(exchange, response, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(inputStreamReader, Task.class);

            if (!taskManager.addTaskPriority(task)) {
                sendHasInteractions(exchange);
                return;
            }
            taskManager.addTask(task);
            sendText(exchange, "Задача успешно добавлена", 201);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllTasks();
            sendText(exchange, "Все задачи успешно удалены", 200);
        } catch (Exception e) {
            sendText(exchange, "Internal Server Error", 500);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetTasks(exchange);
                break;
            case "POST":
                handlePostTask(exchange);
                break;
            case "DELETE":
                handleDeleteTasks(exchange);
                break;
            default:
                sendText(exchange, "Неизвестный метод", 405);
        }
    }
}