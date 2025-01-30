import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.util.ArrayList;

public final class Manager {
    private static double maxID = 0;
    private final ArrayList<Epic> epics = new ArrayList<>();

    public void addTask(String name, String description, String status) {
        epics.add(new Epic(name, description, generateID(), status));
    }

    public void addSubtaskToEpic(double epicID, String name, String description, String status) {
        for (Epic epic : epics) {
            if (epic.getID() == epicID) {
                Subtask newSubtask = new Subtask(name, description, generateID(), status);
                epic.addSubTask(newSubtask);
                checkStatus(epic);
                return;
            }
        }
        System.out.println("Ошибка: Эпик с ID " + epicID + " не найден.");
    }

    public ArrayList<Epic> getTasks() {
        return epics;
    }

    public void deleteAllTasks() {
        epics.clear();
    }

    public Task getTaskAnID(double ID) {
        for (Epic epic : epics) {
            if (epic.getID() == ID) {
                return epic;
            }
            for (Subtask subtask : epic.getSubtasks()) {
                if (subtask.getID() == ID) {
                    return subtask;
                }
            }
        }
        System.out.println("Объект не найден");
        return null;
    }

    public void deleteTaskAnID(double ID) {
        for (int i = 0; i < epics.size(); i++) {
            if (epics.get(i).getID() == ID) {
                epics.remove(i);
                return;
            }
            for(int j = 0; j < epics.get(i).getSubtasks().size(); j++){
                if (epics.get(j).getSubtasks().get(j).getID() == ID) {
                    epics.get(j).getSubtasks().remove(j);
                    return;
                }
            }
        }
    }

    public ArrayList<Subtask> getSubtasksByID(double ID){
        for (Epic epic : epics) {
            if (epic.getID() == ID) {
                return epic.getSubtasks();
            }
        }
        System.out.println("Объект не найден");
        return null;
    }

    public void updateTask(Epic task, ArrayList<Subtask> subtasks) {
        for (int i = 0; i < epics.size(); i++) {
            if (epics.get(i).getID() == task.getID()) {
                epics.set(i, task);
                Epic newEpic = epics.get(i);
                if (!subtasks.isEmpty()) {
                    for (Subtask subtask : subtasks) {
                        newEpic.addSubTask(subtask);
                    }
                }
            }
        }
    }

    public void setStatus(Task task, Status status)
    {
        task.setStatus(status);
        if (task instanceof Subtask) {
            Epic parentEpic = findEpicBySubtask((Subtask) task);
            if (parentEpic != null) {
                checkStatus(parentEpic);
            }
        }
    }

    private Epic findEpicBySubtask(Subtask subtask) {
        for (Epic epic : epics) {
            if (epic.getSubtasks().contains(subtask)) {
                return epic;
            }
        }
        return null;
    }

    public void checkStatus(Epic task){
        boolean allEqualsNew = true;
        boolean allEqualsDone = true;
        ArrayList<Subtask> epicSubtasks = task.getSubtasks();
        if(epicSubtasks.isEmpty()){
            task.setStatus(Status.NEW);
            return;
        }
        for (Subtask subtask : epicSubtasks){
            if(subtask.getStatus() != Status.DONE){
                allEqualsDone = false;
            }
            if(subtask.getStatus() != Status.NEW){
                allEqualsNew = false;
            }
        }
        if(allEqualsDone) {
            task.setStatus(Status.DONE);
            return;
        } else if(allEqualsNew){
            task.setStatus(Status.NEW);
            return;
        }
        task.setStatus(Status.IN_PROGRESS);

    }


    private static double generateID() {
        return maxID++;
    }


}
