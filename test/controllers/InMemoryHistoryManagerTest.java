package controllers;

import controllers.interfaces.HistoryManager;
import controllers.interfaces.TaskManager;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    // проверка на то, что история не содержит null-задач
    @Test
    void shouldNotBeAddedTaskWithNonExistIDToHistory() {
        taskManager.getTask(555);

        ArrayList<Task> history = (ArrayList<Task>) taskManager.getHistory();
        assertFalse(history.contains(null), "История задач не должна содержать null-задач");
    }

    // проверка на корректность работы двусвязного списка - добавление
    @Test
    void shouldBeSizeOfHistory1AfterAddingOneTask() {
        Task task = new Task("task1", "description1", Status.NEW);
        task.setId(1);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    // проверка на корректность работы двусвязного списка - удаление и обновление списка после
    @Test
    void shouldBeSizeHistory1AfterRemove1TaskFrom2() {
        Task task1 = new Task("task1", "description1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("task2", "description2", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }
}