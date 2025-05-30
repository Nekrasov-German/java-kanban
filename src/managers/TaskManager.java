package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();
    void deleteTasks();
    Task getTask(int id);
    int createTask(Task task);
    int updateTask(Task task);
    void deleteTaskForId(int id);

    List<Epic> getEpics();
    void deleteEpics();
    Epic getEpic(int id);
    int createEpic(Epic epic);
    int updateEpic(Epic epic);
    void deleteEpicForId(int id);
    List<SubTask> getSubTasksForEpic(Epic epic);

    List<SubTask> getSubTasks();
    void deleteSubTasks();
    SubTask getSubTask(int id);
    int createSubTask(SubTask subTask);
    int updateSubTask(SubTask subTask);
    void deleteSubTaskForId(int id);

    List<Task> getHistory();
}
