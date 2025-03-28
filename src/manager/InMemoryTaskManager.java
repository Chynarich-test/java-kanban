package manager;

import manager.history.HistoryManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Long, Task> tasks = new HashMap<>();
    protected final HashMap<Long, Epic> epics = new HashMap<>();
    protected final HashMap<Long, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyDB = Managers.getDefaultHistory();
    protected long maxID = 0;

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
        if (!epics.containsKey(subtask.getIdEpic())) {
            System.out.println("Ошибка: Эпик с ID " + subtask.getIdEpic() + " не найден.");
            return;
        }
        Epic epic = epics.get(subtask.getIdEpic());
        long newSubtaskID = generateID();
        subtasks.put(newSubtaskID, new Subtask(subtask, newSubtaskID, subtask.getIdEpic()));
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
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        historyDB.remove(new ArrayList<>(tasks.values()));
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        historyDB.remove(new ArrayList<>(epics.values()));
        historyDB.remove(new ArrayList<>(subtasks.values()));
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        historyDB.remove(new ArrayList<>(subtasks.values()));
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(Status.NEW);
        }
    }

    //Сделал 3 метода когда пользователь знает что он хочет вернуть и когда не знает какой группе, принадлежит его айди
    @Override
    public Task getObjectAnID(long id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyDB.add(task);
            return task;
        }
        if (epics.containsKey(id)) {
            Task epic = epics.get(id);
            historyDB.add(epic);
            return epic;
        }
        if (subtasks.containsKey(id)) {
            Task subtask = subtasks.get(id);
            historyDB.add(subtask);
            return subtask;
        }
        System.out.println("Объект не найден");
        return null;
    }

    @Override
    public Task getTaskAnID(long id) {
        if (tasks.containsKey(id)) {
            historyDB.add(tasks.get(id));
            return tasks.get(id);
        }
        System.out.println("Объект не найден");
        return null;
    }

    @Override
    public Epic getEpicAnID(long id) {
        if (epics.containsKey(id)) {
            historyDB.add(epics.get(id));
            return epics.get(id);
        }
        System.out.println("Объект не найден");
        return null;
    }

    @Override
    public Subtask getSubtaskAnID(long id) {
        if (subtasks.containsKey(id)) {
            historyDB.add(subtasks.get(id));
            return subtasks.get(id);
        }
        System.out.println("Объект не найден");
        return null;
    }

    //Тут не вижу смысла делать разные методы под разные группы, в любом случае айди уникальный
    @Override
    public void deleteTaskAnID(long id) {
        if (tasks.containsKey(id)) {
            historyDB.remove(id);
            tasks.remove(id);
            return;
        }
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Long subtask : epic.getSubtasks()) {
                historyDB.remove(subtask);
                subtasks.remove(subtask);
            }
            historyDB.remove(id);
            epics.remove(id);
            return;
        }
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getIdEpic()).getSubtasks().remove(id);
            checkStatus(epics.get(subtasks.get(id).getIdEpic()));
            subtasks.remove(id);
            historyDB.remove(id);
            return;
        }
        System.out.println("Объект не найден");
    }

    @Override
    public ArrayList<Subtask> getSubtasksByID(long id) {
        if (!epics.containsKey(id)) {
            System.out.println("Объект не найден");
            return null;
        }
        ArrayList<Long> subtasksIDs = epics.get(id).getSubtasks();
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Long subtaskID : subtasksIDs) {
            subtasks.add(getSubtaskAnID(subtaskID));
        }
        return subtasks;
    }

    @Override
    public void updateTask(Task task) {
        Task updatableTask = tasks.get(task.getId());
        updatableTask.setName(task.getName());
        updatableTask.setDescription(task.getDescription());
        updatableTask.setStatus(task.getStatus());
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic updatableEpic = epics.get(epic.getId());
        updatableEpic.setName(epic.getName());
        updatableEpic.setDescription(epic.getDescription());
        checkStatus(updatableEpic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask updatableSubtask = subtasks.get(subtask.getId());
        long newEpicId = subtask.getIdEpic();
        long oldEpicId = updatableSubtask.getIdEpic();
        if (newEpicId != oldEpicId) {
            addSubtaskToEpic(subtask);
            deleteTaskAnID(subtask.getId());
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

    protected void checkStatus(Epic epic) {
        boolean allEqualsNew = true;
        boolean allEqualsDone = true;
        ArrayList<Long> epicSubtasks = epic.getSubtasks();
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (Long subtask : epicSubtasks) {
            if (subtasks.get(subtask).getStatus() != Status.DONE) {
                allEqualsDone = false;
            }
            if (subtasks.get(subtask).getStatus() != Status.NEW) {
                allEqualsNew = false;
            }
            if (subtasks.get(subtask).getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        if (allEqualsDone) {
            epic.setStatus(Status.DONE);
            return;
        } else if (allEqualsNew) {
            epic.setStatus(Status.NEW);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);
    }


    protected long generateID() {
        return maxID++;
    }


}
