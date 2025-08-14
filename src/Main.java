import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();
        LocalDateTime time = LocalDateTime.of(2025,7,20,10,0,0);
        Duration duration = Duration.ofMinutes(40);

        LocalDateTime timeSubtask2 = LocalDateTime.of(2025,7,20,19,10,0);
        Duration durationSubtask2 = Duration.ofMinutes(60);

        LocalDateTime timeSubtask1 = LocalDateTime.of(2025,7,20,11,0,0);
        Duration durationSubtask1 = Duration.ofMinutes(110);

        LocalDateTime timeSubtask3 = LocalDateTime.of(2025,7,20,9,0,0);
        Duration durationSubtask3 = Duration.ofMinutes(10);

        Task task1 = new Task("Задача 1", "Описание первой задачи",duration,time);
        Task task2 = new Task("Задача 2","Описание второй задачи");
        Epic epicWithSubTasks = new Epic("Эпик с тремя подзадачами", "Содержит три подзадачи.");
        SubTask subTask1 = new SubTask("Подзадача 1","Описание подзадачи 1", durationSubtask1,
                timeSubtask1, epicWithSubTasks.getId());
        SubTask subTask2 = new SubTask("Подзадача 2","Описание подзадачи 2", durationSubtask2,
                timeSubtask2, epicWithSubTasks.getId());
        SubTask subTask3 = new SubTask("Подзадача 3","Описание подзадачи 3", durationSubtask3,
                timeSubtask3, epicWithSubTasks.getId());
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

        System.out.println("Tasks :");
        manager.getTasks().forEach(System.out::println);
        System.out.println();
        System.out.println("Epics :");
        manager.getEpics().forEach(System.out::println);
        System.out.println();
        System.out.println("SubTasks :");
        manager.getSubTasks().forEach(System.out::println);
        System.out.println("\n" + "-".repeat(15) + "\n");
        System.out.println("Запрос задач по приоритету : ");
        manager.getPrioritizedTasks().forEach(System.out::println);
    }
}
