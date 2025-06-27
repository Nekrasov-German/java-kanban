package managers;

import tasks.Node;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> nodes = new HashMap<>();

    private Node<Task> first;
    private Node<Task> last;

    @Override
    public void add(Task task) {
        if (task != null) {
            linkedLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(nodes.get(id));
        nodes.remove(id);
    }

    private void linkedLast(Task task) {
        if (nodes.containsKey(task.getId())) {
            remove(task.getId());
        }
        final Node<Task> oldLast = last;
        final Node<Task> newNode = new Node<>(task, last, null);
        last = newNode;
        nodes.put(task.getId(), newNode);
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.setNext(newNode);
        }
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> next = node.getNext();
            final Node<Task> prev = node.getPrev();
            node.setData(null);

            if (first == node && last == node) {
                first = null;
                last = null;
            } else if (first == node) {
                first = next;
                first.setPrev(null);
            } else if (last == node) {
                last = prev;
                last.setNext(null);
            } else {
                prev.setNext(next);
                next.setPrev(prev);
            }
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = first;
        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }
}
