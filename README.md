# CPU Scheduling Simulator
A Java Swing desktop application simulating 4 CPU scheduling algorithms with a Gantt chart and metrics.

## Algorithms Included
- **FCFS** – First Come First Served
- **SJF** – Shortest Job First (Non-Preemptive)
- **Round Robin** – Configurable time quantum
- **Priority Scheduling** – Non-Preemptive (lower number = higher priority)

## Project Structure
```
CPUScheduler/
├── src/
│   └── scheduler/
│       ├── model/
│       │   ├── Process.java        – Process data model
│       │   ├── GanttEntry.java     – Gantt chart entry
│       │   └── ScheduleResult.java – Simulation output
│       ├── algorithms/
│       │   ├── Scheduler.java      – Abstract base class
│       │   ├── FCFS.java
│       │   ├── SJF.java
│       │   ├── RoundRobin.java
│       │   └── PriorityScheduling.java
│       └── gui/
│           ├── MainWindow.java     – Main GUI (entry point)
│           └── GanttChartPanel.java– Custom Gantt chart renderer
└── README.md
```

## Requirements
- Java JDK 8 or higher

## How to Compile & Run

### Option 1 – Command Line
```bash
# Create output directory
mkdir -p out

# Compile all files
javac -d out -sourcepath src $(find src -name "*.java")

# Run the application
java -cp out scheduler.gui.MainWindow
```

### Option 2 – IntelliJ IDEA
1. Open IntelliJ → **File → Open** → select the `CPUScheduler` folder
2. Mark `src` as **Sources Root** (right-click → Mark Directory as → Sources Root)
3. Run `scheduler.gui.MainWindow`

### Option 3 – Eclipse
1. **File → New → Java Project** → uncheck "Use default location" → select `CPUScheduler` folder
2. Right-click project → **Build Path → Use as Source Folder** on `src`
3. Run `MainWindow.java`

## How to Use
1. Add processes using the **+ Add Process** button (edit PID, Arrival, Burst, Priority inline)
2. Select an algorithm from the dropdown
3. For **Round Robin**, set the time quantum
4. Click **▶ Run Simulation**
5. View the **Gantt Chart** and **Results Table**
6. Check **Avg Waiting Time**, **Avg Turnaround**, and **CPU Utilization** metrics

## Notes
- Priority: lower number = higher priority (e.g., 1 > 2 > 3)
- SJF and Priority are non-preemptive
- Idle slots appear in the Gantt chart when no process is available
