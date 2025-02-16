package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{

    private final ArrayList<Task> historyDB = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return historyDB;
    }

    public void add(Task task){
        if(historyDB.size() == 10) historyDB.removeFirst();
        historyDB.add(new Task(task));
    }
}
