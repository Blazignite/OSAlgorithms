package algorithms;

public class RRProcess {
    String pid;
    int arrivalTime;
    int burstTime;
    int remainingBurstTime;
    int completedTime;
    int turnAroundTime;
    int waitingTime;

    public RRProcess(String pid, int arrivalTime, int burstTime){
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
    }
}