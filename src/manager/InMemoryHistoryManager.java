package manager;


import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Long, Task> historyDB = new LinkedHashMap<>();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyDB.values());
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        remove(task.getId());
        historyDB.put(task.getId(), new Task(task));
    }


    @Override
    public void remove(long id) {
        historyDB.remove(id);
    }
}
