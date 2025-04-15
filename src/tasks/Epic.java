package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Long> subtasksIDs = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, long id, Status status) {
        super(name, description, id, status);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(Epic epic, long id) {
        super(epic.getName(), epic.getDescription(), id, epic.getStatus());
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

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
