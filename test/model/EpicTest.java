package model;

import controllers.Managers;
import controllers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

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

        subtask1 = new Subtask("subtask1", "DescriptionSubtask", Status.NEW, epic.getId());
        subtask2 = new Subtask("subtask2", "DescriptionSubtask2", Status.NEW, epic.getId());
    }

    // проверка на то, что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    void shouldNotAllowEpicToAddAsSubtask() {
        taskManager.addSubtask(subtask1);
        epic.getSubtasksInEpic().add(epic.getId());
        taskManager.addEpic(epic);

        assertFalse(epic.getSubtasksInEpic().contains(epic.getId()), "Эпик не может добавить себе в качестве подзадачи");
    }

    // проверка новых полей - startTime, endTime и duration (выбрала эпик, так как ту можно проверить все три поля)
    @Test
    void shouldCalculateEpicEndTimeStartTimeAndDuration() {
        subtask1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        subtask1.setDuration(30);
        subtask2.setStartTime(LocalDateTime.of(2025,1,1,10,40));
        subtask2.setDuration(20);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic.getSubtasksInEpic().add(subtask1.getId());
        epic.getSubtasksInEpic().add(subtask2.getId());

        epic.updateStatus(Arrays.asList(subtask1, subtask2));
        taskManager.addEpic(epic);

        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, 1,1,10, 50), epic.getEndTime());
        assertEquals(50, epic.getDuration());
    }

    // Все подзадачи со статусом NEW
    @Test
    void shouldSetEpicStatusToNewWhenAllSubtasksAreNew() {
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic.updateStatus(taskManager.getSubtasks(epic.getId()));

        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если статус всех подзадач NEW");
    }

    // Все подзадачи со статусом DONE
    @Test
    void shouldSetEpicStatusToDoneWhenAllSubtasksAreDone() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic.getSubtasksInEpic().add(subtask1.getId());
        epic.getSubtasksInEpic().add(subtask2.getId());

        epic.updateStatus(Arrays.asList(subtask1, subtask2));

        assertEquals(Status.DONE, epic.getStatus());
    }

    // Подзадачи со статусами NEW и DONE
    @Test
    void shouldSetEpicStatusToInProgressWhenSubtasksAreNewAndDone() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.NEW);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic.getSubtasksInEpic().add(subtask1.getId());
        epic.getSubtasksInEpic().add(subtask2.getId());

        epic.updateStatus(Arrays.asList(subtask1, subtask2));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    // Подзадачи со статусом IN_PROGRESS
    @Test
    void shouldSetEpicStatusToInProgressWhenAnySubtaskIsInProgress() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic.getSubtasksInEpic().add(subtask1.getId());
        epic.getSubtasksInEpic().add(subtask2.getId());

        epic.updateStatus(Arrays.asList(subtask1, subtask2));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    // Статус эпика, если нет подзадач
    @Test
    void shouldNotChangeEpicStatusIfNoSubtasks() {
        taskManager.addEpic(epic);

        epic.updateStatus(taskManager.getSubtasks(epic.getId()));
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