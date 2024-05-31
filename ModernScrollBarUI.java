import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*; 
class ModernScrollBarUI extends BasicScrollBarUI {
    private Color troughColor; 
    public ModernScrollBarUI(Color troughColor) {
        this.troughColor = troughColor;
    } 
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(troughColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height); 
        g2.dispose();
    } 
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
        Color thumbColor = new Color(128, 128, 128); 
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width - 2, thumbBounds.height - 2, 10, 10);  
        g2.dispose();
    } 
 
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    } 
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    } 
    private JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }
} 
