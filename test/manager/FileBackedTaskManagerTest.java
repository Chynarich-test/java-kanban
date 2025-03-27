package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    private static final String TEST_FILE = "test_tasks.txt";
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager(TEST_FILE);
    }

    @AfterEach
    void cleanUp() {
        File file = new File("resources/" + TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void ShouldSaveToFile() throws IOException {
        Task task = new Task("Таск 1", "Описание", 1, "NEW");
        manager.addTask(task);

        File file = new File("resources/" + TEST_FILE);
        assertTrue(file.exists());

        String content = Files.readString(file.toPath());
        assertTrue(content.contains("0,TASK,Таск 1,Описание,NEW"));
    }

    @Test
    void ShouldClearFile() throws IOException {
        Task task = new Task("Таск 1", "Описание", 1, "NEW");
        manager.addTask(task);
        manager.deleteAllTasks();

        File file = new File("resources/" + TEST_FILE);
        String content = Files.readString(file.toPath());

        assertTrue(content.isEmpty());
    }

    @Test
    void ShouldRemoveTaskFromFile() throws IOException {
        Task task = new Task("Таск 1", "Описание", 1, "NEW");
        manager.addTask(task);
        manager.deleteTaskAnID(1);

        File file = new File("resources/" + TEST_FILE);
        String content = Files.readString(file.toPath());

        assertFalse(content.contains("1,TASK,Таск 1,Описание,NEW"));
    }
}
