package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public final class Manager {
    private static long maxID = 0;
    private final HashMap<Long, Task> tasks = new HashMap<>();
    private final HashMap<Long, Epic> epics = new HashMap<>();
    private final HashMap<Long ,Subtask> subtasks = new HashMap<>();

    public void addTask(Task task) {
        long taskID = generateID();
        tasks.put(taskID, new Task(task, taskID));
    }

    public void addEpic(Epic epic) {
        long epicID = generateID();
        epics.put(epicID, new Epic(epic, epicID));
    }

    public void addSubtaskToEpic(Subtask subtask) {
        if(!epics.containsKey(subtask.getIDEpic())){
            System.out.println("Ошибка: Эпик с ID " + subtask.getIDEpic() + " не найден.");
            return;
        }
        Epic epic = epics.get(subtask.getIDEpic());
        long newSubtaskID = generateID();
        subtasks.put(newSubtaskID, new Subtask(subtask, newSubtaskID, subtask.getIDEpic()));
        epic.addSubTask(newSubtaskID);
        checkStatus(epic);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks(){
        subtasks.clear();
        for(Epic epic : epics.values()){
            epic.getSubtasks().clear();
            checkStatus(epic);
        }
    }

    //Сделал 3 метода когда пользователь знает что он хочет вернуть и когда не знает какой группе, принадлежит его айди
    public Object getObjectAnID(long ID) {
        if(tasks.containsKey(ID)){
            return tasks.get(ID);
        }
        if(epics.containsKey(ID)){
            return epics.get(ID);
        }
        if(subtasks.containsKey(ID)){
            return subtasks.get(ID);
        }
        System.out.println("Объект не найден");
        return null;
    }

    public Task getTaskAnID(long ID) {
        if(tasks.containsKey(ID)){
            return tasks.get(ID);
        }
        System.out.println("Объект не найден");
        return null;
    }

    public Epic getEpicAnID(long ID) {
        if(epics.containsKey(ID)){
            return epics.get(ID);
        }
        System.out.println("Объект не найден");
        return null;
    }

    public Subtask getSubtaskAnID(long ID) {
        if(subtasks.containsKey(ID)){
            return subtasks.get(ID);
        }
        System.out.println("Объект не найден");
        return null;
    }

    //Тут не вижу смысла делать разные методы под разные группы, в любом случае айди уникальный
    public void deleteTaskAnID(long ID) {
        if(tasks.containsKey(ID)){
            tasks.remove(ID);
            return;
        }
        if(epics.containsKey(ID)){
            Epic epic = epics.get(ID);
            for(Long subtask : epic.getSubtasks()){
                subtasks.remove(subtask);
            }
            epics.remove(ID);
            return;
        }
        if(subtasks.containsKey(ID)){
            epics.get(subtasks.get(ID).getIDEpic()).getSubtasks().remove(ID);
            subtasks.remove(ID);

            return;
        }
        System.out.println("Объект не найден");
    }

    public ArrayList<Subtask> getSubtasksByID(long ID){
        if(!epics.containsKey(ID)){
            System.out.println("Объект не найден");
            return null;
        }
        ArrayList<Long> subtasksIDs = epics.get(ID).getSubtasks();
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for(Long subtaskID : subtasksIDs){
            subtasks.add(getSubtaskAnID(subtaskID));
        }
        return subtasks;
    }

    public void updateTask(Task task) {
        Task updatableTask = tasks.get(task.getID());
        updatableTask.setName(task.getName());
        updatableTask.setDescription(task.getDescription());
        updatableTask.setStatus(task.getStatus());
    }

    public void updateEpic(Epic epic) {
        Epic updatableEpic = epics.get(epic.getID());
        updatableEpic.setName(epic.getName());
        updatableEpic.setDescription(epic.getDescription());
        checkStatus(updatableEpic);
    }

    public void updateSubtask(Subtask subtask){
        Subtask updatableSubtask = subtasks.get(subtask.getID());
        long newEpicId = subtask.getIDEpic();
        long oldEpicId = updatableSubtask.getIDEpic();
        if(newEpicId != oldEpicId ){
            addSubtaskToEpic(subtask);
            deleteTaskAnID(subtask.getID());
            checkStatus(epics.get(newEpicId));
            checkStatus(epics.get(oldEpicId));
            return;
        }
        updatableSubtask.setName(subtask.getName());
        updatableSubtask.setDescription(subtask.getDescription());
        updatableSubtask.setStatus(subtask.getStatus());
        checkStatus(epics.get(newEpicId));
    }

    public void updateStatusTask(Task task, Status newStatus)
    {
        if(task == null) return;
        task.setStatus(newStatus);
    }

    private void checkStatus(Epic epic){
        boolean allEqualsNew = true;
        boolean allEqualsDone = true;
        ArrayList<Long> epicSubtasks = epic.getSubtasks();
        if(epicSubtasks.isEmpty()){
            epic.setStatus(Status.NEW);
            return;
        }
        for (Long subtask : epicSubtasks){
            if(subtasks.get(subtask).getStatus() != Status.DONE){
                allEqualsDone = false;
            }
            if(subtasks.get(subtask).getStatus() != Status.NEW){
                allEqualsNew = false;
            }
            if(subtasks.get(subtask).getStatus() == Status.IN_PROGRESS){
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        if(allEqualsDone) {
            epic.setStatus(Status.DONE);
            return;
        } else if(allEqualsNew){
            epic.setStatus(Status.NEW);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);
    }


    private static long generateID() {
        return maxID++;
    }


}
