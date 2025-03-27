package manager;

import manager.exceptions.FileCreationException;
import manager.exceptions.FileLoadException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskSerializer {
    private static final String PROJECT_ROOT = System.getProperty("user.dir");

    public static FileBackedTaskManager loadFromFile(File file) throws FileLoadException {
        try {
            List<String> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while (br.ready()) {
                    list.add(br.readLine());
                }
            }
            list.sort(new FileBackedComparator());

            FileBackedTaskManager manager = new FileBackedTaskManager();

            for (String item : list) {
                String[] parseItem = List.of(item.split(",")).toArray(new String[0]);
                String itemType = parseItem[1];

                switch (itemType) {
                    case "TASK":
                        Task task = new Task(parseItem[2], parseItem[3],
                                Long.parseLong(parseItem[0]), parseItem[4]);
                        manager.tasks.put(Long.parseLong(parseItem[0]), task);
                        break;
                    case "EPIC":
                        Epic epic = new Epic(parseItem[2], parseItem[3],
                                Long.parseLong(parseItem[0]), Status.valueOf(parseItem[4]));
                        manager.epics.put(Long.parseLong(parseItem[0]), epic);
                        break;
                    case "SUBTASK":
                        Subtask subtask = new Subtask(parseItem[2], parseItem[3],
                                Long.parseLong(parseItem[0]), Status.valueOf(parseItem[4]), Long.parseLong(parseItem[5]));
                        manager.subtasks.put(Long.parseLong(parseItem[0]), subtask);

                        //Пришлось сделать так, потому что базовый метод addSubtaskToEpic не позволяет создавать эпик
                        //с кастомным айди, а городить новый метод ради этого не хочу
                        Epic subtaskEpicId = manager.epics.get(subtask.getIdEpic());
                        subtaskEpicId.addSubTask(Long.parseLong(parseItem[0]));
                        break;
                }
            }
            manager.maxID = list.size();
            return manager;

        } catch (IOException e) {
            throw new FileLoadException();
        }
    }

    public static String toCsvString(Task task) {
        if (task instanceof Subtask) {
            return task.getId() + "," + task.getType() + "," + task.getName()
                    + "," + task.getDescription() + "," + task.getStatus() + "," + ((Subtask) task).getIdEpic();
        }
        return task.getId() + "," + task.getType() + "," + task.getName()
                + "," + task.getDescription() + "," + task.getStatus();
    }

    public static void createDbFiles(String fileName) throws FileCreationException {
        try {
            Path dbDir = Paths.get(PROJECT_ROOT, "resources");
            if (!Files.exists(dbDir)) {
                Files.createDirectory(dbDir);
            }
            Path dbFile = Paths.get(PROJECT_ROOT, "resources", fileName);
            if (!Files.exists(dbFile)) {
                Files.createFile(dbFile);
            }
        } catch (IOException e) {
            throw new FileCreationException();

        }
    }
}
