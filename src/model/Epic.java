package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public int calculateDuration(List<Subtask> subtasks) {
        return subtasks.stream()
                .mapToInt(Subtask::getDuration)
                .sum();
    }

    public LocalDateTime calculateStartTime(List<Subtask> subtasks) {
        return subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public LocalDateTime calculateEndTime(List<Subtask> subtasks) {
        return subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
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

    public void updateStatus(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.setStatus(Status.NEW);
            return;
        }

        boolean allDone = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);
        boolean anyInProgress = subtasks.stream()
                .anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS);
        boolean hasNew = subtasks.stream()
                .anyMatch(subtask -> subtask.getStatus() == Status.NEW);
        boolean hasDone = subtasks.stream()
                .anyMatch(subtask -> subtask.getStatus() == Status.DONE);

        if (hasNew && hasDone) {
            this.setStatus(Status.IN_PROGRESS);
        } else if (allDone) {
            this.setStatus(Status.DONE);
        } else if (anyInProgress) {
            this.setStatus(Status.IN_PROGRESS);
        } else {
            this.setStatus(Status.NEW);
        }

        this.setDuration(calculateDuration(subtasks));
        this.setStartTime(calculateStartTime(subtasks));
        this.setEndTime(calculateEndTime(subtasks));
    }
}