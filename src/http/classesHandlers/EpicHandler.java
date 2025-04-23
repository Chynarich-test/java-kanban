package http.classesHandlers;

import com.sun.net.httpserver.HttpExchange;
import http.DefaultTypeHandler;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class EpicHandler extends DefaultTypeHandler<Epic> {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager, "epics");
    }

    @Override
    protected Class<Epic> getTaskType() {
        return Epic.class;
    }

    @Override
    protected void addTaskSpecific(Epic task) throws Exception {
        taskManager.addEpic(task);
    }

    @Override
    protected void updateTaskSpecific(Epic task) throws Exception {
        taskManager.updateEpic(task);
    }

    @Override
    protected Function<Integer, Epic> taskGetterById() {
        return taskManager::getEpicAnID;
    }

    @Override
    protected List<Epic> getTasksSpecific() {
        return taskManager.getEpics();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                List<Subtask> subtasks = getId(exchange).map(taskManager::getSubtasksByID).orElse(null);
                if (subtasks != null) {
                    writeResponse(exchange, subtasks, 200);
                } else {
                    writeResponse(exchange, "Not Found", 404);
                }
                return;
            }
        }

        super.handle(exchange);
    }
}
