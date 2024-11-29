package controllers;

import controllers.interfaces.TaskManager;
import model.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest {
    static TaskManager taskManager;
    static Task task1;
    static Task task2;
    static Epic epic1;
    static Subtask subtask1;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        task1 = new Task("task1", "descriptionTask1", Status.NEW);
        taskManager.addTask(task1);
        task2 = new Task("task2", "descriptionTask2", Status.IN_PROGRESS);
        taskManager.addTask(task2);

        epic1 = new Epic("epic1", "descriptionEpic1");
        taskManager.addEpic(epic1);

        subtask1 = new Subtask("subtask1", "descriptionSubtask1", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
    }

    // проверка на добавление задач разного типа и поиска их по id
    @Test
    void shouldBeNotNullWhenAddDifferentTypesOfTasksAndFindByID() {
        assertNotNull(taskManager.getTask(task1.getId()));
        assertNotNull(taskManager.getEpic(epic1.getId()));
        assertNotNull(taskManager.getSubtask(subtask1.getId()));
    }

    // проверка на конфликт между заданным и сгенерированным id внутри менеджера
    @Test
    void shouldBeNotConflictWhenAddTaskWithGeneratedAndGivenID() {
        task1.setId(100);
        taskManager.addTask(task1);
        assertNotEquals(task1.getId(), task2.getId(),
                "Задачи с заданным и сгенерированным id не должны конфликтовать");
    }

    // проверка на неизменность задачи (по всем полям) при её добавлении в менеджер
    @Test
    void shouldNotChangeTasksWhenAddedToManager() {
        Task addedTask = taskManager.getTask(task1.getId());

        assertEquals(task1.getName(), addedTask.getName(), "Имя задачи должно остаться неизменным");
        assertEquals(task1.getDescription(), addedTask.getDescription(), "Описание задачи должно остаться неизменным");
        assertEquals(task1.getStatus(), addedTask.getStatus(), "Статус задачи должен остаться неизменным");
    }

    // проверка на то, что удаляемые подзадачи не должны хранить внутри себя старые id
    @Test
    void shouldBeNullWhenSubtaskRemoved() {
        Epic epic = new Epic("epic1", "description1");
        epic.setId(1);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask1", "description1", Status.NEW, epic.getId());
        subtask.setId(1);
        taskManager.addSubtask(subtask);

        epic.getSubtasksInEpic().add(subtask.getIdEpic());
        taskManager.deleteSubtask(subtask.getId());

        assertFalse(epic.getSubtasksInEpic().contains(subtask.getId()));
        assertNull(taskManager.getSubtask(subtask.getId()));
    }

    // проверка на то, что внутри эпиков не должно оставаться неактуальных id подзадач
    @Test
    void shouldBeFalseWhenSubtaskRemovedFromAnEpic() {
        Epic epic = new Epic("epic1", "description1");
        epic.setId(1);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask1", "description1", Status.NEW, epic.getId());
        subtask.setId(1);
        taskManager.addSubtask(subtask);

        taskManager.deleteSubtask(subtask.getId());
        assertFalse(epic.getSubtasksInEpic().contains(subtask.getId()));
    }

    // проверка на корректное изменение данных через сеттеры объектов задач
    @Test
    void shouldBeEqualsWhenDataWasChangedViaSetters() {
        Task task = new Task("task1", "description1", Status.NEW);
        task.setId(1);
        taskManager.addTask(task);

        task.setDescription("new description");
        task.setStatus(Status.IN_PROGRESS);

        taskManager.updateTask(task.getId(), task);

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals("new description", updatedTask.getDescription());
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
    }
}