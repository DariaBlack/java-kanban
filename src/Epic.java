import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subtasksInEpic;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasksInEpic = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksInEpic() {
        return subtasksInEpic;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtasksInEpic=" + subtasksInEpic +
                '}';
    }

    public void updateStatus(TaskManager taskManager) {
        List<Subtask> subtasks = taskManager.getSubtasks(this.getId());
        if (subtasks.isEmpty()) {
            this.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
        }

        if (allDone) {
            this.setStatus(Status.DONE);
        } else if (anyInProgress) {
            this.setStatus(Status.IN_PROGRESS);
        } else {
            this.setStatus(Status.NEW);
        }
    }
}