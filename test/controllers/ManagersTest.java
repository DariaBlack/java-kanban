package controllers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    // тест на возврат классом Managers проинициализированных экземпляров менеджеров
    @Test
    public void shouldBeNotNullWhenInstanceCreated() {
        assertNotNull(Managers.getDefault(), "Managers возвращает не проинициализированный экземпляр TaskManager");
        assertNotNull(Managers.getDefaultHistory(), "Managers возвращает не проинициализированный экземпляр HistoryManager");
    }
}