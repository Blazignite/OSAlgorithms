package algorithms;
import javax.swing.JTextArea;
//Laraze
public class CircularScan implements OperatingSystemAlgorithm {

    @Override
    public String getInstructions() {
        return "<html><b>C-SCAN Disk Scheduling</b><br><br>"
             + "The disk arm moves from one end to the other, servicing<br>"
             + "requests. When it reaches the end, it immediately returns<br>"
             + "to the beginning without servicing any requests on the way back.<br><br>"
             + "<b>Do you want to continue?</b></html>";
    }

    @Override
    public void run() {
    }
}
