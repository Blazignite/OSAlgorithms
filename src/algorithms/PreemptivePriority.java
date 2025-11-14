package algorithms;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
//Santos
public class PreemptivePriority implements OperatingSystemAlgorithm {
	@Override
    public String getInstructions() {
        return "<html>"
             + "<b>Preemptive Priority Scheduling</b><br><br>"
             + "This algorithm schedules processes based on priority.<br>"
             + "The highest priority process runs first.<br>"
             + "If a new process arrives with a higher priority,<br>"
             + "the current process is preempted.<br><br>"
             + "You will be asked to enter the number of processes,<br>"
             + "then each process's Arrival Time, Burst Time, and Priority.<br><br>"
             + "<b>Do you want to continue?</b>"
             + "</html>";
    }

    @Override
    public void run(JTextArea outputArea) {
        outputArea.setText("");
        outputArea.append("--- Running Preemptive Priority ---\n");
        
        String numStr = JOptionPane.showInputDialog("Enter number of processes:");
        int n = Integer.parseInt(numStr);
        
        //logic here
        
        outputArea.append("Process P1 finished at time X\n");
        outputArea.append("Average Wait Time: Y\n");
        outputArea.append("--- Preemptive Priority Finished ---\n");
    }
}
