package algorithms;
import javax.swing.JTextArea;

public interface OperatingSystemAlgorithm {
	String getInstructions();
    
    void run(JTextArea outputArea);
}
