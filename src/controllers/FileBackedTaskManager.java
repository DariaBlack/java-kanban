package controllers;

import controllers.exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                fileWriter.write(taskToString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(epicToString(epic) + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(subtaskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при попытке сохранения.");
        }
    }

    private String taskToString(Task task) {
        return task.getId() + ",TASK," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",";
    }

    private String epicToString(Epic epic) {
        return epic.getId() + ",EPIC," + epic.getName() + "," + epic.getStatus() + "," + epic.getDescription() + ",";
    }

    private String subtaskToString(Subtask subtask) {
        return subtask.getId() + ",SUBTASK," + subtask.getName() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getIdEpic();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.read();
        return fileBackedTaskManager;
    }

    private void read() {
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                Task task = fromString(line);
                if (task != null) {
                    // сделала, спасибо за рекомендацию
                    if (task.getType() == TypeOfTask.EPIC) {
                        addEpic((Epic) task);
                    } else if (task.getType() == TypeOfTask.SUBTASK) {
                        addSubtask((Subtask) task);
                    } else if (task.getType() == TypeOfTask.TASK) {
                        addTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при попытке чтения данных из файла.");
        }
    }

    // не заметила, что метод остался без модификатора доступа
    private static Task fromString(String value) {
        String[] parts = value.split(",");
        TypeOfTask typeOfTask = TypeOfTask.valueOf(parts[1]);
        Task task = null;

        if (typeOfTask == TypeOfTask.TASK) {
            task = new Task(parts[2], parts[4], Status.valueOf(parts[3]));
        } else if (typeOfTask == TypeOfTask.EPIC) {
            task = new Epic(parts[2], parts[4]);
        } else if (typeOfTask == TypeOfTask.SUBTASK) {
            task = new Subtask(parts[2], parts[4], Status.valueOf(parts[3]), Integer.parseInt(parts[5]));
        }
        return task;
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