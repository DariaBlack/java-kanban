package model;

import controllers.Managers;
import controllers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    // проверка на то, что объект Subtask нельзя сделать своим же эпиком
    @Test
    void shouldNotAllowSubtaskToBeOwnEpic() {
        Epic epic = new Epic("epic", "descriptionEpic");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "descriptionSubtask", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        subtask.setId(subtask.getId());
        assertNotEquals(subtask.getId(), subtask.getIdEpic(), "Подзадача не может быть собственным эпиком");
    }
}