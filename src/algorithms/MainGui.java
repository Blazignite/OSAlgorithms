package algorithms; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class MainGui {
    private JFrame frame;
    private JLabel footerLabel;

    // Panels for CardLayout
    private JPanel cardContainer;
    private CardLayout cardLayout; 
    
    // Card 1: Home Screen
    private JPanel homePanel;
    private JButton btnPreemptivePriority;
    private JButton btnRoundRobin;
    private JButton btnCircularScan;
    
    // Card 2: Results Screen (We keep this strictly for background logging now)
    private JPanel resultsPanel;
    private JTextArea outputArea;
    private JScrollPane scrollPane;
    private JButton btnBack; 

    // Algorithm objects
    private OperatingSystemAlgorithm priorityAlg;
    private OperatingSystemAlgorithm rrAlg;
    private OperatingSystemAlgorithm cscanAlg;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainGui window = new MainGui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public MainGui() {
        priorityAlg = new PreemptivePriority();
        rrAlg = new RoundRobin();
        cscanAlg = new CircularScan();
        initialize();
    }
    
    private void initialize() {
        // --- 1. MAIN FRAME ---
        frame = new JFrame();
        frame.setTitle("OS Algorithm Showcase");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setLayout(new BorderLayout()); 
        frame.setLocationRelativeTo(null); 

        // --- 2. CARD CONTAINER ---
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        
        // --- 3. HOME PANEL ---
        homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Welcome to the Operating System Algorithm Simulator");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel1 = new JLabel("This app aims to simulate and calculate the values of Preemptive Priority, Round Robin,");
        descLabel1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel2 = new JLabel("and C Scan to demonstrate how it works.");
        descLabel2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel3 = new JLabel("You will simply provide the values and the application will simulate it for you!");
        descLabel3.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel4 = new JLabel("To get started, please select an Operating System Algorithm below.");
        descLabel4.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        Dimension buttonSize = new Dimension(350, 40);
        
        btnPreemptivePriority = new JButton("Preemptive Priority");
        btnPreemptivePriority.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPreemptivePriority.setMaximumSize(buttonSize);
        
        btnRoundRobin = new JButton("Round Robin");
        btnRoundRobin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRoundRobin.setMaximumSize(buttonSize);

        btnCircularScan = new JButton("Circular Scan");
        btnCircularScan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCircularScan.setMaximumSize(buttonSize);

        homePanel.add(Box.createVerticalGlue()); 
        homePanel.add(titleLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        homePanel.add(descLabel1);
        homePanel.add(descLabel2);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        homePanel.add(descLabel3);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        homePanel.add(descLabel4);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        homePanel.add(btnPreemptivePriority);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        homePanel.add(btnRoundRobin);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        homePanel.add(btnCircularScan);
        homePanel.add(Box.createVerticalGlue()); 

        // --- 4. RESULTS PANEL ---
        resultsPanel = new JPanel(new BorderLayout(10, 10)); 
        resultsPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); 

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        outputArea.setEditable(false); 
        scrollPane = new JScrollPane(outputArea);
        resultsPanel.add(scrollPane, BorderLayout.CENTER); 
        
        btnBack = new JButton("Back to Home");
        resultsPanel.add(btnBack, BorderLayout.SOUTH); 
        
        // --- 5. ADD CARDS ---
        cardContainer.add(homePanel, "HOME");    
        cardContainer.add(resultsPanel, "RESULTS"); 
        
        // --- 6. FOOTER ---
        footerLabel = new JLabel("Laraze, Santos, Teodoro | 3ITC", SwingConstants.CENTER);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // --- 7. ADD TO FRAME ---
        frame.add(cardContainer, BorderLayout.CENTER); 
        frame.add(footerLabel, BorderLayout.SOUTH);    
        
        // --- 8. LISTENERS ---
        
        btnPreemptivePriority.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(priorityAlg);
            }
        });
        
        btnRoundRobin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(rrAlg);
            }
        });
        
        btnCircularScan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(cscanAlg);
            }
        });
        
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardContainer, "HOME");
            }
        });
    }

    // --- MODIFIED runAlgorithm Method ---
    private void runAlgorithm(OperatingSystemAlgorithm alg) {
        String instructions = alg.getInstructions();
        int choice = JOptionPane.showConfirmDialog(
            frame,                        
            instructions,                
            "Algorithm Confirmation",    
            JOptionPane.YES_NO_OPTION,   
            JOptionPane.QUESTION_MESSAGE 
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // 1. Run the algorithm (We still pass outputArea, but we don't show it)
            alg.run(outputArea); 
            
            // 2. WE REMOVED THE LINE THAT SWAPS THE CARD.
            // The user stays on "HOME", and the Algorithm class will pop up its own window.
        } 
    }
}