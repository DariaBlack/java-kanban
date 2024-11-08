package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int maxHistorySize = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= maxHistorySize) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getList() {
        return new ArrayList<>(history);
    }

    @Override
    public String toString() {
        return history.toString();
    }
}