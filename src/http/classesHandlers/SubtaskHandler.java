package http.classesHandlers;

import http.DefaultTypeHandler;
import manager.TaskManager;
import tasks.Subtask;

import java.util.List;
import java.util.function.Function;

public class SubtaskHandler extends DefaultTypeHandler<Subtask> {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager, "subtasks");
    }

    @Override
    protected Class<Subtask> getTaskType() {
        return Subtask.class;
    }

    @Override
    protected void addTaskSpecific(Subtask task) throws Exception {
        taskManager.addSubtaskToEpic(task);
    }

    @Override
    protected void updateTaskSpecific(Subtask task) throws Exception {
        taskManager.updateSubtask(task);
    }

    @Override
    protected Function<Integer, Subtask> taskGetterById() {
        return taskManager::getSubtaskAnID;
    }

    @Override
    protected List<Subtask> getTasksSpecific() {
        return taskManager.getSubtasks();
    }
}
