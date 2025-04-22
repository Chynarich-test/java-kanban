package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Long id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, long id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.valueOf(status);
    }

    public Task(String name, String description, long id, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, long id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(Task task) {
        this.name = task.name;
        this.description = task.description;
        this.id = task.id;
        this.status = task.status;
        this.duration = task.duration;
        this.startTime = task.startTime;
    }

    public Task(Task task, long id) {
        this.name = task.name;
        this.description = task.description;
        this.id = id;
        this.status = task.status;
        this.duration = task.duration;
        this.startTime = task.startTime;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (this.id != null && task.id != null) {
            return Objects.equals(this.id, task.id);
        }
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return Objects.hash(name, description, status);
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
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

    public Type getType() {
        return Type.TASK;
    }
}
