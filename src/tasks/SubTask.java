package tasks;


public class SubTask extends Task {
    protected int epicId;

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epicId = epic.getId();
        this.type = Type.SUBTASK;
    }

    public SubTask(String name, String description, int id, Status status, Type type, int epicId) {
        super(name, description);
        this.id = id;
        this.status = status;
        this.type = type;
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "\nSubTask{" +
                "\nepicId=" + epicId +
                ", \nname='" + name + '\'' +
                ", \ndescription='" + description + '\'' +
                ", \nid=" + id +
                ", \nstatus=" + status +
                ", \ntype=" + type +
                '}';
    }
}
