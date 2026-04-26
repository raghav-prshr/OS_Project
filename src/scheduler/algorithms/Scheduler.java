package scheduler.algorithms;

import scheduler.model.Process;
import scheduler.model.ScheduleResult;
import java.util.List;

public abstract class Scheduler {
    public abstract ScheduleResult schedule(List<Process> processes);

    protected void computeMetrics(List<Process> processes) {
        for (Process p : processes) {
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }
    }

    protected double avgWT(List<Process> processes) {
        return processes.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
    }

    protected double avgTAT(List<Process> processes) {
        return processes.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);
    }

    protected double cpuUtil(List<Process> processes, int totalTime) {
        int busyTime = processes.stream().mapToInt(Process::getBurstTime).sum();
        return totalTime == 0 ? 0 : (busyTime * 100.0 / totalTime);
    }
}
