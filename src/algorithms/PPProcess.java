package algorithms;

public class PPProcess {
	int pid;
    int arrivalTime;
    int burstTime;
    int priority;
    int completedTime;
    int turnAroundTime;
    int waitingTime;
    int remainingBurstTime;
    boolean isComplete = false;

    public PPProcess(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingBurstTime = burstTime;
    }
}
