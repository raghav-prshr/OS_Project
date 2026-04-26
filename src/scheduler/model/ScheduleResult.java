package scheduler.model;

import java.util.List;

public class ScheduleResult {
    private List<Process> processes;
    private List<GanttEntry> gantt;
    private double avgWaitingTime;
    private double avgTurnaroundTime;
    private double cpuUtilization;

    public ScheduleResult(List<Process> processes, List<GanttEntry> gantt,
                          double avgWT, double avgTAT, double cpuUtil) {
        this.processes = processes;
        this.gantt = gantt;
        this.avgWaitingTime = avgWT;
        this.avgTurnaroundTime = avgTAT;
        this.cpuUtilization = cpuUtil;
    }

    public List<Process> getProcesses() { return processes; }
    public List<GanttEntry> getGantt() { return gantt; }
    public double getAvgWaitingTime() { return avgWaitingTime; }
    public double getAvgTurnaroundTime() { return avgTurnaroundTime; }
    public double getCpuUtilization() { return cpuUtilization; }
}
