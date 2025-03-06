package java.tasks;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void epicEqualityById() {
        Epic epic1 = new Epic("Epic1", "Description1", 1, Status.NEW);
        Epic epic2 = new Epic("Epic2", "Description2", 1, Status.DONE);
        assertEquals(epic1, epic2, "Эпики с одинаковым ID должны быть равны");
    }

    @Test
    void epicAddYourselfToYourself() {
        Epic epic1 = new Epic("Epic1", "Description1", 1, Status.NEW);
        epic1.addSubTask((long) 1);
        assertEquals(0, epic1.getSubtasks().size(), "Эпики не должны добавляться друг в друга");
    }
}