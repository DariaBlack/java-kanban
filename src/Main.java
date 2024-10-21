public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Купить продукты", "Описание задачи №1", Status.NEW);
        Task task2 = new Task("Уборка", "Убраться в квартире", Status.IN_PROGRESS);
        Task task3 = new Task("Теория 4 спринта", "Прочитать теорию:)", Status.DONE);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        Epic epic1 = new Epic("Переезд", "Описание эпика №1");
        Epic epic2 = new Epic("Что-то грандиозное!", "Ух");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Собрать вещи", "В старой квартире", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Распаковать вещи", "В новой квартире", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Первая часть грандиозного", "Вау", Status.DONE, epic2.getId());
        Subtask subtask4 = new Subtask("Вторая часть грандиозного", "Ого", Status.DONE, epic2.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        printAllTasks(taskManager);

        final Task task = taskManager.getTask(task1.getId());
        task.setStatus(Status.DONE);
        taskManager.updateTask(task1.getId(), task1);
        printAllTasks(taskManager);

        System.out.println("DELETE: " + task1.getName());
        taskManager.deleteTask(task1.getId());
        System.out.println("DELETE: " + epic1.getName());
        taskManager.deleteEpic(epic1.getId());
        printAllTasks(taskManager);

        System.out.println("DELETE ALL TASKS:");
        taskManager.deleteAllTasks();
        printAllTasks(taskManager);

    }

    public static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task t : taskManager.getTasks()) {
            System.out.println(t.toString());
        }

        System.out.println("Эпики:");
        for (Epic e : taskManager.getEpics()) {
            System.out.println(e.toString());
            for (Subtask sub : taskManager.getSubtasks(e.getId())) {
                System.out.println("--> " + sub.toString());
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask sub : taskManager.getSubtasks()) {
            System.out.println(sub.toString());
        }
    }
}