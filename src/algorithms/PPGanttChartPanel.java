package algorithms;

import javax.swing.*;
import java.awt.*;

public class PPGanttChartPanel extends JPanel {
    
    private PPGanttChart[] ganttChart; 
    private int totalTime;        

    private Color[] myColors = {
        Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, 
        Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW
    };

    public PPGanttChartPanel(PPGanttChart[] ganttChart) {
        this.ganttChart = ganttChart;
        this.totalTime = 0;
        for(int i = 0; i < ganttChart.length; i++) {
            if (ganttChart[i] != null) {
                this.totalTime = ganttChart[i].endTime;
            } else {
                break;
            }
        }
        
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int w = getWidth() - 60; 
        int h = 50;             
        int startX = 30;      
        int startY = 80;       

        for (int i = 0; i < ganttChart.length; i++) {
            PPGanttChart item = ganttChart[i];
            
            if (item == null)
            	break;

            int duration = item.endTime - item.startTime;
            if (duration <= 0) continue;

            int rectX = startX + (int) ((double) item.startTime / totalTime * w);
            int rectW = (int) ((double) duration / totalTime * w);
            
            // color
            if (item.pid == -1) {
                g.setColor(Color.LIGHT_GRAY); 
            } else {
                g.setColor(myColors[item.pid % myColors.length]);
            }

            g.fillRect(rectX, startY, rectW, h);
            
            g.setColor(Color.BLACK);
            g.drawRect(rectX, startY, rectW, h);
            
            String label = (item.pid == -1) ? "Idle" : "P" + item.pid;
            
            if (rectW > 20) {
                g.drawString(label, rectX + (rectW / 2) - 5, startY + 30);
            }
            
            g.drawString(String.valueOf(item.startTime), rectX, startY + h + 15);
        }
        g.drawString(String.valueOf(totalTime), startX + w, startY + h + 15);
    }
}