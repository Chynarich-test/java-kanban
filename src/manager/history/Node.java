package manager.history;

import tasks.Task;

public class Node {
    public Task task;
    public Long next;
    public Long prev;

    public Node(Task task, Long next, Long prev) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}
