package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private final long idEpic;

    public Subtask(String name, String description, long id, Status status, long idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
        this.type = Type.SUBTASK;
    }

    public Subtask(String name, String description, Status status, long idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
        this.type = Type.SUBTASK;
    }

    public Subtask(Subtask subtask, long id, long idEpic) {
        super(subtask.getName(), subtask.getDescription(), id, subtask.getStatus());
        this.idEpic = idEpic;
        this.type = Type.SUBTASK;
    }

    public Subtask(Subtask subtask, long idEpic) {
        super(subtask.getName(), subtask.getDescription(), subtask.getStatus());
        this.idEpic = idEpic;
        this.type = Type.SUBTASK;
    }

    public long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "IDEpic=" + idEpic +
                "} " + super.toString();
    }

    @Override
    public String toCsvString() {
        return super.toCsvString() + "," + idEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }
}
