package tests.manager;

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

    @Test
    void ThereIsAMaximumListSize() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        for(int i = 1; i <= 13; i++){
            Task task = new Task("Task " + i, "Desc", Status.NEW);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size(),
                "Должно быть ограничение по максимальной длине");
        assertEquals("Task 4", historyManager.getHistory().getFirst().getName(),
                "Список должен очищаться с конца");
    }
}