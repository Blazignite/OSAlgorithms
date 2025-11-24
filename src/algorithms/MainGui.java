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
    
    //labels
    private JLabel groupLabel;
    private JLabel titleLabel;
    private JLabel dl1;
    private JLabel dl2;
    private JLabel dl3;
    private JLabel dl4;
    
    //design variables
    private Color buttonBg = Color.WHITE;
    private Color buttonTxt = new Color(0, 82, 212);
    private Color txtColor = Color.WHITE;
    
    private Font titleFont = new Font("SansSerif", Font.BOLD, 26);
    private Font descFont = new Font("SansSerif", Font.PLAIN, 14);
    private Font btnFont = new Font("SansSerif", Font.BOLD, 14);
    
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
        mainFrame = new JFrame("Santos, Teodoro, Laraze OS Algorithms Final Project");
        mainFrame.setBounds(100, 100, 800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        mainFrame.setLayout(new BorderLayout()); 
        mainFrame.setLocationRelativeTo(null); 

        card1 = new CardLayout();
        cardContainer = new JPanel(card1);
        
        homePanel = new GradientPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        titleLabel = new JLabel("Welcome to the Operating System Algorithm Simulator!");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(txtColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dl1 = designLabel("This app calculates the value for Preemptive Priority, Round Robin,");
        dl2 = designLabel("and C Scan to demonstrate how it works by showing the table and the chart.");
        dl3 = designLabel("You should provide the required values and the app will simulate it for you!");
        dl4 = designLabel("Please select an algorithm below.");
        
        Dimension buttonSize = new Dimension(350, 40);
        
        btnPP = designButton("Preemptive Priority", buttonSize);
        btnRR = designButton("Round Robin", buttonSize);
        btnCS = designButton("Circular Scan", buttonSize);

        homePanel.add(Box.createVerticalGlue()); 
        homePanel.add(titleLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));
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
        cardContainer.add(homePanel, "HOME");    
        
        groupLabel = new JLabel("Laraze, Santos, Teodoro | 3ITC", SwingConstants.CENTER);
        groupLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        groupLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        groupLabel.setOpaque(true);
        groupLabel.setBackground(Color.WHITE);
        
        //add ti  frame
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
    }
    private JLabel designLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(descFont);
        label.setForeground(txtColor);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    private JButton designButton(String text, Dimension size) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(size);
        btn.setPreferredSize(size);
        btn.setFont(btnFont);
        btn.setBackground(buttonBg);
        btn.setForeground(buttonTxt);
        btn.setFocusPainted(false); 
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); 
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(buttonTxt); 
                btn.setForeground(Color.WHITE);
                
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(buttonBg);
                btn.setForeground(buttonTxt);
                
                btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return btn;
        
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
            alg.run(); 

            card1.show(cardContainer, "RESULTS");
         

        } 
    }
}