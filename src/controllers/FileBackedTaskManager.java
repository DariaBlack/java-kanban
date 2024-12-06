package controllers;

import controllers.exceptions.ManagerSaveException;
import controllers.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;

    }

    public void save() throws IOException {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                fileWriter.write( taskToString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(epicToString(epic) + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(subtaskToString(subtask)+ "\n");
            }
        } catch (RuntimeException e) {
            throw new ManagerSaveException("Ошибка при попытке сохранения.");
        }
    }

    private String taskToString(Task task) {
        return task.getId() + ",TASK," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",";
    }

    private String epicToString(Epic epic) {
        return epic.getId() + ",TASK," + epic.getName() + "," + epic.getStatus() + "," + epic.getDescription() + ",";
    }

    private String subtaskToString(Subtask subtask) {
        return subtask.getId() + ",TASK," + subtask.getName() + "," + subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getIdEpic();
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file); // ещё что-то? чтение бакета?
        return fileBackedTaskManager;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        super.updateSubtask(id, subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
