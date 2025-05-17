package tasks;

import managers.Manager;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Type type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = Manager.getNextId();
        this.status = Status.NEW;
        this.type = Type.TASK;
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
