package Tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private final double ID;
    private Status status;

    public Task(String name, String description, double ID, String status){
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.status = Status.valueOf(status);
    }

    public Task(Task task){
        this.name = task.name;
        this.description = task.description;
        this.ID = task.ID;
        this.status = task.status;
    }

    public double getID() {
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
}
