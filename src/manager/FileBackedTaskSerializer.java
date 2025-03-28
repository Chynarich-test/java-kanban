package manager;

import manager.exceptions.FileCreationException;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskSerializer {
    private static final String PROJECT_ROOT = System.getProperty("user.dir");

    public static String toCsvString(Task task) {
        String outString = "";
        outString += task.getId() + "," + task.getType() + "," + task.getName()
                + "," + task.getDescription() + "," + task.getStatus() + ",";
        if (task instanceof Subtask) {
            outString += ((Subtask) task).getIdEpic();
        }
        return outString;
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
