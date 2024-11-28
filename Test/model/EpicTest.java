package model;

import controllers.Managers;
import controllers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    // проверка на то, что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    void shouldNotAllowEpicToAddAsSubtask() {
        Epic epic = new Epic("epic", "descriptionEpic");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask", "DescriptionSubtask", Status.NEW, epic.getId());
        subtask.setId(epic.getId());

        taskManager.addSubtask(subtask);

        assertFalse(epic.getSubtasksInEpic().isEmpty(), "Эпик не может добавить себе в качестве подзадачи");
    }
}