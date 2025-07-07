package tasks;

import managers.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<Integer> subTasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.type = Type.EPIC;
    }

    public Epic(String name, String description, int id, Status status, Type type) {
        super(name, description);
        this.id = id;
        this.status = status;
        this.type = type;
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void setSubTasksId(SubTask subTask) {
        subTasksId.add(subTask.getId());
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        return "\nEpic{" +
                "\nsubTasksId=" + subTasksId +
                ", \nname='" + name + '\'' +
                ", \ndescription='" + description + '\'' +
                ", \nid=" + id +
                ", \nstatus=" + status +
                ", \ntype=" + type +
                '}';
    }
}
