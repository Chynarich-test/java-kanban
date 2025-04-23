package manager;

import manager.exceptions.ConflictWithExistingException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void shouldCreateTaskWhenNoTimeOverlap() {
        // Задача создается, когда нет пересечений по времени
        Task task1 = new Task("1", "D", 0, Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task2 = new Task("2", "D", 1, Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 11, 30));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getTasks().size(), "Обе задачи должны быть добавлены");
    }

    @Test
    void shouldThrowExceptionWhenTaskFullyInsideAnotherTask() {
        // Задача не создается, если она полностью внутри другой задачи
        Task task1 = new Task("1", "D", 0, Status.NEW,
                Duration.ofHours(2), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task2 = new Task("2", "D", 1, Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 1, 10, 30));

        taskManager.addTask(task1);

        assertThrows(ConflictWithExistingException.class, () -> {
            taskManager.addTask(task2);
        }, "Должно быть выброшено исключение ConflictWithExistingException при полном вложении");

        assertEquals(1, taskManager.getTasks().size(), "Только первая задача должна быть добавлена");
    }


    @Test
    void shouldNotCreateTaskWhenStartOrEndOverlaps() {
        // Задача не создается, если её начало или конец попадает в промежуток другой задачи
        Task task1 = new Task("1", "D", 0, Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task2 = new Task("2", "D", 1, Status.NEW,
                Duration.ofHours(1), LocalDateTime.of(2024, 1, 1, 10, 30));

        taskManager.addTask(task1);
        assertThrows(ConflictWithExistingException.class, () -> {
            taskManager.addTask(task2);
        }, "Должно быть выброшено исключение ConflictWithExistingException при полном вложении");

        assertEquals(1, taskManager.getTasks().size(), "Только первая задача должна быть добавлена");
    }

    @Test
    void shouldNotCreateTaskWhenStartOrEndEquals() {
        // Задача не создается, если её начало или конец совпадает с другой задачей
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("1", "D", 0, Status.NEW,
                Duration.ofHours(1), startTime);
        Task task2 = new Task("2", "D", 1, Status.NEW,
                Duration.ofHours(1), startTime);

        taskManager.addTask(task1);
        assertThrows(ConflictWithExistingException.class, () -> {
            taskManager.addTask(task2);
        }, "Должно быть выброшено исключение ConflictWithExistingException при полном вложении");

        assertEquals(1, taskManager.getTasks().size(), "Только первая задача должна быть добавлена");
    }


}