package algorithms; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class MainGui {
    private JFrame mainFrame;

    private JPanel cardContainer;
    private CardLayout card1; 
    
    private JPanel homePanel;
    private JButton btnPP;
    private JButton btnRR;
    private JButton btnCS;
    
    private JPanel resultsPanel;
    private JTextArea resArea;
    private JScrollPane resScroll;
    private JButton btnBack; 
    
    //labels
    private JLabel groupLabel;
    private JLabel titleLabel;
    private JLabel dl1;
    private JLabel dl2;
    private JLabel dl3;
    private JLabel dl4;

    private OperatingSystemAlgorithm santosPrioAlgo;
    private OperatingSystemAlgorithm teodoroRRAlgo;
    private OperatingSystemAlgorithm larazeCSAlgo;
    
    public static void main(String[] args) {
        MainGui mainGui = new MainGui();
        mainGui.mainFrame.setVisible(true);        
    }
    
    public MainGui() {
        santosPrioAlgo = new PreemptivePriority();
        teodoroRRAlgo = new RoundRobin();
        larazeCSAlgo = new CircularScan();
        startApp();
    }
    
    private void startApp() {
        // --- 1. MAIN FRAME ---
        mainFrame = new JFrame("Santos, Teodoro, Laraze OS Algorithms Final Project");
        mainFrame.setBounds(100, 100, 800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        mainFrame.setLayout(new BorderLayout()); 
        mainFrame.setLocationRelativeTo(null); 

        card1 = new CardLayout();
        cardContainer = new JPanel(card1);
        
        homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Welcome to the Operating System ALgorithm Simulator!");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dl1 = new JLabel("This apps calculates the value for Preemptive Priority, Round Robin,");
        dl1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dl1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        dl2 = new JLabel("and C Scan to demonstrate how it works by showing the table and the chart.");
        dl2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dl2.setAlignmentX(Component.CENTER_ALIGNMENT);

        dl3 = new JLabel("You should provide the required values and the app will simulate it foor you!");
        dl3.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dl3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dl4 = new JLabel("Please select an algorithm below.");
        dl4.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dl4.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        Dimension buttonSize = new Dimension(350, 40);
        
        btnPP = new JButton("Preemptive Priority");
        btnPP.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPP.setMaximumSize(buttonSize);
        
        btnRR = new JButton("Round Robin");
        btnRR.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRR.setMaximumSize(buttonSize);

        btnCS = new JButton("Circular Scan");
        btnCS.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCS.setMaximumSize(buttonSize);

        homePanel.add(Box.createVerticalGlue()); 
        homePanel.add(titleLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        homePanel.add(dl1);
        homePanel.add(dl2);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        homePanel.add(dl3);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        homePanel.add(dl4);
        homePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        homePanel.add(btnPP);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        homePanel.add(btnRR);
        homePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        homePanel.add(btnCS);
        homePanel.add(Box.createVerticalGlue()); 

        resultsPanel = new JPanel(new BorderLayout(10, 10)); 
        resultsPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); 

        resArea = new JTextArea();
        resArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        resArea.setEditable(false); 
        resScroll = new JScrollPane(resArea);
        resultsPanel.add(resScroll, BorderLayout.CENTER); 
        
        btnBack = new JButton("Back to Home");
        resultsPanel.add(btnBack, BorderLayout.SOUTH); 
        
        cardContainer.add(homePanel, "HOME");    
        cardContainer.add(resultsPanel, "RESULTS"); 
        
        groupLabel = new JLabel("Laraze, Santos, Teodoro | 3ITC", SwingConstants.CENTER);
        groupLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        //ADD TO FRAME
        mainFrame.add(cardContainer, BorderLayout.CENTER); 
        mainFrame.add(groupLabel, BorderLayout.SOUTH);    
        
        btnPP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(santosPrioAlgo);
            }
        });
        
        btnRR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(teodoroRRAlgo);
            }
        });
        
        btnCS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runAlgorithm(larazeCSAlgo);
            }
        });
        
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                card1.show(cardContainer, "HOME");
            }
        });
    }

    private void runAlgorithm(OperatingSystemAlgorithm alg) {
        String instructions = alg.getInstructions();
        int choice = JOptionPane.showConfirmDialog(
            mainFrame,                        
            instructions,                
            "Algorithm Confirmation",    
            JOptionPane.YES_NO_OPTION,   
            JOptionPane.QUESTION_MESSAGE 
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            alg.run(resArea); 

            card1.show(cardContainer, "RESULTS");
        } 
    }
}