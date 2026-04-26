package scheduler.algorithms;

import scheduler.model.GanttEntry;
import scheduler.model.Process;
import scheduler.model.ScheduleResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FCFS extends Scheduler {

    @Override
    public ScheduleResult schedule(List<Process> input) {
        List<Process> processes = new ArrayList<>();
        for (Process p : input) processes.add(p.copy());
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<GanttEntry> gantt = new ArrayList<>();
        int currentTime = 0;

        for (Process p : processes) {
            if (currentTime < p.getArrivalTime()) {
                gantt.add(new GanttEntry("Idle", currentTime, p.getArrivalTime()));
                currentTime = p.getArrivalTime();
            }
            int start = currentTime;
            currentTime += p.getBurstTime();
            p.setCompletionTime(currentTime);
            gantt.add(new GanttEntry(p.getPid(), start, currentTime));
        }

        computeMetrics(processes);
        return new ScheduleResult(processes, gantt, avgWT(processes), avgTAT(processes), cpuUtil(processes, currentTime));
    }
}
