package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{

    private final ArrayList<Task> historyDB = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public ArrayList<Task> getHistory() {
        return historyDB;
    }

    public void add(Task task){
        if(task == null) return;
        if(historyDB.size() == MAX_HISTORY_SIZE) historyDB.removeFirst();
        historyDB.add(new Task(task));
    }
}
