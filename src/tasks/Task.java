package tasks;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private long ID;
    private Status status;

    public Task(String name, String description, long ID, String status){
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.status = Status.valueOf(status);
    }

    public Task(String name, String description, long ID, Status status){
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.status = status;
    }

    public Task(Task task){
        this.name = task.name;
        this.description = task.description;
        this.ID = task.ID;
        this.status = task.status;
    }

    public Task(Task task, long ID){
        this.name = task.name;
        this.description = task.description;
        this.ID = ID;
        this.status = task.status;
    }

    public Task(String name, String description, Status status){
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public long getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Double.compare(ID, task.ID) == 0 && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, ID, status);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
