package model;

import controllers.InMemoryTaskManager;
import controllers.Managers;
import controllers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    static TaskManager taskManager;
    static Epic epic;
    static Subtask subtask1;
    static Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        epic = new Epic("epic", "descriptionEpic");
        taskManager.addEpic(epic);

        subtask1 = new Subtask("subtask1", "DescriptionSubtask", Status.NEW, epic.getId());
        subtask1.setId(epic.getId());

        subtask2 = new Subtask("subtask2", "DescriptionSubtask2", Status.NEW, epic.getId());
        subtask2.setId(epic.getId());
    }

    // проверка на то, что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    void shouldNotAllowEpicToAddAsSubtask() {
        taskManager.addSubtask(subtask1);

        assertFalse(epic.getSubtasksInEpic().isEmpty(), "Эпик не может добавить себе в качестве подзадачи");
    }

    // проверка новых полей - startTime, endTime и duration (выбрала эпик, так как ту можно проверить все три поля)
    @Test
    void shouldCalculateEpicEndTimeStartTimeAndDuration() {
        subtask1.setDuration(60);
        subtask1.setStartTime(LocalDateTime.of(2000, 1, 1, 0,0,0));
        taskManager.addSubtask(subtask1);

        subtask2.setDuration(120);
        subtask2.setStartTime(LocalDateTime.of(2000, 1, 1, 1,0,0));
        taskManager.addSubtask(subtask2);

        epic.updateStatus((InMemoryTaskManager) taskManager);
        epic.setEndTime(epic.calculateEndTime((InMemoryTaskManager) taskManager));

        assertEquals(180, epic.getDuration(), "Продолжительность эпика должна быть суммой продолжительностей всех подзадач.");
        assertEquals(subtask1.getStartTime(), epic.getStartTime(), "Время начала должно совпадать с временем начала самой ранней подзадачи.");
        assertEquals(subtask2.getEndTime(), epic.getEndTime(), "Время завершения должно совпадать с временем окончания самой поздней подзадачи.");
    }

    // Все подзадачи со статусом NEW
    @Test
    void shouldSetEpicStatusToNewWhenAllSubtasksAreNew() {
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic.updateStatus((InMemoryTaskManager) taskManager);

        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если статус всех подзадач NEW");
    }

    // Все подзадачи со статусом DONE
    @Test
    void shouldSetEpicStatusToDoneWhenAllSubtasksAreDone() {
        subtask1.setStatus(Status.DONE);
        taskManager.addSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.addSubtask(subtask2);

        epic.updateStatus((InMemoryTaskManager) taskManager);

        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если статус всех подзадач DONE");
    }

    // Подзадачи со статусами NEW и DONE
    @Test
    void shouldSetEpicStatusToInProgressWhenSubtasksAreNewAndDone() {
        subtask1.setStatus(Status.DONE);
        taskManager.addSubtask(subtask1);
        subtask2.setStatus(Status.NEW);
        taskManager.addSubtask(subtask2);

        epic.updateStatus((InMemoryTaskManager) taskManager);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если подзадачи со статусами NEW и DONE");
    }

    // Подзадачи со статусом IN_PROGRESS
    @Test
    void shouldSetEpicStatusToInProgressWhenAnySubtaskIsInProgress() {
        taskManager.addSubtask(subtask1);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.addSubtask(subtask2);

        epic.updateStatus((InMemoryTaskManager) taskManager);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если хотя бы одна подзадача в процессе");
    }

    // Статус эпика, если нет подзадач
    @Test
    void shouldNotChangeEpicStatusIfNoSubtasks() {
        taskManager.addEpic(epic);

        epic.updateStatus((InMemoryTaskManager) taskManager);
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если нет подзадач");
    }

    // Проверка расчёта времени и продолжительности эпика
    @Test
    void shouldCalculateEpicTimeForNoSubtasks() {
        taskManager.addEpic(epic);

        assertNull(epic.getStartTime(), "Время начала эпика должно быть null, если нет подзадач");
        assertNull(epic.getEndTime(), "Время окончания эпика должно быть null, если нет подзадач");
    }
}