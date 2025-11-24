package algorithms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

// Santos
public class PreemptivePriority implements OperatingSystemAlgorithm {

    
    // run method variables
    private int num;
    private JDialog inputDialog;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton submitButton;
    private JScrollPane scrollPaneInput;
    private JPanel btnPanel;

    // results window variables
    private JFrame resultFrame;
    private JTable resultTable;
    private DefaultTableModel resultTableModel;
    private JScrollPane tableScroll;
    private PPGanttChartPanel ganttPanel;
    private JPanel footerPanel;
    private JPanel labelsPanel;
    private JLabel labelAvgTAT;
    private JLabel labelAvgWT;
    private JButton btnBack;

    @Override
    public String getInstructions() {
        return "<html>"
                + "<b>Preemptive Priority Scheduling</b><br><br>"
                + "1. Enter the number of processes.<br>"
                + "2. Fill in the table (Arrival, Burst, Priority).<br>"
                + "3. The lower the number, the higher the priority (1 is the highest priority)<br>"
                + "4. Click 'Submit' to see the <b>Result</b>.<br><br>"
                + "<b>Do you want to continue?</b>"        
                + "</html>";
    }

    @Override
    public void run() {
        String numInput = JOptionPane.showInputDialog(null, "Enter number of processes: (e.g. 5)", "Preemptive Priority", JOptionPane.QUESTION_MESSAGE);
       
        if (numInput == null) return;

        try {
            num = Integer.parseInt(numInput);
            if (num <= 0) {
                new NumberFormatException(); 
                return;
            }
            if (num > 100) {
                JOptionPane.showMessageDialog(null, "Too many processes! Please enter a number less than or equal to 100.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid positive number. (e.g. 5)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // table
        String[] header = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
        Object[][] tableCells = new Object[num][4];
        
        for (int i = 1; i <= num; i++) {
            tableCells[i-1][0] = "P" + (i);
        }

        tableModel = new DefaultTableModel(tableCells, header) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 1 || col == 2 || col == 3; 
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSurrendersFocusOnKeystroke(true);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
      
        table.getColumnModel().getColumn(1).setCellEditor(createIntegerEditor(0));
        table.getColumnModel().getColumn(2).setCellEditor(createIntegerEditor(1));
        table.getColumnModel().getColumn(3).setCellEditor(createIntegerEditor(1));


        // input to table
        inputDialog = new JDialog();
        inputDialog.setTitle("Input positive integer numbers");
        inputDialog.setModal(true);
        inputDialog.setLayout(new BorderLayout());
        inputDialog.add(new JScrollPane(table), BorderLayout.CENTER);
        
        GradientPanel dialogPanel = new GradientPanel();
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        scrollPaneInput = new JScrollPane(table);
        dialogPanel.add(scrollPaneInput, BorderLayout.CENTER);

        submitButton = new JButton("Submit and Calculate");
        btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(submitButton);
        
        dialogPanel.add(btnPanel, BorderLayout.SOUTH);
        inputDialog.setContentPane(dialogPanel);
        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.isEditing()) table.getCellEditor().stopCellEditing();

                PPProcess[] processes = new PPProcess[num]; 
                try {
                    for (int i = 0; i < num; i++) {
                        Object atObj = tableModel.getValueAt(i, 1);
                        Object btObj = tableModel.getValueAt(i, 2);
                        Object prioObj = tableModel.getValueAt(i, 3);

                        if (atObj == null || btObj == null || prioObj == null) throw new NullPointerException();

                        int at = Integer.parseInt(atObj.toString());
                        int bt = Integer.parseInt(btObj.toString());
                        int pr = Integer.parseInt(prioObj.toString());

                        processes[i] = new PPProcess(i + 1, at, bt, pr);
                    }
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(inputDialog, "Please fill all fields with valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PPGanttChart[] ganttChart = runPPAlgo(processes);
                showResult(ganttChart, processes);         
                inputDialog.dispose();
            }
        });
        
        inputDialog.setSize(500, 400);
        inputDialog.setLocationRelativeTo(null);
        inputDialog.setVisible(true);
    }

    private PPGanttChart[] runPPAlgo(PPProcess[] processes) {
        PPGanttChart[] ganttChart = new PPGanttChart[1000]; 
        int ganttIndex = 0; 

        int total = processes.length;
        int currTime = 0;
        int completedProcess = 0;
        int currentPid = -1;
        int startBlock = 0;

        while (completedProcess < total) {
            PPProcess bestProcess = null;
            for (PPProcess p : processes) {
                if (p.arrivalTime <= currTime && !p.isComplete) {
                    if (bestProcess == null) bestProcess = p;
                    else if (p.priority < bestProcess.priority) 
                        bestProcess = p;
                    else if (p.priority == bestProcess.priority && p.arrivalTime < bestProcess.arrivalTime) 
                        bestProcess = p;
                    else if (p.priority == bestProcess.priority && p.arrivalTime == bestProcess.arrivalTime && p.remainingBurstTime < bestProcess.remainingBurstTime) 
                        bestProcess = p;
                    else if (p.priority == bestProcess.priority && p.arrivalTime == bestProcess.arrivalTime && p.remainingBurstTime == bestProcess.remainingBurstTime && p.pid < bestProcess.pid) 
                        bestProcess = p;
                }
            }

            int nextPid;
            if (bestProcess != null) nextPid = bestProcess.pid;
            else nextPid = -1;
            
            //if natapos or napalitan yung process, iadd ung current process sa gantt
            if (nextPid != currentPid) {
                if (currTime > 0) {
                    ganttChart[ganttIndex] = new PPGanttChart(currentPid, startBlock, currTime);
                    ganttIndex++;
                }
                currentPid = nextPid;
                startBlock = currTime;
            }

            if (bestProcess == null) {
                currTime++;
            } 
            else {
                bestProcess.remainingBurstTime--;
                currTime++;
                if (bestProcess.remainingBurstTime == 0) {
                    bestProcess.isComplete = true;
                    completedProcess++;
                    bestProcess.completedTime = currTime;
                    bestProcess.turnAroundTime = bestProcess.completedTime - bestProcess.arrivalTime;
                    bestProcess.waitingTime = bestProcess.turnAroundTime - bestProcess.burstTime;
                }
            }
        }
        //last process
        ganttChart[ganttIndex] = new PPGanttChart(currentPid, startBlock, currTime);
        return ganttChart;
    }

    private void showResult(PPGanttChart[] ganttChart, PPProcess[] processes) {
        resultFrame = new JFrame("Calculation Results & Gantt Chart");
        resultFrame.setSize(900, 650); 
        resultFrame.setLocationRelativeTo(null);
        resultFrame.setAlwaysOnTop(true);
        resultFrame.setLayout(new BorderLayout());

        String[] header = {"Process", "AT", "BT", "Priority", "CT", "Turnaround", "Waiting"};
        Object[][] tableCells = new Object[processes.length][7];
        
        double totalTAT = 0;
        double totalWT = 0;

        for (int i = 0; i < processes.length; i++) {
            PPProcess p = processes[i];
            tableCells[i][0] = "P" + p.pid;
            tableCells[i][1] = p.arrivalTime;
            tableCells[i][2] = p.burstTime;
            tableCells[i][3] = p.priority;
            tableCells[i][4] = p.completedTime;
            tableCells[i][5] = p.turnAroundTime;
            tableCells[i][6] = p.waitingTime;
            
            totalTAT += p.turnAroundTime;
            totalWT += p.waitingTime;
        }

        resultTableModel = new DefaultTableModel(tableCells, header) {
            @Override
            public boolean isCellEditable(int row, int col) { 
                return false; 
            } 
        };
        
        resultTable = new JTable(resultTableModel);
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(800, 200));
        tableScroll.setBorder(BorderFactory.createTitledBorder("Final Process Table"));

        ganttPanel = new PPGanttChartPanel(ganttChart);
        ganttPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        
        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS)); 
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        footerPanel.setBackground(new Color(240, 240, 240));

        labelsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        labelsPanel.setOpaque(false);
        
        labelAvgTAT = new JLabel(String.format("Average Turnaround Time: %.2f ms", totalTAT / processes.length));
        labelAvgTAT.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelAvgTAT.setForeground(new Color(0, 102, 204)); 
        
        labelAvgWT = new JLabel(String.format("Average Waiting Time: %.2f ms", totalWT / processes.length));
        labelAvgWT.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelAvgWT.setForeground(new Color(204, 51, 0)); 

        labelsPanel.add(labelAvgTAT);
        labelsPanel.add(labelAvgWT);

        btnBack = new JButton("Back to Home");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(e -> resultFrame.dispose()); 

        footerPanel.add(labelsPanel);
        footerPanel.add(Box.createVerticalStrut(10));
        footerPanel.add(btnBack);

        resultFrame.add(tableScroll, BorderLayout.NORTH);
        resultFrame.add(ganttPanel, BorderLayout.CENTER);
        resultFrame.add(footerPanel, BorderLayout.SOUTH);

        resultFrame.setVisible(true);
    }
    private DefaultCellEditor createIntegerEditor(int minimumValue) {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(minimumValue);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);
        ((NumberFormat) formatter.getFormat()).setGroupingUsed(false);

        JFormattedTextField ftf = new JFormattedTextField(formatter);

        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

        ftf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    int end = ftf.getText().length();
                    ftf.setSelectionStart(end);
                    ftf.setSelectionEnd(end);
                });
            }
        });

        return new DefaultCellEditor(ftf) {
            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int col) {
                ftf.setValue(null);
                ftf.setText("");

                if (value != null) {
                    String s = value.toString().trim();
                    if (!s.isEmpty()) {
                        ftf.setValue(Integer.parseInt(s));
                    } else {
                        ftf.setValue(null);
                        ftf.setText("");
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    int end = ftf.getText().length();
                    ftf.setSelectionStart(end);
                    ftf.setSelectionEnd(end);
                });

                return super.getTableCellEditorComponent(tbl, value, isSelected, row, col);
            }

            @Override
            public boolean stopCellEditing() {
            	String text = ftf.getText().trim();
                if (text.isEmpty()) {
                    ftf.setValue(null);
                    return super.stopCellEditing();
                }

                try {
                    ftf.commitEdit();
                } catch (Exception e) {
                    return false;
                }
                return super.stopCellEditing();
            }
        };
    }

}