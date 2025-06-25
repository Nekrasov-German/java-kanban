import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание первой задачи");
        Task task2 = new Task("Задача 2","Описание второй задачи");
        Epic epicWithSubTasks = new Epic("Эпик с тремя подзадачами", "Содержит три подзадачи.");
        SubTask subTask1 = new SubTask("Подзадача 1","Описание подзадачи 1", epicWithSubTasks);
        SubTask subTask2 = new SubTask("Подзадача 2","Описание подзадачи 2", epicWithSubTasks);
        SubTask subTask3 = new SubTask("Подзадача 3","Описание подзадачи 3", epicWithSubTasks);
        Epic epicWithoutSubTask = new Epic("Эпик без подзадач","Описание Эпика без подзадачю");


        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epicWithoutSubTask);
        manager.createEpic(epicWithSubTasks);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getEpic(epicWithoutSubTask.getId());
        manager.getEpic(epicWithSubTasks.getId());
        manager.getSubTask(subTask1.getId());
        manager.getSubTask(subTask2.getId());
        manager.getSubTask(subTask3.getId());

        System.out.println("История просмотров :");
        for (Task taskHistory : manager.getHistory()) {
            System.out.println(taskHistory);
        }
        System.out.println("\n" + "-".repeat(15) + "\n");
        System.out.print("Размер истории просмотров : ");
        System.out.println(manager.getHistory().size());
        System.out.println("\n" + "-".repeat(15) + "\n");

        manager.getTask(task2.getId());
        manager.getSubTask(subTask2.getId());
        manager.getEpic(epicWithSubTasks.getId());

        System.out.println("История просмотров после повторных добавлений :");
        for (Task taskHistory : manager.getHistory()) {
            System.out.println(taskHistory);
        }
        System.out.println("\n" + "-".repeat(15) + "\n");
        System.out.print("Размер истории просмотров после повторных добавлений : ");
        System.out.println(manager.getHistory().size());
        System.out.println("\n" + "-".repeat(15) + "\n");

        manager.deleteTaskForId(task2.getId());

        System.out.println("История просмотров после удаления Task :");
        for (Task taskHistory : manager.getHistory()) {
            System.out.println(taskHistory);
        }
        System.out.println("\n" + "-".repeat(15) + "\n");
        System.out.print("Размер истории просмотров после удаления Task : ");
        System.out.println(manager.getHistory().size());
        System.out.println("\n" + "-".repeat(15) + "\n");

        manager.deleteEpicForId(epicWithSubTasks.getId());

        System.out.println("История просмотров после удаления Epic с подзадачами :");
        for (Task taskHistory : manager.getHistory()) {
            System.out.println(taskHistory);
        }
        System.out.println("\n" + "-".repeat(15) + "\n");
        System.out.print("Размер истории просмотров после удаления Epic с подзадачами : ");
        System.out.println(manager.getHistory().size());
        System.out.println("\n" + "-".repeat(15) + "\n");
    }
}
