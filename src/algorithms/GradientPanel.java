package algorithms;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel{
	private Color gradientStart = new Color(0, 82, 212); 
    private Color gradientEnd = new Color(143, 148, 251);

    public GradientPanel() {
        // Optional: Set a layout by default
        this.setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // High-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int w = getWidth();
        int h = getHeight();
        
        // Draw Gradient from Top-Left to Bottom-Right
        GradientPaint gp = new GradientPaint(0, 0, gradientStart, w, h, gradientEnd);
        
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}
