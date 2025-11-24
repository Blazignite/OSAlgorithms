package algorithms;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

// Teodoro
public class RoundRobin implements OperatingSystemAlgorithm {

    //Panel to swap for later
    private JPanel resultsContainer; 

    @Override
    public String getInstructions() {
        return "<html><b>Round Robin Scheduling</b><br><br>"
             + "This is a preemptive algorithm. Each process is given<br>"
             + "a fixed time slice (Time Quantum) to run.<br><br>"
             + "<b>Do you want to continue?</b></html>";
    }

    @Override
    public void run(JTextArea outputArea) {
        //Setup GUI
        if (resultsContainer == null) {
            try {
                Container parent = outputArea.getParent(); // JViewport
                if (parent != null) {
                    Container scrollPane = parent.getParent(); // JScrollPane
                    if (scrollPane != null) {
                        Container panel = scrollPane.getParent(); // resultsPanel
                        if (panel instanceof JPanel) {
                            resultsContainer = (JPanel) panel;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not find GUI Container");
            }
        }

        //Default Text
        outputArea.setText("--- Running Round Robin ---\nInputting data...");

        try {
            //Inputs
            String intString = JOptionPane.showInputDialog(null, "Enter Number of Processes: ");
            if (intString == null) return;
            int num = Integer.parseInt(intString.trim());

            ArrayList<RRProcess> processes = new ArrayList<>();
            ArrayList<RRGanttChart> ganttChart = new ArrayList<>();

            for (int i = 0; i < num; i++){
                String pid = "P" + (i+1);
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

            //Logic
            processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
            Queue<RRProcess> queue = new LinkedList<>();

            int currentTime = 0;
            int completed = 0;
            int index = 0;

            if (!processes.isEmpty() && processes.get(0).arrivalTime > currentTime) {
                currentTime = processes.get(0).arrivalTime;
            }

            while (completed < num) {
                while (index < num && processes.get(index).arrivalTime <= currentTime){
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

                if (p.remainingBurstTime > tq){
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

                while (index < num && processes.get(index).arrivalTime <= currentTime){
                    queue.add(processes.get(index));
                    index++;
                }

                if (p.remainingBurstTime > 0){
                    queue.add(p);
                }
            }

            processes.sort(Comparator.comparing(p -> Integer.parseInt(p.pid.substring(1))));
            
            //Dashboard Panel
            JPanel dashboard = createDashboardPanel(processes, ganttChart, tq);
            
            if (resultsContainer != null) {
                BorderLayout layout = (BorderLayout) resultsContainer.getLayout();
                Component center = layout.getLayoutComponent(BorderLayout.CENTER);
                if (center != null) {
                    resultsContainer.remove(center);
                }
                
                resultsContainer.add(dashboard, BorderLayout.CENTER);
                
                resultsContainer.revalidate();
                resultsContainer.repaint();
            } else {
                outputArea.append("\n[Error] Could not swap to Graphical View.");
            }

        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //JPanel for the Results
    private JPanel createDashboardPanel(ArrayList<RRProcess> processes, ArrayList<RRGanttChart> ganttChart, int tq) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        //Table
        String[] columnNames = {"Process ID", "Arrival", "Burst", "Completion", "Turn Around", "Waiting"};
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

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int x=0;x<6;x++) table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Process Table (TQ=" + tq + ")"));

        //Gantt Chart
        JPanel ganttPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (ganttChart.isEmpty()) return;
                
                int totalTime = ganttChart.get(ganttChart.size()-1).endTime;
                int width = getWidth() - 40;
                int startX = 20;
                int startY = 40;
                int height = 50;

                for (RRGanttChart entry : ganttChart) {
                    int duration = entry.endTime - entry.startTime;
                    int barWidth = (int) ((double)duration / totalTime * width);
                    int barX = startX + (int) ((double)entry.startTime / totalTime * width);
                    
                    if(entry.pid.equals("IDLE")) g.setColor(Color.LIGHT_GRAY);
                    else g.setColor(Color.CYAN);
                    
                    g.fillRect(barX, startY, barWidth, height);
                    g.setColor(Color.BLACK);
                    g.drawRect(barX, startY, barWidth, height);

                    if (barWidth > 20) {
                        g.drawString(entry.pid, barX + (barWidth/2) - 5, startY + 30);
                    }
                    g.drawString(String.valueOf(entry.startTime), barX, startY + height + 15);
                }
                g.drawString(String.valueOf(totalTime), startX + width, startY + height + 15);
            }
        };
        ganttPanel.setPreferredSize(new Dimension(800, 150));
        ganttPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));

        // C. Averages
        JPanel footer = new JPanel(new FlowLayout());
        JLabel avgLabel = new JLabel(String.format("Avg Turnaround: %.2f  |  Avg Waiting: %.2f", 
            (totalTAT/processes.size()), (totalWT/processes.size())));
        avgLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        footer.add(avgLabel);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(ganttPanel, BorderLayout.SOUTH);
        mainPanel.add(footer, BorderLayout.NORTH);
        
        return mainPanel;
    }
}