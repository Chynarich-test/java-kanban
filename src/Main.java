import manager.FileBackedTaskManager;
import manager.FileBackedTaskSerializer;
import manager.exceptions.FileCreationException;
import manager.exceptions.FileLoadException;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        try {
            FileBackedTaskSerializer.createDbFiles("tasks.txt");
            FileBackedTaskManager manager = FileBackedTaskSerializer.loadFromFile(
                    Paths.get(System.getProperty("user.dir"), "resources", "tasks.txt").toFile());
            System.out.println(manager.getEpicAnID(2));
            System.out.println(manager.getSubtaskAnID(4));
            System.out.println(manager.getSubtaskAnID(5));
        } catch (FileCreationException e) {
            System.out.println("Не удалось создать файлы");
        } catch (FileLoadException e) {
            System.out.println("Не удалось загрузить данные");
        }

//        FileBackedTaskManager manager = new FileBackedTaskManager();
//        //Создание данных
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


    }
}