package manager;

import manager.exceptions.FileLoadException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private final String fileName;

    public FileBackedTaskManager() {
        this.fileName = "tasks.txt";
    }

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws FileLoadException {
        try {
            List<String> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while (br.ready()) {
                    list.add(br.readLine());
                }
            }

            FileBackedTaskManager manager = new FileBackedTaskManager();


            for (String item : list) {
                String[] parseItem = List.of(item.split(",")).toArray(new String[0]);
                String itemType = parseItem[1];

                switch (itemType) {
                    case "TASK":
                        Task task = new Task(parseItem[2], parseItem[3],
                                Long.parseLong(parseItem[0]), Status.valueOf(parseItem[4]),
                                Duration.parse(parseItem[5]), LocalDateTime.parse(parseItem[6]));
                        manager.tasks.put(Long.parseLong(parseItem[0]), task);
                        break;
                    case "EPIC":
                        Epic epic = new Epic(parseItem[2], parseItem[3],
                                Long.parseLong(parseItem[0]), Status.valueOf(parseItem[4]));
                        manager.epics.put(Long.parseLong(parseItem[0]), epic);
                        break;
                    case "SUBTASK":
                        Subtask subtask = new Subtask(parseItem[2], parseItem[3],
                                Long.parseLong(parseItem[0]), Status.valueOf(parseItem[4]),
                                Long.parseLong(parseItem[7]),
                                Duration.parse(parseItem[5]), LocalDateTime.parse(parseItem[6]));
                        manager.subtasks.put(Long.parseLong(parseItem[0]), subtask);

                        Epic epicForSubtask = manager.epics.get(subtask.getIdEpic());
                        epicForSubtask.addSubTask(Long.parseLong(parseItem[0]));
                        break;
                }
            }
            manager.maxID = list.size();
            return manager;

        } catch (IOException e) {
            throw new FileLoadException();
        }
    }

    private void save() {
        try {
            Path db = Paths.get(PROJECT_ROOT, "resources", fileName);
            Files.write(db, "id,type,name,status,description,duration,startTime,epic\n".getBytes());

            try (FileWriter fileWriter = new FileWriter(
                    db.toString(), true)) {
                for (Task task : tasks.values()) {
                    fileWriter.write(FileBackedTaskSerializer.toCsvString(task) + "\n");
                }
                for (Epic epic : epics.values()) {
                    fileWriter.write(FileBackedTaskSerializer.toCsvString(epic) + "\n");
                }
                for (Subtask subtask : subtasks.values()) {
                    fileWriter.write(FileBackedTaskSerializer.toCsvString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка сохранения файла");
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtaskToEpic(Subtask subtask) {
        super.addSubtaskToEpic(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }


    @Override
    public void deleteTaskAnID(long id) {
        super.deleteTaskAnID(id);
        save();
    }


    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
}

