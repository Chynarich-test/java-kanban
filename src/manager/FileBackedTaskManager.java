package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private final String fileName;

    public FileBackedTaskManager() {
        this.fileName = "tasks.txt";
    }

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
    }

    private void save() {
        try {
            Path db = Paths.get(PROJECT_ROOT, "resources", fileName);
            Files.write(db, new byte[0]);

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

