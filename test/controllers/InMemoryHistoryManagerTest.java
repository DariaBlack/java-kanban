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
    static Task task2;
    static Task task3;

    // вынесла в отдельный блок на случай дальнейшего добавления дополнительных тестов
    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        task1 = new Task("task1", "descriptionTask1", Status.NEW);
        task1.setId(1);
        task2 = new Task("task2", "descriptionTask2", Status.NEW);
        task2.setId(2);
        task3 = new Task("task3", "descriptionTask3", Status.NEW);
        task3.setId(3);
    }

    // проверка на то, что добавленные в HistoryManager задачи сохраняют предыдущую версию задачи
    @Test
    void shouldKeepPreviousVersionTaskInHistory() {
        taskManager.addTask(task1);
        historyManager.add(task1);

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
        taskManager.addTask(task1);
        historyManager.add(task1);
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
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
        System.out.println(historyManager.toString());
    }

    // пустая история задач
    @Test
    void shouldReturnEmptyHistoryWhenNoTasksAdded() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пуста, если задачи не добавлены");
    }

    // дублирование задач
    @Test
    void shouldNotAllowDuplicatesInHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        assertEquals(2, historyManager.getHistory().size(), "История не должна содержать дубликатов");
        assertTrue(historyManager.getHistory().contains(task1), "Задача task1 должна быть в истории");
        assertTrue(historyManager.getHistory().contains(task2), "Задача task2 должна быть в истории");
    }

    // удаление задач из начала
    @Test
    void shouldRemoveTaskFromHistoryWhenRemovedFromBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        assertFalse(historyManager.getHistory().contains(task1), "Задача task1 должна быть удалена из истории");
        assertEquals(2, historyManager.getHistory().size(), "После удаления задачи из начала, размер истории должен быть 2");
    }

    // удаление задач из середины
    @Test
    void shouldRemoveTaskFromHistoryWhenRemovedFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        assertFalse(historyManager.getHistory().contains(task2), "Задача task2 должна быть удалена из истории");
        assertEquals(2, historyManager.getHistory().size(), "После удаления задачи из середины, размер истории должен быть 2");
    }

    // удаление задач из конца
    @Test
    void shouldRemoveTaskFromHistoryWhenRemovedFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        assertFalse(historyManager.getHistory().contains(task3), "Задача task3 должна быть удалена из истории");
        assertEquals(2, historyManager.getHistory().size(), "После удаления задачи с конца, размер истории должен быть 2");
    }
}