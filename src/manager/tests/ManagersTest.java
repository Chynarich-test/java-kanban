package manager.tests;

import manager.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void epicEqualityById() {
        assertNotNull(Managers.getDefault(), "Менеджер задач не должен быть null");
        assertNotNull(Managers.getDefaultHistory(), "Менеджер истории не должен быть null");
    }

}