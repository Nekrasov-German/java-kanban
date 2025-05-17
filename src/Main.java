import managers.Manager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Manager manager = new Manager();

        Task task = new Task("Задача 1", "Описание первой задачи");
        Task task1 = new Task("Задача 2","Описание задачи 2");
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epic.getId());
        SubTask subTask1 = new SubTask("Подзадача 2","Описание подзадачи 2", epic.getId());
        Epic epic1 = new Epic("Эпик 2","Описание Эпика 2");
        SubTask subTask2 = new SubTask("Подзадача 3","Описание подзадачи 3", epic1.getId());

        manager.createTask(task);
        manager.createTask(task1);
        manager.createEpic(epic);
        manager.createSubTask(subTask);
        manager.createSubTask(subTask1);
        subTask.setStatus(Status.IN_PROGRESS);
        manager.createEpic(epic1);
        manager.createSubTask(subTask2);

        System.out.println("Вывод всех Task:\n");
        System.out.println(manager.getTasks());
        System.out.println("\n"+"-".repeat(15) + "\n");
        System.out.println("Вывод всех Epic:\n");
        System.out.println(manager.getEpics());
        System.out.println("\n"+"-".repeat(15) + "\n");
        System.out.println("Вывод SubTask определенного Epic:\n");
        System.out.println(manager.getSubTasksForEpic(epic));
        System.out.println("\n"+"-".repeat(15) + "\n");

        System.out.println("Удаление SubTask по определенному ID.");
        manager.deleteSubTaskForId(subTask1.getId());

        System.out.println("Вывод удаленного SubTask:\n");
        System.out.println(manager.getSubTask(subTask1.getId()));
        System.out.println("\n"+"-".repeat(15) + "\n");
        System.out.println("Вывод всех Epic для проверки Status после удаления SubTask:\n");
        System.out.println(manager.getEpics());
    }
}
