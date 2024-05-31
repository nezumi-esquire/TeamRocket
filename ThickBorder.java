
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D; 
public class ThickBorder implements Border {
    private final int thickness; 
    public ThickBorder(int thickness) {
        this.thickness = thickness;
    } 
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
 
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(x, y, width - 1, height - 1, 15, 15); 
 
        Stroke roundedStroke = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(roundedStroke); 
 
        g2.setColor(new Color(90, 32, 0)); 
        g2.draw(roundedRectangle);
    } 
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    } 
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}