package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;


    @Test
    void epicStatusAllNewSubtasks() {
        Epic epic = new Epic("Эпик с подзадачами", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("1", "D", Status.NEW, 0);
        Subtask sub2 = new Subtask("2", "D", Status.NEW, 0);
        taskManager.addSubtaskToEpic(sub1);
        taskManager.addSubtaskToEpic(sub2);

        assertEquals(Status.NEW, taskManager.getEpicAnID(0).getStatus(),
                "Статус эпика должен быть NEW, если все подзадачи NEW");
    }

    @Test
    void epicStatusAllDoneSubtasks() {
        Epic epic = new Epic("Эпик с подзадачами", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("1", "D", Status.DONE, 0);
        Subtask sub2 = new Subtask("2", "D", Status.DONE, 0);
        taskManager.addSubtaskToEpic(sub1);
        taskManager.addSubtaskToEpic(sub2);

        assertEquals(Status.DONE, taskManager.getEpicAnID(0).getStatus(),
                "Статус эпика должен быть DONE, если все подзадачи DONE");
    }

    @Test
    void epicStatusNewAndDoneSubtasks() {
        Epic epic = new Epic("Эпик с подзадачами", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("1", "D", Status.NEW, 0);
        Subtask sub2 = new Subtask("2", "D", Status.DONE, 0);
        taskManager.addSubtaskToEpic(sub1);
        taskManager.addSubtaskToEpic(sub2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicAnID(0).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если подзадачи NEW и DONE");
    }

    @Test
    void epicStatusInProgressSubtasks() {
        Epic epic = new Epic("Эпик с подзадачами", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("1", "D", Status.IN_PROGRESS, 0);
        Subtask sub2 = new Subtask("2", "D", Status.IN_PROGRESS, 0);
        taskManager.addSubtaskToEpic(sub1);
        taskManager.addSubtaskToEpic(sub2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicAnID(0).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если все подзадачи IN_PROGRESS");

        taskManager.deleteTaskAnID(1);
        taskManager.deleteTaskAnID(2);

        assertEquals(Status.NEW, taskManager.getEpicAnID(0).getStatus(), "Статус эпика должен стать NEW после удаления всех подзадач");
    }

    @Test
    void hasTimeConflict_NoTasks_ReturnsFalse() {
        assertFalse(taskManager.hasTimeConflict(), "Пустой список задач нет конфликтов");
    }

    @Test
    void hasTimeConflict_SingleTask_ReturnsFalse() {
        Task task = new Task("Task", "Desc", 0, Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        assertFalse(taskManager.hasTimeConflict());
    }

    @Test
    void hasTimeConflict_NonOverlapping_ReturnsFalse() {
        Task task1 = new Task("1", "D", 0, Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task2 = new Task("2", "D", 1, Status.NEW,
                Duration.ofHours(2), LocalDateTime.of(2024, 1, 1, 11, 1));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertFalse(taskManager.hasTimeConflict());
    }

    @Test
    void hasTimeConflict_Overlapping_ReturnsTrue() {
        // Задачи полностью пересекаются
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 12, 0);
        Task task1 = new Task("1", "D", 0, Status.NEW,
                Duration.ofHours(2), start);
        Task task2 = new Task("2", "D", 1, Status.NEW,
                Duration.ofHours(1), start.plusMinutes(30));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertTrue(taskManager.hasTimeConflict());
    }

    @Test
    void hasTimeConflict_EdgeCaseEqualStart_ReturnsTrue() {
        // Одинаковое время начала
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 9, 0);
        Task task1 = new Task("1", "D", 0, Status.NEW,
                Duration.ofMinutes(30), start);
        Task task2 = new Task("2", "D", 1, Status.NEW,
                Duration.ofMinutes(30), start);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertTrue(taskManager.hasTimeConflict());
    }

    @Test
    void hasTimeConflict_SubtaskAndTaskOverlap_ReturnsTrue() {
        // Подзадача эпика пересекается с обычной задачей
        Epic epic = new Epic("E", "D", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("S", "D", 1, Status.NEW, epic.getId(),
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task = new Task("T", "D", 2, Status.NEW,
                Duration.ofHours(2), LocalDateTime.of(2024, 1, 1, 10, 30));

        taskManager.addSubtaskToEpic(subtask);
        taskManager.addTask(task);

        assertTrue(taskManager.hasTimeConflict());
    }

    @Test
    void hasTimeConflict_EpicWithSubtasks_ChecksCorrectly() {
        // Эпик с подзадачами должен учитываться
        Epic epic = new Epic("E", "D", Status.NEW);
        taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("S1", "D", 1, Status.NEW, epic.getId(),
                Duration.ofHours(2), LocalDateTime.of(2024, 1, 1, 8, 0));
        Subtask sub2 = new Subtask("S2", "D", 2, Status.NEW, epic.getId(),
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 9, 30));

        taskManager.addSubtaskToEpic(sub1);
        taskManager.addSubtaskToEpic(sub2);

        assertTrue(taskManager.hasTimeConflict());
    }


}