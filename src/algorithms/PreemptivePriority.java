package algorithms;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

// Import for the formatters
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;

// Imports for the bug-fix editor
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.table.TableCellEditor;

//Santos
public class PreemptivePriority implements OperatingSystemAlgorithm {

    // (Process inner class is unchanged)
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

    // (getInstructions method is unchanged)
    @Override
    public String getInstructions() {
        return "<html>"
             + "<b>PreemptivePriority Scheduling</b><br><br>"
             + "This algorithm schedules processes based on priority.<br>"
             + "The highest priority process runs first (lower number = higher priority).<br>"
             + "If a new process arrives with a higher priority,<br>"
             + "the current process is preempted.<br><br>"
             + "You will be asked to enter the number of processes,<br>"
             + "then fill in each process's Arrival Time, Burst Time, and Priority.<br><br>"
             + "<b>Do you want to continue?</b>"
             + "</html>";
    }

    // The run method is MODIFIED
    @Override
    public void run(JTextArea outputArea) {
        // --- 1. GET NUMBER OF PROCESSES ---
        String numStr = JOptionPane.showInputDialog(null, "Enter number of processes:", "Preemptive Priority", JOptionPane.QUESTION_MESSAGE);
        int n; 

        if (numStr == null) {
            outputArea.setText("Preemptive Priority run cancelled by user.");
            return;
        }

        try {
            n = Integer.parseInt(numStr);
            if (n <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- 2. CREATE THE TABLE MODEL ---
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority", "Completed Time", "Turn Around Time", "Waiting Time"};
        Object[][] data = new Object[n][7];
        for (int i = 0; i < n; i++) {
            data[i][0] = "P" + (i + 1); 
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 1 || col == 2 || col == 3;
            }
        };

        // --- 3. CREATE THE GUI DIALOG ---
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        
        // --- 3a. SET UP INPUT VALIDATION ---
        
        // Create a formatter for Arrival Time (>= 0)
        NumberFormatter atFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        atFormatter.setValueClass(Integer.class);
        atFormatter.setMinimum(0);
        atFormatter.setMaximum(Integer.MAX_VALUE);
        atFormatter.setAllowsInvalid(false); // No invalid chars
        atFormatter.setCommitsOnValidEdit(true); // Commits value on valid keypress
        ((NumberFormat)atFormatter.getFormat()).setGroupingUsed(false); // No commas!

        // Create a formatter for Burst Time & Priority (>= 1)
        NumberFormatter btPrioFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        btPrioFormatter.setValueClass(Integer.class);
        btPrioFormatter.setMinimum(1); // Must be at least 1
        btPrioFormatter.setMaximum(Integer.MAX_VALUE);
        btPrioFormatter.setAllowsInvalid(false);
        btPrioFormatter.setCommitsOnValidEdit(true);
        ((NumberFormat)btPrioFormatter.getFormat()).setGroupingUsed(false); // No commas!
        
        // Apply our NEW Bug-Fix Cell Editors
        table.getColumnModel().getColumn(1).setCellEditor(new IntegerInputCellEditor(atFormatter));
        table.getColumnModel().getColumn(2).setCellEditor(new IntegerInputCellEditor(btPrioFormatter));
        table.getColumnModel().getColumn(3).setCellEditor(new IntegerInputCellEditor(btPrioFormatter));
        // --- END OF VALIDATION SETUP ---

        JDialog dialog = new JDialog();
        dialog.setTitle("Enter Process Data");
        dialog.setModal(true); 
        dialog.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // --- 4. CREATE BUTTONS ---
        JButton submitButton = new JButton("Submit");
        JButton clearButton = new JButton("Clear");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // --- 5. ADD BUTTON LOGIC (ActionListeners) ---
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < n; i++) {
                    model.setValueAt(null, i, 1); 
                    model.setValueAt(null, i, 2); 
                    model.setValueAt(null, i, 3); 
                }
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This is now a "backup" in case an edit is
                // still in progress (e.g., user hits Submit while typing)
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                Process[] processes = new Process[n];
                try {
                    for (int i = 0; i < n; i++) {
                        Object atObj = model.getValueAt(i, 1);
                        Object btObj = model.getValueAt(i, 2);
                        Object prioObj = model.getValueAt(i, 3);
                        
                        if (atObj == null || btObj == null || prioObj == null) {
                            throw new NullPointerException("All fields must be filled.");
                        }
                        int at = ((Number) atObj).intValue();
                        int bt = ((Number) btObj).intValue();
                        int prio = ((Number) prioObj).intValue();
                        if (at < 0 || bt <= 0 || prio <= 0) { 
                            throw new NumberFormatException("Invalid data. AT must be >= 0, BT and Priority must be > 0.");
                        }
                        processes[i] = new Process(i + 1, at, bt, prio);
                    }
                } catch (Exception ex) {
                    String message = "Invalid input. Please fill all fields with valid positive integers.";
                    if (ex instanceof NullPointerException) {
                        message = "All fields must be filled.";
                    }
                    JOptionPane.showMessageDialog(dialog, message, "Input Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
                
                runAlgorithmLogic(processes);

                outputArea.setText("--- Running Preemptive Priority ---\n\n");
                outputArea.append(String.format("%-10s %-10s %-10s %-10s %-10s %-10s %-10s\n", 
                    "Process", "AT", "BT", "Priority", "CT", "TAT", "WT"));
                outputArea.append("----------------------------------------------------------------------\n");
                double totalTAT = 0, totalWT = 0;
                Arrays.sort(processes, Comparator.comparingInt(p -> p.pid)); 
                for (int i = 0; i < n; i++) {
                    Process p = processes[i];
                    model.setValueAt(p.completedTime, i, 4);
                    model.setValueAt(p.turnAroundTime, i, 5);
                    model.setValueAt(p.waitingTime, i, 6);
                    outputArea.append(String.format("%-10s %-10d %-10d %-10d %-10d %-10d %-10d\n", 
                        "P" + p.pid, p.arrivalTime, p.burstTime, p.priority,
                        p.completedTime, p.turnAroundTime, p.waitingTime));
                    totalTAT += p.turnAroundTime;
                    totalWT += p.waitingTime;
                }
                outputArea.append("\n----------------------------------------------------------------------\n");
                outputArea.append(String.format("Average Turn Around Time: %.2f\n", totalTAT / n));
                outputArea.append(String.format("Average Waiting Time: %.2f\n", totalWT / n));
                outputArea.append("--- Preemptive Priority Finished ---\n");

                dialog.dispose();
            }
        });

