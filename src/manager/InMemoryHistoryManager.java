package manager;


import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

    private final Map<Long, Task> historyDB= new LinkedHashMap<>();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyDB.values());
    }

    @Override
    public void add(Task task){
        if(task == null) return;
        remove(task.getID());
        historyDB.put(task.getID(), new Task(task));
    }


    @Override
    public void remove(long id) {
        historyDB.remove(id);
    }
}
