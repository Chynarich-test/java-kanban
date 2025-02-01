package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public final class Manager {
    private static long maxID = 0;
    //Не вижу смысла создавать третий объект с тасками, потому что только эти два объекта полностью удовлетворяют
    //условиям, если нужна задача без подзадач то это будет эпик с пустыми сабтасками.
    //Если понадобится в будущих заданиях использовать голый объект таска, то уже там добавлю.
    private final HashMap<Long, Epic> epics = new HashMap<>();
    private final HashMap<Long ,Subtask> subtasks = new HashMap<>();

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

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks(){
        return new ArrayList<>(subtasks.values());
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
    public Object getTaskAnID(long ID) {
        if(epics.containsKey(ID)){
            return epics.get(ID);
        }
        if(subtasks.containsKey(ID)){
            return subtasks.get(ID);
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

    public void updateEpic(Epic epic) {
        Epic updatableEpic = epics.get(epic.getID());
        updatableEpic.setName(epic.getName());
        updatableEpic.setDescription(epic.getDescription());
        updatableEpic.setStatus(epic.getStatus());
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

    public void setStatusEpic(Epic epic, Status newStatus)
    {
        if(epic == null) return;
        if(epic.getSubtasks().isEmpty()){
            epic.setStatus(newStatus);
            return;
        }
        System.out.println("У эпика есть связанные сабтаски, которые мешают смене статуса");
    }

    public void setStatusSubtask(Subtask subtask, Status newStatus)
    {
        if(subtask == null) return;
        subtask.setStatus(newStatus);
        checkStatus(epics.get(subtask.getIDEpic()));
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
