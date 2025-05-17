package tasks;

public class SubTask extends Task {
    protected int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;

        this.type = Type.SUBTASK;
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
