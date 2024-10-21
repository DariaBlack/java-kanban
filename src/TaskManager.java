import java.util.HashMap;
import java.util.List;

public class TaskManager {
    HashMap<Integer, Task> getTasks() {} // получение списка всех задач
    void deleteAllTasks() {} // удаление всех задач
    Task getTask(int id) {} // получение задачи по id
    void addTask(Task task) {} // создание задачи, сама задача в параметре
    void updateTask(int id, Task task) {} // обновление задачи, новая версия задачи + id в параметре
    void deleteTask (int id) {} // удаление задачи по id
    HashMap<Integer, Subtask> getSubtasks(int id) {} // получение всех подзадач эпика
}