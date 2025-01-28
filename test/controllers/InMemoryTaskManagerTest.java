package controllers;

import controllers.exceptions.NotFoundException;
import controllers.interfaces.TaskManager;
import model.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class InMemoryTaskManagerTest {
    static TaskManager taskManager;
    static Task task1;
    static Task task2;
    static Epic epic1;
    static Subtask subtask1;
    static Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        epic1 = new Epic("epic1", "descriptionEpic1");

        subtask1 = new Subtask("subtask1", "descriptionSubtask1", Status.NEW, epic1.getId());
        subtask2 = new Subtask("subtask2", "descriptionSubtask2", Status.NEW, epic1.getId());

        task1 = new Task("task1", "descriptionTask1", Status.NEW);
        task2 = new Task("task2", "descriptionTask2", Status.IN_PROGRESS);
    }

    // проверка на добавление задач разного типа и поиска их по id
    @Test
    void shouldBeNotNullWhenAddDifferentTypesOfTasksAndFindByID() {
        taskManager.addTask(task1);
        taskManager.addSubtask(subtask1);

        epic1.getSubtasksInEpic().add(subtask1.getId());
        epic1.updateStatus(Arrays.asList(subtask1));
        taskManager.addEpic(epic1);

        assertEquals(epic1, taskManager.getEpic(epic1.getId()));
//        assertEquals(subtask1, taskManager.getSubtasks(subtask1.getId()));
//        assertEquals(task1, taskManager.getTask(task1.getId()));
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
        taskManager.addSubtask(subtask1);
        epic1.getSubtasksInEpic().add(subtask1.getId());
        epic1.updateStatus(Arrays.asList(subtask1));
        taskManager.addEpic(epic1);
        Task addedEpic = taskManager.getEpic(epic1.getId());

        assertEquals(epic1.getName(), addedEpic.getName(), "Имя задачи должно остаться неизменным");
        assertEquals(epic1.getDescription(), addedEpic.getDescription(), "Описание задачи должно остаться неизменным");
        assertEquals(epic1.getStatus(), addedEpic.getStatus(), "Статус задачи должен остаться неизменным");
    }

    // проверка на то, что удаляемые подзадачи не должны хранить внутри себя старые id
    @Test
    void shouldBeNullWhenSubtaskRemoved() {
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.deleteSubtask(subtask1.getId());

        try {
            taskManager.getSubtask(subtask1.getId());
            fail("Ожидалось исключение NotFoundException");
        } catch (NotFoundException e) {
            // Ожидаемое поведение
        }

        assertFalse(epic1.getSubtasksInEpic().contains(subtask1.getId()), "Подзадача не должна оставаться в эпике после удаления");
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
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        epic1.getSubtasksInEpic().add(subtask1.getId());
        epic1.getSubtasksInEpic().add(subtask2.getId());
        epic1.updateStatus(Arrays.asList(subtask1, subtask2));

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        epic1.updateStatus(Arrays.asList(subtask1, subtask2));

        taskManager.addEpic(epic1);
        Task updatedEpic = taskManager.getEpic(epic1.getId());

        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }
}