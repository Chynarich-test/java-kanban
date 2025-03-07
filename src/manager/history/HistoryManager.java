package manager.history;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(long id);

    void remove(List<Task> id);

    List<Task> getHistory();
}
