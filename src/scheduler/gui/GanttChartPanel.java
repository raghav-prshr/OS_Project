package scheduler.gui;

import scheduler.model.GanttEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GanttChartPanel extends JPanel {

    private List<GanttEntry> ganttData;
    private static final int BAR_HEIGHT = 46;
    private static final int MARGIN = 20;
    private static final int LABEL_Y_OFFSET = 30;
    private static final int TIME_Y_OFFSET = 65;
    private static final int MIN_BLOCK_WIDTH = 48;

    // Color palette for processes
    private static final Color[] PROCESS_COLORS = {
        new Color(99, 179, 237),   // blue
        new Color(104, 211, 145),  // green
        new Color(246, 173, 85),   // orange
        new Color(252, 129, 129),  // red
        new Color(183, 148, 246),  // purple
        new Color(129, 230, 217),  // teal
        new Color(250, 240, 137),  // yellow
        new Color(251, 182, 206),  // pink
    };
    private static final Color IDLE_COLOR = new Color(55, 65, 81);

    public GanttChartPanel() {
        setBackground(new Color(17, 24, 39));
        setPreferredSize(new Dimension(800, 100));
    }

    public void setGanttData(List<GanttEntry> data) {
        this.ganttData = data;
        if (data != null && !data.isEmpty()) {
            int totalTime = data.get(data.size() - 1).getEnd();
            int w = Math.max(800, totalTime * MIN_BLOCK_WIDTH + MARGIN * 2);
            setPreferredSize(new Dimension(w, 100));
        }
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ganttData == null || ganttData.isEmpty()) {
            g.setColor(new Color(107, 114, 128));
            g.setFont(new Font("Consolas", Font.ITALIC, 13));
            g.drawString("Run a scheduler to see the Gantt chart", MARGIN, 50);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int totalTime = ganttData.get(ganttData.size() - 1).getEnd();
        int availW = getWidth() - MARGIN * 2;
        float scale = (float) availW / totalTime;
        scale = Math.max(scale, (float) MIN_BLOCK_WIDTH);

        java.util.Map<String, Color> colorMap = new java.util.LinkedHashMap<>();
        int[] colorIdx = {0};

        for (GanttEntry entry : ganttData) {
            int x = MARGIN + (int)(entry.getStart() * scale);
            int w = Math.max(1, (int)((entry.getEnd() - entry.getStart()) * scale));

            Color col;
            if (entry.getPid().equals("Idle")) {
                col = IDLE_COLOR;
            } else {
                boolean isNew = !colorMap.containsKey(entry.getPid());
                col = colorMap.computeIfAbsent(entry.getPid(), k -> PROCESS_COLORS[colorIdx[0] % PROCESS_COLORS.length]);
                if (isNew) colorIdx[0]++;
            }

            // Draw block with rounded corners
            g2.setColor(col);
            g2.fillRoundRect(x + 1, MARGIN, w - 2, BAR_HEIGHT, 8, 8);

            // Border
            g2.setColor(col.darker());
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x + 1, MARGIN, w - 2, BAR_HEIGHT, 8, 8);

            // Label inside block
            g2.setColor(new Color(17, 24, 39));
            g2.setFont(new Font("Consolas", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String label = entry.getPid();
            int lw = fm.stringWidth(label);
            if (lw + 4 < w) {
                g2.drawString(label, x + (w - lw) / 2, MARGIN + BAR_HEIGHT / 2 + fm.getAscent() / 2 - 1);
            }

            // Time labels below
            g2.setColor(new Color(156, 163, 175));
            g2.setFont(new Font("Consolas", Font.PLAIN, 10));
            String startStr = String.valueOf(entry.getStart());
            g2.drawString(startStr, x, TIME_Y_OFFSET);
        }

        // Draw last time marker
        GanttEntry last = ganttData.get(ganttData.size() - 1);
        int xEnd = MARGIN + (int)(last.getEnd() * scale);
        g2.setColor(new Color(156, 163, 175));
        g2.setFont(new Font("Consolas", Font.PLAIN, 10));
        g2.drawString(String.valueOf(last.getEnd()), xEnd, TIME_Y_OFFSET);
    }
}