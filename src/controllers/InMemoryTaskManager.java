package controllers;

import controllers.exceptions.NotFoundException;
import controllers.interfaces.HistoryManager;
import controllers.interfaces.TaskManager;
import controllers.exceptions.TimeOverlapException;
import model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int nextId;
    private HistoryManager historyManager;
    private final Set<Task> sortedTask = new TreeSet<>((time1, time2) -> {
        if (time1.getStartTime() == null) {
            if (time2.getStartTime() == null) {
                return 0;
            }
            return 1;
        }
        return time1.getStartTime().compareTo(time2.getStartTime());
    });

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        nextId = 1;
        historyManager = Managers.getDefaultHistory();
    }

    public boolean addTaskPriority(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        if (startTime == null) {
            return false;
        }

        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(aTask -> {
                    LocalDateTime setStartTime = aTask.getStartTime();
                    LocalDateTime setEndTime = aTask.getEndTime();

                    return !(startTime.isAfter(setEndTime) || endTime.isBefore(setStartTime));
                });

        return !isOverlapping;
    }

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTask);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            for (Integer subtaskId : epic.getSubtasksInEpic()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }

        for (Epic epic : epics.values()) {
            epic.getSubtasksInEpic().clear();
            epic.updateStatus(getSubtasks(epic.getId()));

        }
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        if (task == null) {
            throw new NotFoundException("Задача с ID " + id + " не найдена");
        }

        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task getEpic(int id) {
        Epic epic = epics.get(id);

        if (epic == null) {
            throw new NotFoundException("Эпик с ID " + id + " не найден");
        }

        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task getSubtask(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask == null) {
            throw new NotFoundException("Эпик с ID " + id + " не найден");
        }

        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        if (addTaskPriority(task)) {
            task.setId(nextId++);
            tasks.put(task.getId(), task);
            sortedTask.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (addTaskPriority(subtask)) {
            Epic epic = epics.get(subtask.getIdEpic());

            if (epic == null) {
                return;
            }

            subtask.setId(nextId++);
            subtasks.put(subtask.getId(), subtask);
            sortedTask.add(subtask);
            epic.getSubtasksInEpic().add(subtask.getId());
            epic.updateStatus(getSubtasks(epic.getId()));
        }
    }

    @Override
    public void updateTask(int id, Task task) {
        addTaskPriority(task);

        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.put(id, epic);
        }
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        addTaskPriority(subtask);

        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
            Epic epic = epics.get(subtask.getIdEpic());
            if (epic != null) {
                epic.updateStatus(getSubtasks(epic.getId()));
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            sortedTask.remove(removedTask);
        }
    }

    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);

        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtasksInEpic()) {
               Subtask removedSubtask = subtasks.remove(subtaskId);
               if (removedSubtask != null) {
                   sortedTask.remove(removedSubtask);
               }
            }
        }
    }

    @Override
    public void deleteSubtask(int id) {
        historyManager.remove(id);

        Subtask removedSubtask = subtasks.remove(id);
        if (removedSubtask != null) {
            sortedTask.remove(removedSubtask);
            Epic epic = epics.get(removedSubtask.getIdEpic());
            if (epic != null) {
                epic.getSubtasksInEpic().remove((Integer) id);
                epic.updateStatus(getSubtasks(epic.getId()));
            }
        }
    }

    @Override
    public List<Subtask> getSubtasks(int idEpic) {
        Epic epic = epics.get(idEpic);

        if (epic == null) {
            throw new NotFoundException("Эпик с ID " + idEpic + " не найден");
        }

        return epic.getSubtasksInEpic().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}