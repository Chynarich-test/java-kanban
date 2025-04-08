package manager;

import manager.history.HistoryManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Long, Task> tasks = new HashMap<>();
    protected final HashMap<Long, Epic> epics = new HashMap<>();
    protected final HashMap<Long, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyDB = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>((s1, s2) -> {
        if (getStartTime(s1).isBefore(getStartTime(s2))) {
            return -1;
        } else if (getStartTime(s1).isAfter(getStartTime(s2))) {
            return 1;
        } else {
            return Long.compare(s1.getId(), s2.getId());
        }
    });
    protected long maxID = 0;

    @Override
    public void addTask(Task task) {
        long taskID = generateID();
        tasks.put(taskID, new Task(task, taskID));

        if (getStartTime(task) != null) {
            prioritizedTasks.add(task);
        }
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

        if (getStartTime(subtask) != null) {
            prioritizedTasks.add(subtask);
        }
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
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        historyDB.remove(new ArrayList<>(epics.values()));
        historyDB.remove(new ArrayList<>(subtasks.values()));
        prioritizedTasks.removeAll(epics.values());
        prioritizedTasks.removeAll(subtasks.values());
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        historyDB.remove(new ArrayList<>(subtasks.values()));
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            epic.setStatus(Status.NEW);
        });
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
            prioritizedTasks.remove(tasks.get(id));
            historyDB.remove(id);
            tasks.remove(id);
            return;
        }
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);

            epic.getSubtasks().forEach(subtask -> {
                historyDB.remove(subtask);
                subtasks.remove(subtask);
                prioritizedTasks.remove(subtasks.get(subtask));
            });

            historyDB.remove(id);
            prioritizedTasks.remove(epics.get(id));
            epics.remove(id);
            return;
        }
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getIdEpic()).getSubtasks().remove(id);
            checkStatus(epics.get(subtasks.get(id).getIdEpic()));
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyDB.remove(id);
            return;
        }
        System.out.println("Объект не найден");
    }

    @Override
    public List<Subtask> getSubtasksByID(long id) {
        if (!epics.containsKey(id)) {
            System.out.println("Объект не найден");
            return null;
        }
        ArrayList<Long> subtasksIDs = epics.get(id).getSubtasks();
        List<Subtask> subtasks = subtasksIDs.stream()
                .map(this::getSubtaskAnID).collect(Collectors.toList());

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

    @Override
    public Duration getDuration(Task task) {
        if (task instanceof Epic epic) {
            return calculateEpicDuration(epic);
        }
        return task.getDuration();
    }

    private Duration calculateEpicDuration(Epic epic) {
        return getSubtasks().stream()
                .filter(s -> s.getIdEpic() == epic.getId())
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getStartTime(Task task) {
        if (task instanceof Epic epic) {
            return calculateEpicStartTime(epic);
        }
        return task.getStartTime();
    }

    private LocalDateTime calculateEpicStartTime(Epic epic) {
        return calculateEpicTime(epic, Comparator.comparing(Subtask::getStartTime));
    }

    @Override
    public LocalDateTime getEndTime(Task task) {
        if (task instanceof Epic epic) {
            return calculateEpicEndTime(epic);
        }
        return task.getEndTime();
    }

    private LocalDateTime calculateEpicEndTime(Epic epic) {
        return calculateEpicTime(epic, Comparator.comparing(Subtask::getStartTime).reversed());
    }

    private LocalDateTime calculateEpicTime(Epic epic, Comparator<Subtask> comparator) {
        return getSubtasks().stream()
                .filter(s -> s.getIdEpic() == epic.getId())
                .min(comparator)
                .get()
                .getStartTime();
    }

    //я не понял как через стрим апи это сделать
    @Override
    public boolean hasTimeConflict() {
        Task prevTask = null;
        for (Task task : prioritizedTasks) {
            if (prevTask == null) {
                prevTask = task;
                continue;
            }
            if (getEndTime(prevTask).isAfter(getStartTime(task)) || getStartTime(prevTask).equals(getStartTime(task))) {
                return true;
            }
            prevTask = task;
        }
        return false;
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


    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}
