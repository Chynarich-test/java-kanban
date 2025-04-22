package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    protected void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        byte[] resp = responseString.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().set("Content-Type", "text/plain;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void writeResponse(HttpExchange exchange, Object responseObject, int responseCode) throws IOException {
        String jsonResponse = gson.toJson(responseObject);
        byte[] resp = jsonResponse.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void writeResponse(HttpExchange exchange, int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode, -1);
        exchange.close();
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
//        if(pathParts.length != 3){
//            return Optional.empty();
//        }
        String strID = pathParts[2];
        try {
            return Optional.of(Integer.parseInt(strID));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
