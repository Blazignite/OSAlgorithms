package algorithms;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // <--- ADDED THIS IMPORT
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// Santos
public class PreemptivePriority implements OperatingSystemAlgorithm {

    // --- INNER CLASS: PROCESS ---
    private class Process {
        int pid;
        int arrivalTime;
        int burstTime;
        int priority;
        int completedTime;
        int turnAroundTime;
        int waitingTime;
        int remainingBurstTime;
        boolean isComplete = false;

        public Process(int pid, int arrivalTime, int burstTime, int priority) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
            this.remainingBurstTime = burstTime;
        }
    }

    // --- INNER CLASS: GANTT RECORD ---
    private class GanttRecord {
        int pid;
        int startTime;
        int endTime;

        public GanttRecord(int pid, int startTime, int endTime) {
            this.pid = pid;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    @Override
    public String getInstructions() {
        return "<html>"
                + "<b>Preemptive Priority Scheduling</b><br><br>"
                + "1. Enter the number of processes.<br>"
                + "2. Fill in the table (Arrival, Burst, Priority).<br>"
                + "3. Click 'Submit' to see the <b>Visual Dashboard</b>.<br><br>"
                + "<b>Do you want to continue?</b>"
                + "</html>";
    }

    @Override
    public void run(JTextArea outputArea) {
        // --- 1. GET NUMBER OF PROCESSES ---
        String numStr = JOptionPane.showInputDialog(null, "Enter number of processes:", "Preemptive Priority", JOptionPane.QUESTION_MESSAGE);
        int n;

        if (numStr == null) {
            return;
        }

        try {
            n = Integer.parseInt(numStr);
            if (n <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid positive number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- 2. SETUP INPUT TABLE ---
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
        Object[][] data = new Object[n][4];
        for (int i = 0; i < n; i++) data[i][0] = "P" + (i + 1);

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 1 || col == 2 || col == 3; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        
        NumberFormatter intFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        intFormatter.setValueClass(Integer.class);
        intFormatter.setMinimum(0);
        intFormatter.setAllowsInvalid(false);
        ((NumberFormat) intFormatter.getFormat()).setGroupingUsed(false);

        table.getColumnModel().getColumn(1).setCellEditor(new IntegerInputCellEditor(intFormatter));
        table.getColumnModel().getColumn(2).setCellEditor(new IntegerInputCellEditor(intFormatter));
        table.getColumnModel().getColumn(3).setCellEditor(new IntegerInputCellEditor(intFormatter));

        // --- 3. SHOW INPUT DIALOG ---
        JDialog dialog = new JDialog();
        dialog.setTitle("Input Data");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton submitButton = new JButton("Submit & Calculate");
        JPanel btnPanel = new JPanel();
        btnPanel.add(submitButton);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.isEditing()) table.getCellEditor().stopCellEditing();

                Process[] processes = new Process[n];
                try {
                    for (int i = 0; i < n; i++) {
                        Object atObj = model.getValueAt(i, 1);
                        Object btObj = model.getValueAt(i, 2);
                        Object prObj = model.getValueAt(i, 3);

                        if (atObj == null || btObj == null || prObj == null) throw new NullPointerException();

                        int at = Integer.parseInt(atObj.toString());
                        int bt = Integer.parseInt(btObj.toString());
                        int pr = Integer.parseInt(prObj.toString());

                        processes[i] = new Process(i + 1, at, bt, pr);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields with valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // A. RUN ALGORITHM
                List<GanttRecord> history = runAlgorithmLogic(processes);

                // B. SORT PROCESSES BY PID FOR DISPLAY
                Arrays.sort(processes, Comparator.comparingInt(p -> p.pid));

                // C. SHOW THE NEW DASHBOARD WINDOW
                showDashboardWindow(history, processes);
                
                dialog.dispose();
            }
        });

        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // --- LOGIC ---
    private List<GanttRecord> runAlgorithmLogic(Process[] processes) {
        List<GanttRecord> history = new ArrayList<>();
        int n = processes.length;
        int currentTime = 0;
        int completed = 0;
        int currentPid = -1;
        int startBlock = 0;

        while (completed < n) {
            Process best = null;
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !p.isComplete) {
                    if (best == null) best = p;
                    else if (p.priority < best.priority) best = p;
                    else if (p.priority == best.priority && p.arrivalTime < best.arrivalTime) best = p;
                }
            }

            int nextPid = (best != null) ? best.pid : -1;

            if (nextPid != currentPid) {
                if (currentTime > 0) {
                    history.add(new GanttRecord(currentPid, startBlock, currentTime));
                }
                currentPid = nextPid;
                startBlock = currentTime;
            }

            if (best == null) {
                currentTime++;
            } else {
                best.remainingBurstTime--;
                currentTime++;
                if (best.remainingBurstTime == 0) {
                    best.isComplete = true;
                    completed++;
                    best.completedTime = currentTime;
                    best.turnAroundTime = best.completedTime - best.arrivalTime;
                    best.waitingTime = best.turnAroundTime - best.burstTime;
                }
            }
        }
        history.add(new GanttRecord(currentPid, startBlock, currentTime));
        return history;
    }

    // =======================================================================
    // === DASHBOARD WINDOW WITH BACK BUTTON ===
    // =======================================================================
    private void showDashboardWindow(List<GanttRecord> history, Process[] processes) {
        JFrame frame = new JFrame("Calculation Results & Gantt Chart");
        frame.setSize(900, 650); // Made slightly taller for the button
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setLayout(new BorderLayout());

        // --- 1. TOP: RESULT TABLE ---
        String[] columnNames = {"Process", "AT", "BT", "Priority", "CT", "Turnaround", "Waiting"};
        Object[][] data = new Object[processes.length][7];
        
        double totalTAT = 0;
        double totalWT = 0;

        for (int i = 0; i < processes.length; i++) {
            Process p = processes[i];
            data[i][0] = "P" + p.pid;
            data[i][1] = p.arrivalTime;
            data[i][2] = p.burstTime;
            data[i][3] = p.priority;
            data[i][4] = p.completedTime;
            data[i][5] = p.turnAroundTime;
            data[i][6] = p.waitingTime;
            
            totalTAT += p.turnAroundTime;
            totalWT += p.waitingTime;
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; } 
        };
        
        JTable resultTable = new JTable(tableModel);
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(800, 200));
        tableScroll.setBorder(BorderFactory.createTitledBorder("Final Process Table"));

        // --- 2. CENTER: GANTT CHART ---
        GanttChartPanel ganttPanel = new GanttChartPanel(history);
        ganttPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart Visualization"));
        
        // --- 3. BOTTOM: AVERAGES & BACK BUTTON ---
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS)); // Stack vertically
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        footerPanel.setBackground(new Color(240, 240, 240));

        // Panel for the labels
        JPanel labelsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        labelsPanel.setOpaque(false);
        
        JLabel lblAvgTAT = new JLabel(String.format("Average Turnaround Time: %.2f ms", totalTAT / processes.length));
        lblAvgTAT.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblAvgTAT.setForeground(new Color(0, 102, 204)); 
        
        JLabel lblAvgWT = new JLabel(String.format("Average Waiting Time: %.2f ms", totalWT / processes.length));
        lblAvgWT.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblAvgWT.setForeground(new Color(204, 51, 0)); 

        labelsPanel.add(lblAvgTAT);
        labelsPanel.add(lblAvgWT);

        // The Back Button
        JButton btnBack = new JButton("Back to Home");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(e -> frame.dispose()); // Closes this window

        // Add to footer
        footerPanel.add(labelsPanel);
        footerPanel.add(Box.createVerticalStrut(10));
        footerPanel.add(btnBack);

        // Add to Frame
        frame.add(tableScroll, BorderLayout.NORTH);
        frame.add(ganttPanel, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // --- GANTT CHART PANEL ---
    class GanttChartPanel extends JPanel {
        List<GanttRecord> history;
        int totalTime;

        public GanttChartPanel(List<GanttRecord> history) {
            this.history = history;
            this.totalTime = history.isEmpty() ? 1 : history.get(history.size() - 1).endTime;
            this.setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 60;
            int h = 50;
            int x = 30;
            int y = getHeight() / 2 - 25; 

            for (GanttRecord r : history) {
                int duration = r.endTime - r.startTime;
                if (duration <= 0) continue;

                int boxX = x + (int) ((double) r.startTime / totalTime * w);
                int boxW = (int) ((double) duration / totalTime * w);
                
                if (r.pid == -1) g2.setColor(Color.LIGHT_GRAY);
                else g2.setColor(Color.getHSBColor((r.pid * 0.618f) % 1, 0.6f, 0.9f)); 
                
                g2.fillRect(boxX, y, boxW, h);
                g2.setColor(Color.BLACK);
                g2.drawRect(boxX, y, boxW, h);
                
                String lbl = (r.pid == -1) ? "Idle" : "P" + r.pid;
                if (boxW > 20) {
                    g2.setColor(Color.BLACK);
                    g2.drawString(lbl, boxX + boxW / 2 - 5, y + 30);
                }
                g2.drawString(String.valueOf(r.startTime), boxX, y + h + 15);
            }
            g2.drawString(String.valueOf(totalTime), x + w, y + h + 15);
        }
    }

    class IntegerInputCellEditor extends DefaultCellEditor {
        JFormattedTextField ftf;
        public IntegerInputCellEditor(NumberFormatter formatter) {
            super(new JFormattedTextField(formatter));
            ftf = (JFormattedTextField) getComponent();
            ftf.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { SwingUtilities.invokeLater(() -> ftf.selectAll()); }
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            JFormattedTextField f = (JFormattedTextField) super.getTableCellEditorComponent(t, v, s, r, c);
            f.setValue(v);
            return f;
        }
        public boolean stopCellEditing() {
            try { ftf.commitEdit(); } catch (Exception e) { return false; }
            return super.stopCellEditing();
        }
    }
}