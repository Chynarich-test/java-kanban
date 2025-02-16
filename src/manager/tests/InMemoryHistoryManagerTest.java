package manager.tests;

import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void historyPreservesTaskState() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Task", "Desc", Status.NEW);
        historyManager.add(task);

        task.setStatus(Status.DONE);
        Task historyTask = historyManager.getHistory().getFirst();

        assertEquals(Status.NEW, historyTask.getStatus(), "История должна сохранять исходное состояние");
    }
}