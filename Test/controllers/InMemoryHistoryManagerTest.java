package controllers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static TaskManager taskManager;
    static HistoryManager historyManager;
    static Task task1;

    // вынесла в отдельный блок на случай дальнейшего добавления дополнительных тестов
    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        task1 = new Task("task1", "descriptionTask1", Status.NEW);
        taskManager.addTask(task1);
        historyManager.add(task1);
    }

    // проверка на то, что добавленные в HistoryManager задачи сохраняют предыдущую версию задачи
    @Test
    void shouldKeepPreviousVersionTaskInHistory() {
        Task updatedTask = new Task("task1", "description2", Status.DONE);
        taskManager.updateTask(task1.getId(), updatedTask);
        historyManager.add(updatedTask);

        assertEquals(2, historyManager.getHistory().size(), "История должна содержать исходную и обновлённую задачу");
        assertEquals("descriptionTask1", historyManager.getHistory().get(0).getDescription(), "Первая версия задачи должна быть сохранена");
        assertEquals("description2", historyManager.getHistory().get(1).getDescription(), "Обновлённая версия должна быть добавлена в историю");
    }
}