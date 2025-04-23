package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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

    Object getObjectAnID(long id);

    Task getTaskAnID(long id);

    Epic getEpicAnID(long id);

    Subtask getSubtaskAnID(long id);

    void deleteTaskAnID(long id);

    List<Subtask> getSubtasksByID(long id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
