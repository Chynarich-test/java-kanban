import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // 1. Создание задач
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        manager.addTask(task1);
        manager.addTask(task2);

        // 2. Создание эпиков
        Epic epicWithSubtasks = new Epic("Эпик с подзадачами", "Описание эпика", Status.NEW);
        Epic emptyEpic = new Epic("Пустой эпик", "Нет подзадач", Status.NEW);
        manager.addEpic(epicWithSubtasks);
        manager.addEpic(emptyEpic);

        // 3. Добавление подзадач
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", Status.NEW, 2);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", Status.DONE, 2);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание 3", Status.IN_PROGRESS, 2);
        manager.addSubtaskToEpic(subtask1);
        manager.addSubtaskToEpic(subtask2);
        manager.addSubtaskToEpic(subtask3);

        // 4. Запросы в разном порядке
        System.out.println(manager.getHistory());

        manager.getTaskAnID(0);
        manager.getEpicAnID(2);
        manager.getSubtaskAnID(4);

        System.out.println(manager.getHistory());

        manager.getEpicAnID(3);
        manager.getTaskAnID(1);
        manager.getSubtaskAnID(5);

        System.out.println(manager.getHistory());

        // 5. После запроса того что уже было
        manager.getTaskAnID(0);
        manager.getEpicAnID(2);
        manager.getSubtaskAnID(4);
        System.out.println(manager.getHistory());
    }
}