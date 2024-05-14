import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class BetterCaret extends DefaultCaret {
    @Override
    protected synchronized void damage(Rectangle r) {
        if (r == null) {
            return;
        }

 
        x = r.x;
        y = r.y;
        width = r.width;
        height = r.height;
        repaint(); 
    }

    @Override
    public void paint(Graphics g) {
        JTextComponent comp = getComponent();
        if (comp == null) {
            return;
        }

 
        g.setColor(comp.getBackground());
        g.fillRect(x, y, width, height);

 
        g.setColor(Color.WHITE);
        g.fillRect(x, y, 8, height); 
    }
}
