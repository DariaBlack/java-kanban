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

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        epic = new Epic("epic", "descriptionEpic");
        taskManager.addEpic(epic);
    }

    // проверка на то, что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    void shouldNotAllowEpicToAddAsSubtask() {
        Subtask subtask = new Subtask("subtask", "DescriptionSubtask", Status.NEW, epic.getId());
        subtask.setId(epic.getId());

        taskManager.addSubtask(subtask);

        assertFalse(epic.getSubtasksInEpic().isEmpty(), "Эпик не может добавить себе в качестве подзадачи");
    }

    // проверка новых полей - startTime и duration
    @Test
    void shouldCalculateEpicEndTimeStartTimeAndDuration() {
        Subtask subtask1 = new Subtask("subtask1", "DescriptionSubtask", Status.NEW, epic.getId());
        subtask1.setId(epic.getId());
        subtask1.setDuration(60);
        subtask1.setStartTime(LocalDateTime.now());
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("subtask2", "DescriptionSubtask2", Status.NEW, epic.getId());
        subtask2.setId(epic.getId());
        subtask2.setDuration(15);
        subtask2.setStartTime(LocalDateTime.now().plusMinutes(15));
        taskManager.addSubtask(subtask2);

        epic.updateStatus((InMemoryTaskManager) taskManager);
        epic.setEndTime(epic.calculateEndTime((InMemoryTaskManager) taskManager));

        assertEquals(75, epic.getDuration(), "Продолжительность эпика должна быть суммой продолжительностей всех подзадач.");
        assertEquals(subtask1.getStartTime(), epic.getStartTime(), "Время начала должно совпадать с временем начала самой ранней подзадачи.");
    }
}