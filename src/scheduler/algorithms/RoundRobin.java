package scheduler.algorithms;

import scheduler.model.GanttEntry;
import scheduler.model.Process;
import scheduler.model.ScheduleResult;

import java.util.*;

public class RoundRobin extends Scheduler {
    private int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public ScheduleResult schedule(List<Process> input) {
        List<Process> processes = new ArrayList<>();
        for (Process p : input) processes.add(p.copy());
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<GanttEntry> gantt = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();
        List<Process> done = new ArrayList<>();
        List<Process> notArrived = new ArrayList<>(processes);

        int currentTime = 0;

        // Add processes that arrive at time 0
        Iterator<Process> it = notArrived.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.getArrivalTime() <= currentTime) {
                queue.add(p);
                it.remove();
            }
        }

        while (!queue.isEmpty() || !notArrived.isEmpty()) {
            if (queue.isEmpty()) {
                int next = notArrived.stream().mapToInt(Process::getArrivalTime).min().orElse(currentTime);
                gantt.add(new GanttEntry("Idle", currentTime, next));
                currentTime = next;
                Iterator<Process> it2 = notArrived.iterator();
                while (it2.hasNext()) {
                    Process p = it2.next();
                    if (p.getArrivalTime() <= currentTime) { queue.add(p); it2.remove(); }
                }
                continue;
            }

            Process p = queue.poll();
            int execTime = Math.min(quantum, p.getRemainingTime());
            int start = currentTime;
            currentTime += execTime;
            p.setRemainingTime(p.getRemainingTime() - execTime);
            gantt.add(new GanttEntry(p.getPid(), start, currentTime));

            // Enqueue newly arrived processes
            Iterator<Process> it3 = notArrived.iterator();
            while (it3.hasNext()) {
                Process np = it3.next();
                if (np.getArrivalTime() <= currentTime) { queue.add(np); it3.remove(); }
            }

            if (p.getRemainingTime() == 0) {
                p.setCompletionTime(currentTime);
                done.add(p);
            } else {
                queue.add(p);
            }
        }

        computeMetrics(done);
        // Sort done list by original process order
        done.sort(Comparator.comparing(Process::getPid));
        return new ScheduleResult(done, gantt, avgWT(done), avgTAT(done), cpuUtil(done, currentTime));
    }
}
