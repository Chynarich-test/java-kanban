package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
