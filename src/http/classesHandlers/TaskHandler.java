package http.classesHandlers;

import http.DefaultTypeHandler;
import manager.TaskManager;
import tasks.Task;

import java.util.List;
import java.util.function.Function;


public class TaskHandler extends DefaultTypeHandler<Task> {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager, "tasks");
    }

    @Override
    protected Class<Task> getTaskType() {
        return Task.class;
    }

    @Override
    protected void addTaskSpecific(Task task) throws Exception {
        taskManager.addTask(task);
    }

    @Override
    protected void updateTaskSpecific(Task task) throws Exception {
        taskManager.updateTask(task);
    }

    @Override
    protected Function<Integer, Task> taskGetterById() {
        return taskManager::getTaskAnID;
    }

    @Override
    protected List<Task> getTasksSpecific() {
        return taskManager.getTasks();
    }
}
