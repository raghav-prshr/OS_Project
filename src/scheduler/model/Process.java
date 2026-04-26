package scheduler.model;

public class Process {
    private String pid;
    private int arrivalTime;
    private int burstTime;
    private int priority;

    // Computed fields
    private int waitingTime;
    private int turnaroundTime;
    private int completionTime;
    private int remainingTime;

    public Process(String pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }

    public Process copy() {
        return new Process(pid, arrivalTime, burstTime, priority);
    }

    // Getters & Setters
    public String getPid() { return pid; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    public int getWaitingTime() { return waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public int getCompletionTime() { return completionTime; }
    public int getRemainingTime() { return remainingTime; }

    public void setWaitingTime(int wt) { this.waitingTime = wt; }
    public void setTurnaroundTime(int tat) { this.turnaroundTime = tat; }
    public void setCompletionTime(int ct) { this.completionTime = ct; }
    public void setRemainingTime(int rt) { this.remainingTime = rt; }

    @Override
    public String toString() {
        return pid;
    }
}
