package algorithms;

public class RRGanttChart {
    String pid;
    int startTime;
    int endTime;

    public RRGanttChart (String pid, int startTime, int endTime) {
        this.pid = pid;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}