        // --- 6. SHOW THE DIALOG ---
        dialog.pack(); 
        dialog.setLocationRelativeTo(null); 
        dialog.setVisible(true); 
    }

    // (runAlgorithmLogic method is unchanged)
    private void runAlgorithmLogic(Process[] processes) {
        int n = processes.length;
        int currentTime = 0;
        int completedProcesses = 0;
        while (completedProcesses < n) {
            Process bestProcess = null; 
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !p.isComplete) {
                    if (bestProcess == null) {
                        bestProcess = p; 
                    } else if (p.priority < bestProcess.priority) {
                        bestProcess = p; 
                    }
                }
            }
            if (bestProcess == null) {
                currentTime++;
            } else {
                bestProcess.remainingBurstTime--;
                currentTime++;
                if (bestProcess.remainingBurstTime == 0) {
                    bestProcess.isComplete = true;
                    completedProcesses++;
                    bestProcess.completedTime = currentTime;
                    bestProcess.turnAroundTime = bestProcess.completedTime - bestProcess.arrivalTime;
                    bestProcess.waitingTime = bestProcess.turnAroundTime - bestProcess.burstTime;
                }
            }
        }
    }
    
    // =====================================================================
    // == NEW AND IMPROVED BUG-FIX INNER CLASS
    // =====================================================================
    /**
     * This custom cell editor fixes all Swing JTable bugs:
     * 1. Fixes "carry-over" bug (resets value)
     * 2. Fixes "lost value" bug (saves on focus loss)
     * 3. Fixes "select all" bug (selects text on click)
     */
class IntegerInputCellEditor extends DefaultCellEditor {
        
        JFormattedTextField ftf;

        public IntegerInputCellEditor(NumberFormatter formatter) {
            super(new JFormattedTextField(formatter));
            ftf = (JFormattedTextField) getComponent();
            
            // This is the "select-all" fix
            ftf.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    // Use invokeLater to wait for the UI to be ready
                    SwingUtilities.invokeLater(() -> ftf.selectAll());
                }
            });
        }

        // This method fixes the "carry-over" bug
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            
            JFormattedTextField field = (JFormattedTextField) super.getTableCellEditorComponent(
                                                                table, value, isSelected, row, column);
            
            // Reset the value *every time* the editor is shown.
            field.setValue(value);
            
            return field;
        }

        // This method MUST be here to return the correct value
        @Override
        public Object getCellEditorValue() {
            return ftf.getValue();
        }

        // This method fixes the "lost value" bug
        @Override
        public boolean stopCellEditing() {
            // This is what fires when the cell loses focus
            try {
                // Try to commit the edit (parse the text and save it)
                ftf.commitEdit();
            } catch (java.text.ParseException e) {
                // Edit was invalid (e.g., empty), so we *don't* stop editing.
                // This keeps the cell active and prevents the null from saving.
                return false; 
            }
            // Edit was valid, so call the parent's method to save the value
            // and stop the edit.
            return super.stopCellEditing();
        }
    }
}