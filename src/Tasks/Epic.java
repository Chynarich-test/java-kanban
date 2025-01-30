package Tasks;

import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description, double ID, String status) {
        super(name, description, ID, status);
    }
    public void addSubTask(Subtask subtask){
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                "} " + super.toString();
    }


}
