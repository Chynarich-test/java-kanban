package manager.history;


import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Long, Node> historyDB = new HashMap<>();
    private Long first = null;
    private Long last = null;

    @Override
    public List<Task> getHistory() {
        if (historyDB.isEmpty()) return List.of();
        List<Task> history = new ArrayList<>();
        Long currentId = first;
        while (currentId != null) {
            Node currentNode = historyDB.get(currentId);
            history.add(currentNode.task);
            currentId = currentNode.next;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        long taskId = task.getId();
        if (historyDB.containsKey(taskId)) {
            remove(taskId);
        }
        if (historyDB.isEmpty()) {
            first = taskId;
            last = taskId;
            historyDB.put(taskId, new Node(new Task(task), null, null));
        } else {
            Long oldLast = last;
            historyDB.get(oldLast).next = taskId;
            last = taskId;
            historyDB.put(taskId, new Node(new Task(task), null, oldLast));
        }
    }


    @Override
    public void remove(long id) {
        if (!historyDB.containsKey(id)) return;
        Node oldNode = historyDB.get(id);

        if (first != null && first.equals(id)) {
            first = oldNode.next;
        }
        if (last != null && last.equals(id)) {
            last = oldNode.prev;
        }
        if (oldNode.prev != null && historyDB.containsKey(oldNode.prev)) {
            Node prevNode = historyDB.get(oldNode.prev);
            prevNode.next = oldNode.next;
        }
        if (oldNode.next != null && historyDB.containsKey(oldNode.next)) {
            Node nextNode = historyDB.get(oldNode.next);
            nextNode.prev = oldNode.prev;
        }

        historyDB.remove(id);

        if (historyDB.isEmpty()) {
            first = null;
            last = null;
        }
    }

    @Override
    public void remove(List<Task> tasks) {
        for (Task task : tasks) {
            remove(task.getId());
        }
    }
}
