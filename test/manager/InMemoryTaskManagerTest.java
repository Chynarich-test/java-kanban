package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    void addAndFindTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task", "Description", Status.NEW);
        manager.addTask(task);
        assertNotNull(manager.getTaskAnID(manager.getTasks().getFirst().getId()),
                "Задача должна быть найдена");

        Epic epic = new Epic("Epic", "Description", Status.NEW);
        manager.addEpic(epic);
        assertNotNull(manager.getEpicAnID(manager.getEpics().getFirst().getId()), "Эпик должен быть найден");

        Subtask subtask = new Subtask("Subtask", "Description",
                Status.NEW, manager.getEpics().getFirst().getId());
        manager.addSubtaskToEpic(subtask);
        assertNotNull(manager.getSubtaskAnID(manager.getSubtasks().getFirst().getId()),
                "Подзадача должна быть найдена");
    }

    @Test
    void noIdConflict() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task1", "Desc", 0, Status.NEW);
        manager.addTask(task1);

        Task task2 = new Task("Task2", "Desc", Status.NEW);
        manager.addTask(task2);
        assertNotEquals(manager.getTasks().getFirst().getId(), manager.getTasks().getLast().getId(),
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

    @Test
    void deleteTask_RemovesItFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.addTask(task);
        long taskId = manager.getTasks().getFirst().getId();
        manager.getTaskAnID(taskId);
        manager.deleteTaskAnID(taskId);
        assertTrue(manager.getHistory().isEmpty(), "Задача должна удалиться из истории");
    }

    @Test
    void deleteAllTasks_RemovesThemFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        Task task2 = new Task("Task2", "Desc", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskAnID(0);
        manager.getTaskAnID(1);
        manager.deleteAllTasks();
        assertTrue(manager.getHistory().isEmpty(), "Все задачи должны удалиться из истории");
    }

    @Test
    void deleteEpic_RemovesEpicAndSubtasksFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, 0);
        manager.addSubtaskToEpic(subtask);

        manager.getEpicAnID(0);
        manager.getSubtaskAnID(1);
        manager.deleteTaskAnID(0);
        System.out.println(manager.getHistory());
        assertTrue(manager.getHistory().isEmpty(), "Все задачи должны удалиться из истории");
    }

    @Test
    void deleteSubtask_RemovesItFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, 0);
        manager.addSubtaskToEpic(subtask);
        manager.getSubtaskAnID(1);
        manager.getEpicAnID(0);
        manager.deleteTaskAnID(1);


        List<Task> history = manager.getHistory();
        assertEquals(1, history.size(), "В истории должен остаться только один элемент");
        assertEquals(new Task("Epic", "Desc", Status.NEW), history.get(0), "В истории должен остаться только эпик");
    }

}