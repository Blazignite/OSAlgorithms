package algorithms;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class RoundRobin implements OperatingSystemAlgorithm {

    @Override
    public String getInstructions() {
        return "<html><b>Round Robin Scheduling</b><br><br>"
                + "Each process is given a fixed time slice (Time Quantum).<br>"
                + "<b>Do you want to continue?</b></html>";
    }

    @Override
    public void run() {

        try {
            // Inputs
            String intString = JOptionPane.showInputDialog(null, "Enter Number of Processes: ");
            if (intString == null) return;
            int num = Integer.parseInt(intString.trim());

            ArrayList<RRProcess> processes = new ArrayList<>();
            ArrayList<RRGanttChart> ganttChart = new ArrayList<>();

            for (int i = 0; i < num; i++) {
                String pid = "P" + (i + 1);
                String atString = JOptionPane.showInputDialog(null, "Arrival Time of " + pid + ":");
                String btString = JOptionPane.showInputDialog(null, "Burst Time of " + pid + ":");
                if (atString == null || btString == null) return;

                int at = Integer.parseInt(atString.trim());
                int bt = Integer.parseInt(btString.trim());

                processes.add(new RRProcess(pid, at, bt));
            }

            String tqString = JOptionPane.showInputDialog(null, "Enter Time Quantum: ");
            if (tqString == null) return;
            int tq = Integer.parseInt(tqString.trim());

            // Logic
            processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
            Queue<RRProcess> queue = new LinkedList<>();

            int currentTime = 0;
            int completed = 0;
            int index = 0;

            if (!processes.isEmpty() && processes.get(0).arrivalTime > currentTime) {
                currentTime = processes.get(0).arrivalTime;
            }

            while (completed < num) {
                while (index < num && processes.get(index).arrivalTime <= currentTime) {
                    queue.add(processes.get(index));
                    index++;
                }

                if (queue.isEmpty()) {
                    if (index < num) {
                        int nextTime = processes.get(index).arrivalTime;
                        ganttChart.add(new RRGanttChart("IDLE", currentTime, nextTime));
                        currentTime = nextTime;
                        queue.add(processes.get(index));
                        index++;
                    } else {
                        break;
                    }
                }

                RRProcess p = queue.poll();
                int start = currentTime;

                if (p.remainingBurstTime > tq) {
                    p.remainingBurstTime -= tq;
                    currentTime += tq;
                } else {
                    currentTime += p.remainingBurstTime;
                    p.remainingBurstTime = 0;
                    p.completedTime = currentTime;
                    p.turnAroundTime = (p.completedTime - p.arrivalTime);
                    p.waitingTime = (p.turnAroundTime - p.burstTime);
                    completed++;
                }

                int end = currentTime;
                ganttChart.add(new RRGanttChart(p.pid, start, end));

                while (index < num && processes.get(index).arrivalTime <= currentTime) {
                    queue.add(processes.get(index));
                    index++;
                }

                if (p.remainingBurstTime > 0) {
                    queue.add(p);
                }
            }

            processes.sort(Comparator.comparing(p -> Integer.parseInt(p.pid.substring(1))));

            // Show results
            showDashboardWindow(processes, ganttChart, tq);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showDashboardWindow(ArrayList<RRProcess> processes, ArrayList<RRGanttChart> ganttChart, int tq) {

        JFrame frame = new JFrame("Round Robin Results");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Table
        String[] col = {"Process ID", "Arrival", "Burst", "Completion", "Turn Around", "Waiting"};
        Object[][] data = new Object[processes.size()][6];

        double totalTAT = 0;
        double totalWT = 0;

        for (int i = 0; i < processes.size(); i++) {
            RRProcess p = processes.get(i);
            data[i][0] = p.pid;
            data[i][1] = p.arrivalTime;
            data[i][2] = p.burstTime;
            data[i][3] = p.completedTime;
            data[i][4] = p.turnAroundTime;
            data[i][5] = p.waitingTime;
            totalTAT += p.turnAroundTime;
            totalWT += p.waitingTime;
        }

        JTable table = new JTable(new DefaultTableModel(data, col));
        table.setRowHeight(25);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < 6; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Process Table (TQ = " + tq + ")"));

        // Gantt Chart Panel
        JPanel ganttPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (ganttChart.isEmpty()) return;

                int totalTime = ganttChart.get(ganttChart.size() - 1).endTime;
                int width = getWidth() - 40;
                int x = 20;
                int y = 40;
                int h = 50;

                for (RRGanttChart entry : ganttChart) {
                    int dur = entry.endTime - entry.startTime;
                    int barW = (int) ((double) dur / totalTime * width);
                    int barX = x + (int) ((double) entry.startTime / totalTime * width);

                    g.setColor(entry.pid.equals("IDLE") ? Color.LIGHT_GRAY : Color.CYAN);
                    g.fillRect(barX, y, barW, h);
                    g.setColor(Color.BLACK);
                    g.drawRect(barX, y, barW, h);

                    if (barW > 20)
                        g.drawString(entry.pid, barX + barW / 2 - 5, y + 30);

                    g.drawString(String.valueOf(entry.startTime), barX, y + h + 15);
                }

                g.drawString(String.valueOf(totalTime), x + width, y + h + 15);
            }
        };
        ganttPanel.setPreferredSize(new Dimension(800, 150));
        ganttPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));

        // Footer
        JPanel footer = new JPanel(new FlowLayout());
        JLabel avg = new JLabel(String.format(
                "Avg Turnaround: %.2f   |   Avg Waiting: %.2f",
                totalTAT / processes.size(),
                totalWT / processes.size()
        ));
        avg.setFont(new Font("SansSerif", Font.BOLD, 14));
        footer.add(avg);

        mainPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(ganttPanel, BorderLayout.SOUTH);
        mainPanel.add(footer, BorderLayout.NORTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
