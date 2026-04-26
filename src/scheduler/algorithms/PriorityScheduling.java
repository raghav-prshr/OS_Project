package scheduler.algorithms;

import scheduler.model.GanttEntry;
import scheduler.model.Process;
import scheduler.model.ScheduleResult;

import java.util.*;

public class PriorityScheduling extends Scheduler {

    @Override
    public ScheduleResult schedule(List<Process> input) {
        List<Process> processes = new ArrayList<>();
        for (Process p : input) processes.add(p.copy());

        List<GanttEntry> gantt = new ArrayList<>();
        List<Process> done = new ArrayList<>();
        List<Process> remaining = new ArrayList<>(processes);
        int currentTime = 0;

        while (!remaining.isEmpty()) {
            List<Process> available = new ArrayList<>();
            for (Process p : remaining) {
                if (p.getArrivalTime() <= currentTime) available.add(p);
            }

            if (available.isEmpty()) {
                int next = remaining.stream().mapToInt(Process::getArrivalTime).min().orElse(currentTime);
                gantt.add(new GanttEntry("Idle", currentTime, next));
                currentTime = next;
                continue;
            }

            // Lower priority number = higher priority; break ties by arrival time
            available.sort(Comparator.comparingInt(Process::getPriority)
                    .thenComparingInt(Process::getArrivalTime));
            Process p = available.get(0);
            remaining.remove(p);

            int start = currentTime;
            currentTime += p.getBurstTime();
            p.setCompletionTime(currentTime);
            gantt.add(new GanttEntry(p.getPid(), start, currentTime));
            done.add(p);
        }

        computeMetrics(done);
        return new ScheduleResult(done, gantt, avgWT(done), avgTAT(done), cpuUtil(done, currentTime));
    }
}
