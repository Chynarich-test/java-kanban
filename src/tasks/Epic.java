package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private final ArrayList<Long> subtasksIDs = new ArrayList<>();

    public Epic(String name, String description, long ID, Status status) {
        super(name, description, ID, status);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(Epic epic, long ID){
        super(epic.getName(), epic.getDescription(), ID, epic.getStatus());
    }

    public Epic(Epic epic){
        super(epic.getName(), epic.getDescription(), epic.getStatus());
    }

    public void addSubTask(Long subtask){
        if (subtask == getID()) return;
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
