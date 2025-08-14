package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    protected int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public SubTask(String name, String description, int id, Status status, Type type, int epicId) {
        super(name, description);
        this.id = id;
        this.status = status;
        this.type = type;
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int id, Status status, Type type, Duration duration,
                   LocalDateTime startTime, int epicId) {
        super(name, description);
        this.id = id;
        this.status = status;
        this.type = type;
        this.epicId = epicId;
        this.duration = duration;
        this.startTime = startTime;
    }

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, duration, startTime);
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
                ", \nduration=" + getDurationFormat(duration) +
                ", \nstartTime=" + getTimeFormat(startTime) +
                ", \nendTime=" + getTimeFormat(getEndTime()) +
                '}';
    }
}
