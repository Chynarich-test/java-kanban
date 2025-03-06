package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtaskToEpic(Subtask subtask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Object getObjectAnID(long ID);

    Task getTaskAnID(long ID);

    Epic getEpicAnID(long ID);

    Subtask getSubtaskAnID(long ID);

    void deleteTaskAnID(long ID);

    ArrayList<Subtask> getSubtasksByID(long ID);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getHistory();
}
