package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private final long IDEpic;

    public Subtask(String name, String description, long ID, Status status, long IDEpic) {
        super(name, description, ID, status);
        this.IDEpic = IDEpic;
    }

    public Subtask(String name, String description, Status status, long IDEpic) {
        super(name, description, status);
        this.IDEpic = IDEpic;
    }

    public Subtask(Subtask subtask, long ID, long IDEpic) {
        super(subtask.getName(), subtask.getDescription(), ID, subtask.getStatus());
        this.IDEpic = IDEpic;
    }

    public Subtask(Subtask subtask, long IDEpic) {
        super(subtask.getName(), subtask.getDescription(), subtask.getStatus());
        this.IDEpic = IDEpic;
    }

    public long getIDEpic() {
        return IDEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "IDEpic=" + IDEpic +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), IDEpic);
    }
}
