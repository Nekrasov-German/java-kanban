package tasks;

import managers.InMemoryTaskManager;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Type type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getNextId();
        this.status = Status.NEW;
        this.type = Type.TASK;
    }

    public Task(String name, String description, int id, Status status, Type type) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "\nTask{" +
                "\nname='" + name + '\'' +
                ", \ndescription='" + description + '\'' +
                ", \nid=" + id +
                ", \nstatus=" + status +
                ", \ntype=" + type +
                '}';
    }
}
