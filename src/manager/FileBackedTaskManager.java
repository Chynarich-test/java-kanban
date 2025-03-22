package manager;

import manager.exceptions.FileCreationException;
import manager.exceptions.FileLoadException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String PROJECT_ROOT = System.getProperty("user.dir");

    public FileBackedTaskManager() {
        try {
            createDbFiles();
            loadData();
        } catch (FileCreationException e) {
            System.out.println("Не удалось создать файлы");
        } catch (FileLoadException e) {
            System.out.println("Не удалось загрузить данные");
        }
    }

    private static List<String> loadFromFile(File file) throws IOException {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                list.add(br.readLine());
            }
        }
        return list;
    }

    private void createDbFiles() throws FileCreationException {
        try {
            Path dbDir = Paths.get(PROJECT_ROOT, "db");
            if (!Files.exists(dbDir)) {
                Files.createDirectory(dbDir);
            }
            Path dbFile = Paths.get(PROJECT_ROOT, "db", "tasks.txt");
            if (!Files.exists(dbFile)) {
                Files.createFile(dbFile);
            }
        } catch (IOException e) {
            throw new FileCreationException();
        }
    }

    private void loadData() throws FileLoadException {
        try {
            Path db = Paths.get(PROJECT_ROOT, "db", "tasks.txt");
            List<String> list = loadFromFile(db.toFile());

            for (String item : list) {
                List<String> parseItem = new ArrayList<>(List.of(item.split(",")));
                if (Objects.equals(parseItem.get(1), "TASK")) {
                    Task task = new Task(parseItem.get(2), parseItem.get(3),
                            Long.parseLong(parseItem.get(0)), parseItem.get(4));
                    tasks.put(Long.parseLong(parseItem.get(0)), task);
                } else if (Objects.equals(parseItem.get(1), "EPIC")) {
                    String[] subtaskIdsArray = parseItem.get(5).split(";");
                    ArrayList<Long> subtasksIDs = new ArrayList<>();

                    for (String idStr : subtaskIdsArray) {
                        subtasksIDs.add(Long.parseLong(idStr));
                    }
                    Epic epic = new Epic(parseItem.get(2), parseItem.get(3),
                            Long.parseLong(parseItem.get(0)), parseItem.get(4), subtasksIDs);
                    epics.put(Long.parseLong(parseItem.get(0)), epic);
                } else if (Objects.equals(parseItem.get(1), "SUBTASK")) {
                    Subtask subtask = new Subtask(parseItem.get(2), parseItem.get(3),
                            Long.parseLong(parseItem.get(0)), Status.valueOf(parseItem.get(4)), Long.parseLong(parseItem.get(5)));
                    subtasks.put(Long.parseLong(parseItem.get(0)), subtask);
                }
            }

        } catch (IOException e) {
            throw new FileLoadException();
        }

    }

    private void save() {
        try {
            Path db = Paths.get(PROJECT_ROOT, "db", "tasks.txt");
            Files.write(db, new byte[0]);

            try (FileWriter fileWriter = new FileWriter(
                    db.toString(), true)) {
                for (Task task : tasks.values()) {
                    fileWriter.write(task.toCsvString() + "\n");
                }
                for (Epic epic : epics.values()) {
                    fileWriter.write(epic.toCsvString() + "\n");
                }
                for (Subtask subtask : subtasks.values()) {
                    fileWriter.write(subtask.toCsvString() + "\n");
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

