package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class InMemoryTaskManager implements TaskManager {
    private static long maxID = 0;
    private final HashMap<Long, Task> tasks = new HashMap<>();
    private final HashMap<Long, Epic> epics = new HashMap<>();
    private final HashMap<Long ,Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyDB = Managers.getDefaultHistory();

    @Override
    public void addTask(Task task) {
        long taskID = generateID();
        tasks.put(taskID, new Task(task, taskID));
    }

    @Override
    public void addEpic(Epic epic) {
        long epicID = generateID();
        epics.put(epicID, new Epic(epic, epicID));
    }

    @Override
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

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks(){
        subtasks.clear();
        for(Epic epic : epics.values()){
            epic.getSubtasks().clear();
            epic.setStatus(Status.NEW);
        }
    }

    //Сделал 3 метода когда пользователь знает что он хочет вернуть и когда не знает какой группе, принадлежит его айди
    @Override
    public Task getObjectAnID(long ID) {
        if(tasks.containsKey(ID)){
            historyDB.add(tasks.get(ID));
            return tasks.get(ID);
        }
        if(epics.containsKey(ID)){
            historyDB.add(epics.get(ID));
            return epics.get(ID);
        }
        if(subtasks.containsKey(ID)){
            historyDB.add(subtasks.get(ID));
            return subtasks.get(ID);
        }
        System.out.println("Объект не найден");
        return null;
    }

    @Override
    public Task getTaskAnID(long ID) {
        if(tasks.containsKey(ID)){
            historyDB.add(tasks.get(ID));
            return tasks.get(ID);
        }
        System.out.println("Объект не найден");
        return null;
    }

    @Override
    public Epic getEpicAnID(long ID) {
        if(epics.containsKey(ID)){
            historyDB.add(epics.get(ID));
            return epics.get(ID);
        }
        System.out.println("Объект не найден");
        return null;
    }

    @Override
    public Subtask getSubtaskAnID(long ID) {
        if(subtasks.containsKey(ID)){
            historyDB.add(subtasks.get(ID));
            return subtasks.get(ID);
        }
        System.out.println("Объект не найден");
        return null;
    }

    //Тут не вижу смысла делать разные методы под разные группы, в любом случае айди уникальный
    @Override
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
            checkStatus(epics.get(subtasks.get(ID).getIDEpic()));
            subtasks.remove(ID);
            return;
        }
        System.out.println("Объект не найден");
    }

    @Override
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

    @Override
    public void updateTask(Task task) {
        Task updatableTask = tasks.get(task.getID());
        updatableTask.setName(task.getName());
        updatableTask.setDescription(task.getDescription());
        updatableTask.setStatus(task.getStatus());
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic updatableEpic = epics.get(epic.getID());
        updatableEpic.setName(epic.getName());
        updatableEpic.setDescription(epic.getDescription());
        checkStatus(updatableEpic);
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyDB.getHistory();
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


    private long generateID() {
        return maxID++;
    }


}
