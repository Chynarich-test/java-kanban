package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public abstract class DefaultCallHandler extends BaseHttpHandler implements HttpHandler {
    private final String pathName;

    public DefaultCallHandler(TaskManager taskManager, String pathName) {
        super(taskManager);
        this.pathName = pathName;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);

        switch (endpoint) {
            case GET_MANY: {
                handleGetMany(exchange);
                break;
            }
            case GET_ONE: {
                handleGetOne(exchange);
                break;
            }
            case POST_ONE: {
                handlePostOne(exchange);
                break;
            }
            case DELETE_ONE: {
                handleDeleteOne(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Not Found", 404);
        }
    }

    protected abstract void handleDeleteOne(HttpExchange exchange) throws IOException;

    protected abstract void handlePostOne(HttpExchange exchange) throws IOException;

    protected abstract void handleGetOne(HttpExchange exchange) throws IOException;

    protected abstract void handleGetMany(HttpExchange exchange) throws IOException;

    private Endpoint getEndpoint(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2 && pathParts[1].equals(pathName)) {
                return Endpoint.GET_MANY;
            } else if (pathParts.length == 3) {
                return getId(exchange)
                        .map(id -> Endpoint.GET_ONE)
                        .orElse(Endpoint.UNKNOWN);
            }
            return Endpoint.UNKNOWN;
        } else if (requestMethod.equals("POST") && pathParts.length == 2 && pathParts[1].equals(pathName)) {
            return Endpoint.POST_ONE;
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3) {
            return getId(exchange)
                    .map(id -> Endpoint.DELETE_ONE)
                    .orElse(Endpoint.UNKNOWN);
        }
        return Endpoint.UNKNOWN;
    }

    enum Endpoint { GET_MANY, GET_ONE, POST_ONE, DELETE_ONE, UNKNOWN }
}
