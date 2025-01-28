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

    @Test
    void shouldSetEpicStatusToNewWhenAllSubtasksAreNew() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        System.out.println("Подзадачи перед обновлением статуса: " + taskManager.getSubtasks(epic.getId()));

        epic.updateStatus(taskManager.getSubtasks(epic.getId()));

        System.out.println("Статус эпика после обновления: " + epic.getStatus());

        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если статус всех подзадач NEW");
    }

    @Test
    void shouldCalculateEpicEndTimeStartTimeAndDuration() {
        subtask1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        subtask1.setDuration(30);
        subtask2.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 40));
        subtask2.setDuration(20);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic.getSubtasksInEpic().add(subtask1.getId());
        epic.getSubtasksInEpic().add(subtask2.getId());

        epic.updateStatus(Arrays.asList(subtask1, subtask2));
        taskManager.updateEpic(epic.getId(), epic);

        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 50), epic.getEndTime());
        assertEquals(50, epic.getDuration());
    }
}