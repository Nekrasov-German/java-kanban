import java.util.HashMap;

public interface InManager {
    HashMap<Integer,Task> getTasks();
    void deleteTasks();
    Task getTask(int id);
    void createTask(Task task);
    void updateTask(Task task);
    void deleteTaskForId(int id);

    HashMap<Integer,Epic> getEpics();
    void deleteEpics();
    Epic getEpic(int id);
    void createEpic(Epic epic);
    void updateEpic(Epic epic);
    void deleteEpicForId(int id);
    HashMap<Integer,SubTask> getSubTasksForEpic(Epic epic);

    HashMap<Integer,SubTask> getSubTasks();
    void deleteSubTasks();
    SubTask getSubTask(int id);
    void createSubTask(SubTask subTask);
    void updateSubTask(SubTask subTask);
    void deleteSubTaskForId(int id);
}
