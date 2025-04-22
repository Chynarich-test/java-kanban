package http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exceptions.ManagerSaveException;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.List;
import java.util.function.Function;

public abstract class DefaultTypeHandler<T extends Task> extends DefaultCallHandler {

    public DefaultTypeHandler(TaskManager taskManager, String pathName) {
        super(taskManager, pathName);
    }

    protected abstract Class<T> getTaskType();

    protected abstract void addTaskSpecific(T task) throws Exception;

    protected abstract void updateTaskSpecific(T task) throws Exception;

    protected abstract Function<Integer, T> taskGetterById();

    protected abstract List<T> getTasksSpecific();

    @Override
    protected void handleDeleteOne(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteTaskAnID(getId(exchange).get());
            writeResponse(exchange, 200);
        } catch (ManagerSaveException e) {
            writeResponse(exchange, "Internal Server Error", 500);
        }
    }

    @Override
    protected void handlePostOne(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        T task;
        task = gson.fromJson(requestBody, getTaskType());
        if (task == null) {
            writeResponse(exchange, "Запрос пустой", 400);
            return;
        }
        try {
            if (task.getId() != null) {
                updateTaskSpecific(task);
                writeResponse(exchange, 200);
            } else {
                try {
                    addTaskSpecific(task);
                    writeResponse(exchange, 201);
                } catch (CharacterCodingException e) {
                    writeResponse(exchange, "Not Acceptable", 406);
                } catch (Exception e) {
                    writeResponse(exchange, "Internal Server Error", 500);
                }
            }
        } catch (Exception e) {
            writeResponse(exchange, "Internal Server Error", 500);
        }

    }

    @Override
    protected void handleGetOne(HttpExchange exchange) throws IOException {
        T task = getId(exchange).map(taskGetterById()).orElse(null);
        if (task != null) {
            writeResponse(exchange, task, 200);
        } else {
            writeResponse(exchange, "Not Found", 404);
        }
    }

    @Override
    protected void handleGetMany(HttpExchange exchange) throws IOException {
        writeResponse(exchange, getTasksSpecific(), 200);
    }
}
