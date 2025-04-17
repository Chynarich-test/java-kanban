package manager;

import manager.exceptions.FileLoadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static final String TEST_FILE = "test_tasks.txt";
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager(TEST_FILE);
        taskManager = new FileBackedTaskManager(TEST_FILE);
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
        assertTrue(content.contains("0,TASK,Таск 1,Описание,NEW,"));
    }

    @Test
    void ShouldClearFile() throws IOException {
        Task task = new Task("Таск 1", "Описание", 1, "NEW");
        manager.addTask(task);
        manager.deleteAllTasks();

        File file = new File("resources/" + TEST_FILE);
        String content = Files.readString(file.toPath());

        assertEquals("id,type,name,status,description,duration,startTime,epic\n", content);
    }

    @Test
    void ShouldRemoveTaskFromFile() throws IOException {
        Task task = new Task("Таск 1", "Описание", 1, "NEW");
        manager.addTask(task);
        manager.deleteTaskAnID(1);

        File file = new File("resources/" + TEST_FILE);
        String content = Files.readString(file.toPath());

        assertFalse(content.contains("1,TASK,Таск 1,Описание,NEW,"));
    }

    @Test
    void loadFromFile_WithNonExistentFile_ThrowsException() {
        File nonExistentFile = new File("non_existent.txt");

        assertThrows(FileLoadException.class,
                () -> FileBackedTaskManager.loadFromFile(nonExistentFile),
                "Загрузка из несуществующего файла должна бросать исключение");
    }

    @Test
    void loadFromFile_WithValidFile_DoesNotThrow() throws IOException {
        // Создаем корректный файл
        File validFile = File.createTempFile("valid", ".txt");
        Files.write(validFile.toPath(), List.of(
                "id,type,name,status,description,duration,startTime,epic",
                "1,TASK,Задача 2,Описание 2,IN_PROGRESS,PT1H30M,2023-04-02T14:30,"
        ));

        assertDoesNotThrow(
                () -> FileBackedTaskManager.loadFromFile(validFile),
                "Корректный файл не должен вызывать исключений");

        validFile.delete();
    }
}
