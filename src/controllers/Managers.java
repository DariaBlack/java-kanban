package controllers;

import controllers.interfaces.HistoryManager;
import controllers.interfaces.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}