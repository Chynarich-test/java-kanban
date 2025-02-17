import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();
        //1. Создания задачи данного типа
        print("1. Создания задачи данного типа");
        print(manager.getEpics());
        manager.addEpic(new Epic("1 эпик", "Описание 1", Status.NEW));
        manager.addEpic(new Epic("2 эпик", "Описание 2", Status.IN_PROGRESS));
        manager.addEpic(new Epic("3 эпик", "Описание 3", Status.DONE));
        print(manager.getEpics());


        manager.addSubtaskToEpic(new Subtask("1 сабтаск у 1", "Сабтаск описание 1", Status.NEW, 0));
        manager.addSubtaskToEpic(new Subtask("2 сабтаск у 1", "Сабтаск описание 2", Status.IN_PROGRESS, 0));
        manager.addSubtaskToEpic(new Subtask("1 сабтаск у 2", "Сабтаск описание 3", Status.DONE, 1));
        manager.addSubtaskToEpic(new Subtask("1 сабтаск у 3", "Сабтаск описание 4", Status.NEW, 2));
        //Обратите внимание, что статус эпиков корректно поменялся из-за наличия у них сабтасков
        manager.addTask(new Task("Таск 1", "Описание таска 1", Status.NEW));

        print(manager.getEpics());
        print(manager.getSubtasks());
        print(manager.getTasks());


        print("\n");

        //2. Обновление задачи
        print("2. Обновление задачи");
        manager.updateEpic(new Epic("1 эпик - изменено", "Описание 1 - изменено", 0, Status.NEW));
        print(manager.getEpics());
        print("\n");
        //Обратите внимание, что статус не поменялся, так как у эпика есть блокирующие его статус сабтаски

        manager.updateSubtask(new Subtask("1 сабтаск у 1 - изменено", "Сабтаск описание 1 - изменено", 3, Status.DONE, 0));
        manager.updateSubtask(new Subtask("2 сабтаск у 1 - изменено", "Сабтаск описание 2 - изменено", 4, Status.IN_PROGRESS, 1));
        print(manager.getSubtasks());
        print(manager.getEpics());
        //Обратите внимание, что статус у 1 эпика поменялся на DONE потому что все сабтаски теперь завершены,
        // а статус 2 эпика поменялся на IN_PROGRESS потому, что в него переместился новый не выполненный сабтаск

        print(manager.getTasks());
        manager.updateTask(new Task("Таск 1 - обновленный", "Описание таска 1 - отредактированною", 7, Status.DONE));
        print(manager.getTasks());
        print("\n");


        //3. Получение задачи по id
        print("3. Получение задачи по id");
        print(manager.getEpicAnID(0));
        print(manager.getSubtaskAnID(8));
        print(manager.getTaskAnID(7));
        print(manager.getObjectAnID(7));
        print("\n");

        //4. Получение всех сабтасков эпика по id
        print("4. Получение всех сабтасков эпика по id");
        print(manager.getSubtasksByID(1));
        print("\n");

        //5. Обновление статуса задач
        print("5. получение истории");
        int sh = 0;
        for(Task o: manager.getHistory()){
            sh++;
            print(sh + ". " + o);
        }
        manager.getTaskAnID(7);
        manager.getTaskAnID(7);
        manager.getTaskAnID(7);
        manager.getTaskAnID(7);
        manager.getTaskAnID(7);
        print("\n");
        sh = 0;
        for(Task o: manager.getHistory()){
            sh++;
            print(sh + ". " + o);
        }
        //можно увидеть как первый элемент удалился из-за переполнения
        print("\n");



        //6. Удаление задачи по id
        print("6. Удаление задачи по id");
        print(manager.getEpicAnID(1));
        print(manager.getSubtaskAnID(5));
        print(manager.getSubtaskAnID(8));
        manager.deleteTaskAnID(1);
        print(manager.getEpicAnID(1));
        print(manager.getSubtaskAnID(5));
        print(manager.getSubtaskAnID(8));
        //сначала он вывел эпик и его сабтаски, после удаления, он его не нашел, а также не нашел его сабтаски

        print(manager.getTasks());
        manager.deleteTaskAnID(7);
        print(manager.getTasks());
        print("\n");

        //7. Удаление всех задач данного типа
        print("7. Удаление всех задач данного типа");

        manager.addTask(new Task("Задача 4", "Описание", Status.DONE));
        print(manager.getTasks());
        manager.deleteAllTasks();
        print(manager.getTasks());

        print("\n");

        print(manager.getSubtasks());
        manager.deleteAllSubtasks();
        print(manager.getSubtasks());

        print("\n");

        print(manager.getEpics());
        manager.deleteAllEpics();
        print(manager.getEpics());

    }

    private static void print(Object out){
        System.out.println(out);
    }
}
