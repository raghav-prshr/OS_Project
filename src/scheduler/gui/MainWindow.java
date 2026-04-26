package scheduler.gui;

import scheduler.algorithms.*;
import scheduler.model.GanttEntry;
import scheduler.model.Process;
import scheduler.model.ScheduleResult;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    // Colors
    private static final Color BG_DARK    = new Color(10, 14, 23);
    private static final Color BG_PANEL   = new Color(17, 24, 39);
    private static final Color BG_CARD    = new Color(26, 35, 50);
    private static final Color ACCENT     = new Color(99, 179, 237);
    private static final Color ACCENT2    = new Color(104, 211, 145);
    private static final Color TEXT_MAIN  = new Color(229, 231, 235);
    private static final Color TEXT_MUTED = new Color(107, 114, 128);
    private static final Color BORDER_COL = new Color(44, 55, 74);
    private static final Color ROW_ALT    = new Color(22, 30, 43);

    private DefaultTableModel processTableModel;
    private JTable processTable;
    private JComboBox<String> algorithmBox;
    private JSpinner quantumSpinner;
    private JLabel quantumLabel;
    private GanttChartPanel ganttPanel;
    private DefaultTableModel resultTableModel;
    private JLabel avgWTLabel, avgTATLabel, cpuUtilLabel;

    public MainWindow() {
        setTitle("CPU Scheduling Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 780);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
        setBackground(BG_DARK);

        initUI();
        addSampleData();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        center.setDividerLocation(390);
        center.setDividerSize(4);
        center.setBorder(null);
        center.setBackground(BG_DARK);
        root.add(center, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COL));
        header.setPreferredSize(new Dimension(0, 58));

        JLabel title = new JLabel("  ⚙  CPU Scheduling Simulator");
        title.setFont(new Font("Consolas", Font.BOLD, 18));
        title.setForeground(ACCENT);
        header.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("FCFS · SJF · Round Robin · Priority   ");
        sub.setFont(new Font("Consolas", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        header.add(sub, BorderLayout.EAST);
        return header;
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 16, 16, 8));

        panel.add(sectionLabel("PROCESSES"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildProcessTableCard());
        panel.add(buildProcessButtons());
        panel.add(Box.createVerticalStrut(10));        panel.add(Box.createVerticalStrut(18));
        panel.add(sectionLabel("ALGORITHM"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildAlgorithmCard());
        panel.add(Box.createVerticalStrut(18));
        panel.add(buildRunButton());

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 8, 16, 16));

        panel.add(sectionLabel("GANTT CHART"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildGanttCard());
        panel.add(Box.createVerticalStrut(16));
        panel.add(sectionLabel("RESULTS"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildResultsCard());
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildMetricsCard());

        return panel;
    }

    private JPanel buildProcessTableCard() {
        String[] cols = {"PID", "Arrival", "Burst", "Priority"};
        processTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return true; }
        };
        processTable = styledTable(processTableModel);

        JScrollPane scroll = new JScrollPane(processTable);
        styleScrollPane(scroll);
        scroll.setPreferredSize(null);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel card = card();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildProcessButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JButton addBtn = accentButton("+ Add Process", ACCENT);
        JButton removeBtn = accentButton("− Remove", new Color(252, 129, 129));
        JButton clearBtn = accentButton("Clear All", TEXT_MUTED);

        addBtn.addActionListener(e -> addProcess());
        removeBtn.addActionListener(e -> removeProcess());
        clearBtn.addActionListener(e -> processTableModel.setRowCount(0));

        p.add(addBtn);
        p.add(removeBtn);
        p.add(clearBtn);
        return p;
    }

    private JPanel buildAlgorithmCard() {
        JPanel card = card();
        card.setLayout(new GridBagLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setForeground(TEXT_MUTED);
        algoLabel.setFont(new Font("Consolas", Font.PLAIN, 12));

        algorithmBox = new JComboBox<>(new String[]{
            "First Come First Served (FCFS)",
            "Shortest Job First (SJF)",
            "Round Robin (RR)",
            "Priority Scheduling"
        });
        styleComboBox(algorithmBox);
        algorithmBox.setPreferredSize(new Dimension(220, 28));
        algorithmBox.addActionListener(e -> {
            boolean isRR = algorithmBox.getSelectedIndex() == 2;
            quantumLabel.setVisible(isRR);
            quantumSpinner.setVisible(isRR);
        });

        quantumLabel = new JLabel("Quantum:");
        quantumLabel.setForeground(TEXT_MUTED);
        quantumLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        quantumLabel.setVisible(false);

        quantumSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 100, 1));
        styleSpinner(quantumSpinner);
        quantumSpinner.setPreferredSize(new Dimension(70, 28));
        quantumSpinner.setVisible(false);

        gbc.gridx = 0; gbc.gridy = 0; card.add(algoLabel, gbc);
        gbc.gridx = 1; card.add(algorithmBox, gbc);
        gbc.gridx = 2; card.add(quantumLabel, gbc);
        gbc.gridx = 3; card.add(quantumSpinner, gbc);

        return card;
    }

    private JPanel buildRunButton() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton run = new JButton("▶  Run Simulation");
        run.setFont(new Font("Consolas", Font.BOLD, 14));
        run.setForeground(new Color(10, 14, 23));
        run.setBackground(ACCENT);
        run.setBorder(new EmptyBorder(8, 24, 8, 24));
        run.setFocusPainted(false);
        run.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        run.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { run.setBackground(ACCENT.brighter()); }
            public void mouseExited(MouseEvent e) { run.setBackground(ACCENT); }
        });
        run.addActionListener(e -> runSimulation());
        p.add(run);
        return p;
    }

    private JPanel buildGanttCard() {
        ganttPanel = new GanttChartPanel();
        JScrollPane scroll = new JScrollPane(ganttPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        styleScrollPane(scroll);
        scroll.setPreferredSize(new Dimension(0, 100));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel card = card();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildResultsCard() {
        String[] cols = {"PID", "Arrival", "Burst", "Priority", "Completion", "Turnaround", "Waiting"};
        resultTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable resultTable = styledTable(resultTableModel);

        JScrollPane scroll = new JScrollPane(resultTable);
        styleScrollPane(scroll);
        scroll.setPreferredSize(new Dimension(0, 200));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel card = card();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildMetricsCard() {
        JPanel card = card();
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        avgWTLabel  = metricLabel("Avg Waiting Time", "—");
        avgTATLabel = metricLabel("Avg Turnaround", "—");
        cpuUtilLabel = metricLabel("CPU Utilization", "—");

        card.add(avgWTLabel);
        card.add(sep());
        card.add(avgTATLabel);
        card.add(sep());
        card.add(cpuUtilLabel);
        return card;
    }

    // ─── Simulation Logic ───────────────────────────────────────────────────

    private void runSimulation() {
        List<Process> processes = readProcesses();
        if (processes == null) return;

        int algo = algorithmBox.getSelectedIndex();
        Scheduler scheduler;
        switch (algo) {
            case 0: scheduler = new FCFS(); break;
            case 1: scheduler = new SJF(); break;
            case 2: scheduler = new RoundRobin((int) quantumSpinner.getValue()); break;
            case 3: scheduler = new PriorityScheduling(); break;
            default: return;
        }

        ScheduleResult result = scheduler.schedule(processes);

        // Update Gantt chart
        ganttPanel.setGanttData(result.getGantt());

        // Update results table
        resultTableModel.setRowCount(0);
        for (Process p : result.getProcesses()) {
            resultTableModel.addRow(new Object[]{
                p.getPid(), p.getArrivalTime(), p.getBurstTime(), p.getPriority(),
                p.getCompletionTime(), p.getTurnaroundTime(), p.getWaitingTime()
            });
        }

        // Update metrics
        avgWTLabel.setText("<html><span style='color:#6b7280;font-size:10px'>Avg Waiting Time</span><br>" +
                "<b style='color:#63b3ed;font-size:14px'>" + String.format("%.2f", result.getAvgWaitingTime()) + "</b></html>");
        avgTATLabel.setText("<html><span style='color:#6b7280;font-size:10px'>Avg Turnaround</span><br>" +
                "<b style='color:#68d391;font-size:14px'>" + String.format("%.2f", result.getAvgTurnaroundTime()) + "</b></html>");
        cpuUtilLabel.setText("<html><span style='color:#6b7280;font-size:10px'>CPU Utilization</span><br>" +
                "<b style='color:#f6ad55;font-size:14px'>" + String.format("%.1f%%", result.getCpuUtilization()) + "</b></html>");
    }

    private List<Process> readProcesses() {
        List<Process> list = new ArrayList<>();
        int rows = processTableModel.getRowCount();
        if (rows == 0) {
            JOptionPane.showMessageDialog(this, "Please add at least one process.", "No Processes", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            for (int i = 0; i < rows; i++) {
                String pid = processTableModel.getValueAt(i, 0).toString().trim();
                int arrival  = Integer.parseInt(processTableModel.getValueAt(i, 1).toString().trim());
                int burst    = Integer.parseInt(processTableModel.getValueAt(i, 2).toString().trim());
                int priority = Integer.parseInt(processTableModel.getValueAt(i, 3).toString().trim());
                if (burst <= 0) throw new NumberFormatException("Burst must be > 0");
                list.add(new Process(pid, arrival, burst, priority));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return list;
    }

    private void addProcess() {
        int row = processTableModel.getRowCount() + 1;
        processTableModel.addRow(new Object[]{"P" + row, 0, 5, 1});
    }

    private void removeProcess() {
        int sel = processTable.getSelectedRow();
        if (sel >= 0) processTableModel.removeRow(sel);
        else if (processTableModel.getRowCount() > 0)
            processTableModel.removeRow(processTableModel.getRowCount() - 1);
    }

    private void addSampleData() {
        processTableModel.addRow(new Object[]{"P1", 0, 8, 2});
        processTableModel.addRow(new Object[]{"P2", 1, 4, 1});
        processTableModel.addRow(new Object[]{"P3", 2, 9, 3});
        processTableModel.addRow(new Object[]{"P4", 3, 5, 2});
        processTableModel.addRow(new Object[]{"P5", 4, 2, 4});
    }

    // ─── Style Helpers ───────────────────────────────────────────────────────

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Consolas", Font.BOLD, 10));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1, true),
            new EmptyBorder(6, 6, 6, 6)
        ));
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? BG_CARD : ROW_ALT);
                else c.setBackground(new Color(55, 80, 110));
                c.setForeground(TEXT_MAIN);
                ((JComponent) c).setBorder(new EmptyBorder(3, 8, 3, 8));
                return c;
            }
        };
        t.setBackground(BG_CARD);
        t.setForeground(TEXT_MAIN);
        t.setFont(new Font("Consolas", Font.PLAIN, 12));
        t.setRowHeight(26);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.getTableHeader().setBackground(new Color(30, 41, 59));
        t.getTableHeader().setForeground(ACCENT);
        t.getTableHeader().setFont(new Font("Consolas", Font.BOLD, 11));
        t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COL));
        t.setSelectionBackground(new Color(55, 80, 110));
        t.setSelectionForeground(Color.WHITE);
        return t;
    }

    private void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_CARD);
        sp.getVerticalScrollBar().setBackground(BG_CARD);
        sp.getHorizontalScrollBar().setBackground(BG_CARD);
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(BG_CARD);
        cb.setForeground(TEXT_MAIN);
        cb.setFont(new Font("Consolas", Font.PLAIN, 12));
        cb.setBorder(new LineBorder(BORDER_COL));
    }

    private void styleSpinner(JSpinner sp) {
    sp.setBackground(Color.WHITE);
    sp.setForeground(Color.BLACK);

    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) sp.getEditor();
    JTextField tf = editor.getTextField();

    tf.setBackground(Color.WHITE);
    tf.setForeground(Color.BLACK);
    tf.setCaretColor(Color.BLACK);
    tf.setFont(new Font("Consolas", Font.BOLD, 12));
}

    private JButton accentButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Consolas", Font.BOLD, 11));
        b.setForeground(color);
        b.setBackground(BG_CARD);
        b.setBorder(new CompoundBorder(new LineBorder(color.darker(), 1, true), new EmptyBorder(4, 10, 4, 10)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(30, 41, 59)); }
            public void mouseExited(MouseEvent e) { b.setBackground(BG_CARD); }
        });
        return b;
    }

    private JLabel metricLabel(String title, String value) {
        JLabel l = new JLabel("<html><span style='color:#6b7280;font-size:10px'>" + title + "</span><br>" +
                "<b style='color:#63b3ed;font-size:14px'>" + value + "</b></html>");
        l.setFont(new Font("Consolas", Font.PLAIN, 12));
        return l;
    }

    private JSeparator sep() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(1, 36));
        s.setForeground(BORDER_COL);
        return s;
    }

    public static void main(String[] args) {
        // Try to set Nimbus look and feel, else fallback
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
