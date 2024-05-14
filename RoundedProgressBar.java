import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

class RoundedProgressBarUI extends BasicProgressBarUI {
    @Override
    public void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = progressBar.getWidth();
        int height = progressBar.getHeight();
        int arcSize = height; 

 
        Insets b = progressBar.getInsets(); 
        int barRectWidth = width - (b.right + b.left);
        int barRectHeight = height - (b.top + b.bottom);
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight); 
        int barWidth = progressBar.getWidth();
        int barHeight = progressBar.getHeight();

 
        g2d.setColor(progressBar.getForeground());
        g2d.fillRoundRect(2, 2, amountFull - 4, barHeight - 4, arcSize, arcSize);

 
        g2d.setColor(progressBar.getBackground());
        g2d.fillRoundRect(amountFull, 2, barWidth - amountFull - 2, barHeight - 4, arcSize, arcSize);

 
        paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);

        g2d.dispose();
    }
}
