package controllers;

import controllers.interfaces.HistoryManager;
import controllers.interfaces.TaskManager;
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

        /*
        "Дата начала задачи по каким-то причинам может быть не задана. Тогда при добавлении её не следует учитывать в
        списке задач и подзадач, отсортированных по времени начала. Такая задача не влияет на приоритет других, а при
        попадании в список может сломать логику работы компаратора." - я поняла эту часть задания так, что задачи без
        даты начала вообще в список отсортированных по времени задач попадать не должны.
         */
        if (startTime == null) {
            return false;
        }

        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(aTask -> {
                    LocalDateTime setStartTime = aTask.getStartTime();
                    LocalDateTime setEndTime = aTask.getEndTime();

                    return !(startTime.isAfter(setEndTime) || endTime.isBefore(setStartTime));
                });

        if (isOverlapping) {
            return false;
        }

        sortedTask.add(task);
        return true;
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
            epic.updateStatus(this);

        }
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        addTaskPriority(task);

        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        addTaskPriority(subtask);

        Epic epic = epics.get(subtask.getIdEpic());

        if (epic == null) {
            return;
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        epic.getSubtasksInEpic().add(subtask.getId());
        epic.updateStatus(this);
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
                epic.updateStatus(this);
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        Task removedTask = tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);

        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            for (Integer subtaskId : removedEpic.getSubtasksInEpic()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    @Override
    public void deleteSubtask(int id) {
        historyManager.remove(id);

        Subtask removedSubtask = subtasks.remove(id);
        if (removedSubtask != null) {
            Epic epic = epics.get(removedSubtask.getIdEpic());
            if (epic != null) {
                epic.getSubtasksInEpic().remove((Integer) id);
                epic.updateStatus(this);
            }
        }
    }


    @Override
    public List<Subtask> getSubtasks(int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) return new ArrayList<>();

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