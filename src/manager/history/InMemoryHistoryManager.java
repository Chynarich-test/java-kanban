package manager.history;


import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Long, Node> historyDB = new HashMap<>();
    private Node first = null;
    private Node last = null;

    private Node linkLast(Task task) {
        Node newNode = new Node(new Task(task), null, last);
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
        return newNode;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            first = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            last = node.prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        if (historyDB.isEmpty()) return List.of();
        List<Task> history = new ArrayList<>();
        Node currentNode = first;
        while (currentNode != null) {
            history.add(currentNode.task);
            currentNode = currentNode.next;
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
        historyDB.put(taskId, linkLast(task));
    }


    @Override
    public void remove(long id) {
        if (!historyDB.containsKey(id)) return;
        Node oldNode = historyDB.get(id);
        removeNode(oldNode);
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
