package manager.tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    void addAndFindTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task", "Description", Status.NEW);
        manager.addTask(task);
        assertNotNull(manager.getTaskAnID(manager.getTasks().getFirst().getID()),
                "Задача должна быть найдена");

        Epic epic = new Epic("Epic", "Description", Status.NEW);
        manager.addEpic(epic);
        assertNotNull(manager.getEpicAnID(manager.getEpics().getFirst().getID()), "Эпик должен быть найден");

        Subtask subtask = new Subtask("Subtask", "Description",
                Status.NEW, manager.getEpics().getFirst().getID());
        manager.addSubtaskToEpic(subtask);
        assertNotNull(manager.getSubtaskAnID(manager.getSubtasks().getFirst().getID()),
                "Подзадача должна быть найдена");
    }

    @Test
    void noIdConflict() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task1", "Desc", 0, Status.NEW);
        manager.addTask(task1);

        Task task2 = new Task("Task2", "Desc", Status.NEW);
        manager.addTask(task2);
        assertNotEquals(manager.getTasks().getFirst().getID(), manager.getTasks().getLast().getID(),
                "ID задач не должны конфликтовать");
    }

    @Test
    void taskImmutability() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task originalTask = new Task("Original", "Desc", Status.NEW);
        manager.addTask(originalTask);

        originalTask.setName("Modified");
        Task storedTask = manager.getTaskAnID(0);

        assert storedTask != null;
        assertEquals("Original", storedTask.getName(), "Имя задачи не должно измениться");
    }
}