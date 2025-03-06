package test.manager;

import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Desc 1", 1, Status.NEW);
        task2 = new Task("Task 2", "Desc 2", 2, Status.IN_PROGRESS);
        task3 = new Task("Task 3", "Desc 3", 3, Status.DONE);
    }

    @Test
    void historyPreservesTaskState() {
        historyManager.add(task1);
        task1.setStatus(Status.DONE);
        Task historyTask = historyManager.getHistory().getFirst();

        assertEquals(Status.NEW, historyTask.getStatus(), "История должна сохранять исходное состояние");
    }

    @Test
    void shouldAddTasksToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size(), "История должна содержать 3 задачи");
        assertIterableEquals(List.of(task1, task2, task3), historyManager.getHistory(), "Порядок задач нарушен");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        assertEquals(1, historyManager.getHistory().size(), "История должна содержать 1 задачу");
        assertFalse(historyManager.getHistory().contains(task1), "Задача 1 должна быть удалена");
    }

    @Test
    void shouldMaintainInsertionOrderAfterRepeatedAdd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // Повторное добавление

        assertIterableEquals(List.of(task2, task1), historyManager.getHistory(),
                "При повторном добавлении задача должна переместиться в конец");
    }

    @Test
    void shouldHandleEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой");

        historyManager.add(task1);
        historyManager.remove(1);

        assertTrue(historyManager.getHistory().isEmpty(), "История должна стать пустой после удаления");
    }
}