package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class BaseHttpHandler implements HttpHandler {
    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {

    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {

    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }
}