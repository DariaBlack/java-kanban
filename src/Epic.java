import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksInEpic;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasksInEpic = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksInEpic() {
        return subtasksInEpic;
    }
}