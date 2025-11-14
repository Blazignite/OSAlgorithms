package algorithms;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGui {
	private JFrame frame;
    private JTextArea outputArea;
    private JPanel buttonPanel;
    private JButton btnPreemptivePriority;
    private JButton btnRoundRobin;
    private JButton btnCircularScan;
    private JScrollPane scrollPane; 


    private OperatingSystemAlgorithm priorityAlg;
    private OperatingSystemAlgorithm rrAlg;
    private OperatingSystemAlgorithm cscanAlg;
    
    public static void main(String[] args) {
        // Run the GUI creation on the "Event Dispatch Thread"
        // This is the standard, safe way to start a Swing application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainGui window = new MainGui();
                    window.frame.setVisible(true); // Show the window
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public MainGui() {
        // --- "PLUG IN" YOUR TEAM'S WORK ---
        // This is where we create the algorithm objects
        priorityAlg = new PreemptivePriority();
        rrAlg = new RoundRobin();
        cscanAlg = new CircularScan();
        // ------------------------------------

        // Call the method that builds the GUI
        initialize();
    }
    
private void initialize() {
        
        // --- 1. SET UP THE MAIN WINDOW (JFrame) ---
        frame = new JFrame();
        frame.setTitle("OS Algorithm Showcase");
        frame.setBounds(100, 100, 700, 500); // (x, y, width, height)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stop app on close
        frame.setLayout(new BorderLayout()); // Use BorderLayout (North, Center, South, etc.)
        frame.setLocationRelativeTo(null); // Center the window on screen

        // --- 2. SET UP THE OUTPUT AREA (JTextArea) ---
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Nice font for output
        outputArea.setEditable(false); // User can't type in it
        
        // Add the text area to a scroll pane
        scrollPane = new JScrollPane(outputArea);
        
        // Add the scroll pane to the "Center" of our BorderLayout
        frame.add(scrollPane, BorderLayout.CENTER);

        // --- 3. SET UP THE BUTTON PANEL (JPanel) ---
        buttonPanel = new JPanel();
        // Add the panel to the "North" (top) of our BorderLayout
        frame.add(buttonPanel, BorderLayout.NORTH);
        // (It uses FlowLayout by default, which is fine for buttons)

        // --- 4. CREATE & HOOK UP THE BUTTONS ---
        
        // == PREEMPTIVE PRIORITY BUTTON ==
        btnPreemptivePriority = new JButton("Preemptive Priority");
        btnPreemptivePriority.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // This is the "wiring"
                runAlgorithm(priorityAlg);
            }
        });
        
        // == ROUND ROBIN BUTTON ==
        btnRoundRobin = new JButton("Round Robin");
        btnRoundRobin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(rrAlg);
            }
        });
        
        // == C-SCAN BUTTON ==
        btnCircularScan = new JButton("Circular Scan");
        btnCircularScan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(cscanAlg);
            }
        });

        // Add the buttons to the panel
        buttonPanel.add(btnPreemptivePriority);
        buttonPanel.add(btnRoundRobin);
        buttonPanel.add(btnCircularScan);
    }
private void runAlgorithm(OperatingSystemAlgorithm alg) {
    // 1. Get the instructions from the object
    String instructions = alg.getInstructions();
    
    // 2. Show the confirmation pop-up
    int choice = JOptionPane.showConfirmDialog(
        frame,                       // Parent window
        instructions,                // Your message (with HTML!)
        "Algorithm Confirmation",    // Pop-up title
        JOptionPane.YES_NO_OPTION,   // Button types
        JOptionPane.QUESTION_MESSAGE // Icon
    );
    
    // 3. Check the user's choice and run
    if (choice == JOptionPane.YES_OPTION) {
        // User clicked YES! Run the algorithm.
        alg.run(outputArea);
    } else {
        // User clicked NO or closed the box
        outputArea.setText("Run cancelled by user.");
    }
}
}
