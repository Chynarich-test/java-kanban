import manager.FileBackedTaskManager;
import manager.Managers;

public class Main {

    public static void main(String[] args) {
        FileBackedTaskManager manager = Managers.getFileTaskManager();

        //Создание данных
//        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
//        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
//        manager.addTask(task1);
//        manager.addTask(task2);
//
//        Epic epicWithSubtasks = new Epic("Эпик с подзадачами", "Описание эпика", Status.NEW);
//        Epic emptyEpic = new Epic("Пустой эпик", "Нет подзадач", Status.NEW);
//        manager.addEpic(epicWithSubtasks);
//        manager.addEpic(emptyEpic);
//
//        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", Status.NEW, 2);
//        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", Status.DONE, 2);
//        Subtask subtask3 = new Subtask("Подзадача 3", "Описание 3", Status.IN_PROGRESS, 2);
//        manager.addSubtaskToEpic(subtask1);
//        manager.addSubtaskToEpic(subtask2);
//        manager.addSubtaskToEpic(subtask3);


        //Если предварительно данные были созданы в другой сессии, то после перезапуска они сохранятся
        System.out.println(manager.getEpicAnID(2));
        System.out.println(manager.getSubtaskAnID(4));
        System.out.println(manager.getSubtaskAnID(5));


    }
}