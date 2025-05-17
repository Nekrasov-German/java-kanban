package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.util.List;

public interface InManager {
    List<Task> getTasks();
    void deleteTasks();
    Task getTask(int id);
    void createTask(Task task);
    void updateTask(Task task);
    void deleteTaskForId(int id);

    List<Epic> getEpics();
    void deleteEpics();
    Epic getEpic(int id);
    void createEpic(Epic epic);
    void updateEpic(Epic epic);
    void deleteEpicForId(int id);
    List<SubTask> getSubTasksForEpic(Epic epic);

    List<SubTask> getSubTasks();
    void deleteSubTasks();
    SubTask getSubTask(int id);
    void createSubTask(SubTask subTask);
    void updateSubTask(SubTask subTask);
    void deleteSubTaskForId(int id);
}
