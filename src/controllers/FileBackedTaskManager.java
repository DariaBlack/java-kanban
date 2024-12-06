package controllers;

import controllers.interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public FileBackedTaskManager() {

    }

    public void save() throws IOException {

    }

    static FileBackedTaskManager loadFromFile(File file) {

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
