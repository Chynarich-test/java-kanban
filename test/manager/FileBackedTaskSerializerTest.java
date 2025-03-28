package manager;

import manager.exceptions.FileCreationException;
import manager.exceptions.FileLoadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskSerializerTest {
    private static final String TEST_FILE = "test_tasks.txt";

    @AfterEach
    void cleanUp() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void ShouldSerializeTaskCorrectly() {
        Task task = new Task("Тестовый таск", "Описание", 1, "NEW");
        String file = FileBackedTaskSerializer.toCsvString(task);
        assertEquals("1,TASK,Тестовый таск,Описание,NEW,", file);
    }

    @Test
    void ShouldSerializeSubtaskCorrectly() {
        Subtask subtask = new Subtask("Тестовый сабтаск", "Описание", 2, Status.NEW, 1);
        String file = FileBackedTaskSerializer.toCsvString(subtask);
        assertEquals("2,SUBTASK,Тестовый сабтаск,Описание,NEW,1", file);
    }

    @Test
    void FilesShouldCreateFileSuccessfully() throws FileCreationException {
        FileBackedTaskSerializer.createDbFiles(TEST_FILE);
        File file = new File("resources/" + TEST_FILE);
        assertTrue(file.exists());
    }

    @Test
    void ShouldLoadTasksCorrectly() throws IOException, FileLoadException {
        String content = """
                1,TASK,Тестовый таск,Описание,NEW,
                2,EPIC,Тестовый Эпик,Описание,NEW,
                3,SUBTASK,Тестовый Сабтаск,Описание,NEW,2
                """;
        Files.write(new File(TEST_FILE).toPath(), content.getBytes());

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(new File(TEST_FILE));

        assertEquals(1, manager.tasks.size());
        assertEquals(1, manager.epics.size());
        assertEquals(1, manager.subtasks.size());
    }
}
