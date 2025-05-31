package managers;

import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int MAX_SIZE = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() == MAX_SIZE) history.removeFirst();
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        if (!history.isEmpty()) {
            return history;
        }
        return List.of();
    }
}
