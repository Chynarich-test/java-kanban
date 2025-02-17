package tests.tasks;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void taskEqualityById() {
        Subtask task1 = new Subtask ("Subtask1", "Description1", 1, Status.NEW, 1);
        Subtask  task2 = new Subtask ("Subtask2", "Description2", 1, Status.DONE, 2);
        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }


}