package tasks;

import java.util.Objects;

public class Task {
    protected Type type;
    private String name;
    private String description;
    private long id;
    private Status status;

    public Task(String name, String description, long id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.valueOf(status);
        this.type = Type.TASK;
    }

    public Task(String name, String description, long id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(Task task) {
        this.name = task.name;
        this.description = task.description;
        this.id = task.id;
        this.status = task.status;
        this.type = Type.TASK;
    }

    public Task(Task task, long id) {
        this.name = task.name;
        this.description = task.description;
        this.id = id;
        this.status = task.status;
        this.type = Type.TASK;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public String toCsvString() {
        return id + "," + type + "," + name + "," + description + "," + status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Double.compare(id, task.id) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
