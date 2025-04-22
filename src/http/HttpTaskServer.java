package http;

import com.sun.net.httpserver.HttpServer;
import http.classesHandlers.*;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8081;
    private final HttpServer server;
    private final TaskManager taskManager;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(this.taskManager));
        server.createContext("/subtasks", new SubtaskHandler(this.taskManager));
        server.createContext("/epics", new EpicHandler(this.taskManager));
        server.createContext("/history", new HistoryHandler(this.taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(this.taskManager));
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public static void main(String[] args) throws IOException {
        try {
            final HttpTaskServer taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера");
        }
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }
}
