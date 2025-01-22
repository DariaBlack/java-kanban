package model;

import controllers.InMemoryTaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subtasksInEpic;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasksInEpic = new ArrayList<>();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Integer> getSubtasksInEpic() {
        return subtasksInEpic;
    }

    public int calculateDuration(InMemoryTaskManager taskManager) {
        int totalDuration = 0;

        for (Integer subtaskId : subtasksInEpic) {
            Subtask subtask = (Subtask) taskManager.getSubtask(subtaskId);
            totalDuration += subtask.getDuration();
        }
        return totalDuration;
    }

    public LocalDateTime calculateStartTime(InMemoryTaskManager taskManager) {
        LocalDateTime earliestStartTime = null;

        for (Integer subtaskId : subtasksInEpic) {
            Subtask subtask = (Subtask) taskManager.getSubtask(subtaskId);
            LocalDateTime subtaskStartTime = subtask.getStartTime();

            if (earliestStartTime == null || (subtaskStartTime != null && subtaskStartTime.isBefore(earliestStartTime))) {
                earliestStartTime = subtaskStartTime;
            }
        }
        return earliestStartTime;
    }

    public LocalDateTime calculateEndTime(InMemoryTaskManager taskManager) {
        LocalDateTime latestEndTime = null;

        for (Integer subtaskId : subtasksInEpic) {
            Subtask subtask = (Subtask) taskManager.getSubtask(subtaskId);
            LocalDateTime subtaskEndTime = subtask.getEndTime();

            if (latestEndTime == null || (subtaskEndTime != null && subtaskEndTime.isAfter(latestEndTime))) {
                latestEndTime = subtaskEndTime;
            }
        }
        return latestEndTime;
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.EPIC;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtasksInEpic=" + subtasksInEpic +
                '}';
    }

    public void updateStatus(InMemoryTaskManager taskManager) {
        List<Subtask> subtasks = taskManager.getSubtasks(this.getId());
        if (subtasks.isEmpty()) {
            this.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
                break;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            this.setStatus(Status.DONE);
        } else if (anyInProgress) {
            this.setStatus(Status.IN_PROGRESS);
        } else {
            this.setStatus(Status.NEW);
        }

        this.setDuration(calculateDuration(taskManager));
        this.setStartTime(calculateStartTime(taskManager));
        this.setEndTime(calculateEndTime(taskManager));
    }
}