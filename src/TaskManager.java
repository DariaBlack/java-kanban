import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    int nextId;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        nextId = 1;
    }

    List getTasks() {
        return new ArrayList<>(tasks.values());
        // Нашла информацию, что если возвращать новым списком значения, то не будет доступа напрямую к внутряшке
        // HashMap, что более безопасно.
    }

    List getEpics() {
        return new ArrayList<>(epics.values());
    }

    List getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    void deleteAllTasks() {
        tasks.clear();
    }

    void deleteAllEpics() {
        epics.clear();
    }

    void deleteAllSubtasks() {
        subtasks.clear();
    }

    Task getTask(int id) {
        return tasks.get(id);
    }

    Task getEpic(int id) {
        return epics.get(id);
    }

    Task getSubtask(int id) {
        return subtasks.get(id);
    }

    void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
    }

    void updateTask(int id, Task task) {
        tasks.put(id, task);
    }

    void updateEpic(int id, Epic epic) {
        epics.put(id, epic);
    }

    void updateSubtask(int id, Subtask subtask) {
        subtasks.put(id, subtask);
    }

    void deleteTask(int id) {
        tasks.remove(id);
    }

    void deleteEpic(int id) {
        epics.remove(id);
    }

    void deleteSubtask(int id) {
        subtasks.remove(id);
    }

    List getSubtasks(int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) return new ArrayList<>();
        return epic.getSubtasksInEpic();
    }
}