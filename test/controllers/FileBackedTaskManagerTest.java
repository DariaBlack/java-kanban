package controllers;

import controllers.exceptions.ManagerSaveException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;


public class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("test", "csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    // проверка на сохранение и загрузку пустого файла
    @Test
    void saveAndLoadEmptyFile() throws IOException {
        fileBackedTaskManager.save();

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(1, lines.size(), "В файле должен быть только заголовок.");

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);
        assertTrue(load.getTasks().isEmpty(), "Список задач должен быть пустым.");
        assertTrue(load.getEpics().isEmpty(), "Список эпиков должен быть пустым.");
        assertTrue(load.getSubtasks().isEmpty(), "Список подзадач должен быть пустым.");
    }

    // проверка на сохранение нескольких задач
    @Test
    void saveAndLoadMultipleTasks() {
        Task task = new Task("task1", "descriptionTask1", Status.NEW);
        task.setDuration(20);
        task.setStartTime(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        fileBackedTaskManager.addTask(task);
        Epic epic = new Epic("epic1", "descriptionEpic1");
        fileBackedTaskManager.addEpic(epic);
        Subtask subtask = new Subtask("subtask1", "descriptionSubtask1", Status.NEW, epic.getId());
        subtask.setDuration(60);
        subtask.setStartTime(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        fileBackedTaskManager.addSubtask(subtask);

        fileBackedTaskManager.save();

        FileBackedTaskManager load = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = load.getTasks();
        assertEquals(1, tasks.size(), "Должна быть 1 задача.");
        assertEquals(task, tasks.get(0), "Сохранённая задача не та же, что и была загружена.");

        List<Epic> epics = load.getEpics();
        assertEquals(1, epics.size(), "Должен быть 1 эпик.");
        assertEquals(epic, epics.get(0), "Сохранённый эпик не тот же, что и был загружен.");

        List<Subtask> subtasks = load.getSubtasks();
        assertEquals(1, subtasks.size(), "Должна быть 1 подзадача.");
        assertEquals(subtask, subtasks.get(0), "Сохранённая подзадача не та же, что и была загружена.");
    }

    // Тест на исключение при попытке загрузить несуществующий файл
    @Test
    void shouldThrowIOExceptionWhenFileDoesNotExist() {
        File nonExistentFile = new File("non_existent_file.csv");
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(nonExistentFile);
        }, "Загрузка несуществующего файла должна вызвать IOException");
    }

    // Тест на успешную запись в файл
    @Test
    void shouldNotThrowIOExceptionWhenFileIsWritable() throws IOException {
        assertDoesNotThrow(() -> {
            fileBackedTaskManager.save();
        }, "Запись в файл должна быть успешной");
    }

    // Тест на успешное чтение из существующего файла
    @Test
    void shouldNotThrowIOExceptionWhenFileExists() throws IOException {
        fileBackedTaskManager.save();
        assertDoesNotThrow(() -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Чтение существующего файла не должно вызывать исключений");
    }
}