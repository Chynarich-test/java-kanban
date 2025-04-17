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
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime)
                    .thenComparingLong(Task::getId)
    );
    protected long maxID = 0;

    @Override
    public void addTask(Task task) {
        if (task.getStartTime() != null && hasConflictWithExisting(task)) return;

        long taskID = generateID();
        tasks.put(taskID, new Task(task, taskID));

        if (task.getStartTime() != null) {
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
        if (subtask.getStartTime() != null && hasConflictWithExisting(subtask)) return;

        Epic epic = epics.get(subtask.getIdEpic());
        long newSubtaskID = generateID();
        subtasks.put(newSubtaskID, new Subtask(subtask, newSubtaskID, subtask.getIdEpic()));
        epic.addSubTask(newSubtaskID);
        recalculateEpicFields(epic);

        if (subtask.getStartTime() != null) {
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
            recalculateEpicFields(epics.get(subtasks.get(id).getIdEpic()));
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

        return subtasksIDs.stream()
                .map(subtasks::get).collect(Collectors.toList());
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
        recalculateEpicFields(updatableEpic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask updatableSubtask = subtasks.get(subtask.getId());
        long newEpicId = subtask.getIdEpic();
        long oldEpicId = updatableSubtask.getIdEpic();
        if (newEpicId != oldEpicId) {
            addSubtaskToEpic(subtask);
            deleteTaskAnID(subtask.getId());
            recalculateEpicFields(epics.get(newEpicId));
            recalculateEpicFields(epics.get(oldEpicId));
            return;
        }
        updatableSubtask.setName(subtask.getName());
        updatableSubtask.setDescription(subtask.getDescription());
        updatableSubtask.setStatus(subtask.getStatus());
        recalculateEpicFields(epics.get(newEpicId));
    }

    @Override
    public List<Task> getHistory() {
        return historyDB.getHistory();
    }

    private void recalculateEpicFields(Epic epic) {
        fillingEpicTimeFields(epic);
        checkStatus(epic);
    }


    private void fillingEpicTimeFields(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByID(epic.getId()).stream()
                .filter(s -> s.getDuration() != null)
                .filter(s -> s.getStartTime() != null)
                .toList();

        if (epicSubtasks.isEmpty()) return;

        epic.setDuration(epicSubtasks.stream()
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus));
        Comparator<Subtask> comparator = Comparator.comparing(Subtask::getStartTime);

        epic.setStartTime(epicSubtasks.stream()
                .min(comparator)
                .get()
                .getStartTime());
        epic.setEndTime(epicSubtasks.stream()
                .max(comparator)
                .get()
                .getStartTime());
    }

    private boolean tasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getDuration() == null || task1.getDuration().isZero() ||
                task2.getStartTime() == null || task2.getDuration() == null || task2.getDuration().isZero()) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean hasConflictWithExisting(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> tasksOverlap(newTask, existingTask));
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
