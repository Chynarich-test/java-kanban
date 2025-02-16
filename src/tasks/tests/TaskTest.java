package tasks.tests;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void taskEqualityById() {
        Task task1 = new Task("Task1", "Description1", 1, Status.NEW);
        Task task2 = new Task("Task2", "Description2", 1, Status.DONE);
        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }
}