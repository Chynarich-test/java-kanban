package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Long> subtasksIDs = new ArrayList<>();

    public Epic(String name, String description, long id, Status status) {
        super(name, description, id, status);
        this.type = Type.EPIC;
    }

    public Epic(String name, String description, long id, String status, ArrayList<Long> subtasksIDs) {
        super(name, description, id, status);
        this.type = Type.EPIC;
        this.subtasksIDs = subtasksIDs;
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.type = Type.EPIC;
    }

    public Epic(Epic epic, long id) {
        super(epic.getName(), epic.getDescription(), id, epic.getStatus());
        this.type = Type.EPIC;
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getStatus());
        this.type = Type.EPIC;
    }

    public void addSubTask(Long subtask) {
        if (subtask == getId()) return;
        subtasksIDs.add(subtask);
    }

    public ArrayList<Long> getSubtasks() {
        return subtasksIDs;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasksIDs +
                "} " + super.toString();
    }

    @Override
    public String toCsvString() {
        StringBuilder outString = new StringBuilder(super.toCsvString());
        for (Long item : subtasksIDs) {
            outString.append(item).append(";");
        }
        return super.toCsvString() + "," + outString;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIDs, epic.subtasksIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIDs);
    }
}
