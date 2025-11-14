package algorithms;
import javax.swing.JTextArea;
//Teodoro
public class RoundRobin implements OperatingSystemAlgorithm {
	@Override
    public String getInstructions() {
        return "<html><b>Round Robin Scheduling</b><br><br>"
             + "This is a preemptive algorithm. Each process is given<br>"
             + "a fixed time slice (Time Quantum) to run.<br><br>"
             + "<b>Do you want to continue?</b></html>";
    }

    @Override
    public void run(JTextArea outputArea) {
        outputArea.setText("--- Running Round Robin ---\n");
        //logic
        outputArea.append("--- Round Robin Finished ---\n");
    }
}
