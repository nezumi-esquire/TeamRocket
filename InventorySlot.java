import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent; 
public class InventorySlot extends JLabel {
    private Item currentItem;
    private LifeQuestUI ui;
    private Player player; 
    public InventorySlot(LifeQuestUI ui, Player player) {
        this.ui = ui;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentItem != null) {
                    JPopupMenu popupMenu = new JPopupMenu(); 
                    JMenuItem detailsMenuItem = new JMenuItem(currentItem.getName() + ": " + currentItem.getDescription());
                    popupMenu.add(detailsMenuItem); 
                    if (player.isItemEquipped(currentItem)) { 
                        JMenuItem unequipMenuItem = new JMenuItem("Unequip");
                        unequipMenuItem.addActionListener(e1 -> {
                            unequipItem(currentItem);
                        });
                        popupMenu.add(unequipMenuItem);
                    } else if (currentItem.getType().equalsIgnoreCase("Weapon") || currentItem.getType().equalsIgnoreCase("Armor")) {
                        JMenuItem equipMenuItem = new JMenuItem("Equip");
                        equipMenuItem.addActionListener(e1 -> {
                            equipItem(currentItem);
                        });
                        popupMenu.add(equipMenuItem);
                    } 
                    popupMenu.show(InventorySlot.this, e.getX(), e.getY()); 
                }
            }
        });
        this.player = player;
        setOpaque(false); 
    }
    private void equipItem(Item item) {
        player.equipItem(item);
        ui.updateInventoryUI();
        ui.updateHealthBar();
        ui.updateManaBar();
    }
    private void unequipItem(Item item) {
        player.unequipItem(item); 
        ui.updateHealthBar(); 
        ui.updateManaBar(); 
        ui.updateInventoryUI(); 
    } 
    public void setItem(Item item) {
        this.currentItem = item;
        if (item != null) {
            setIcon(item.getIcon());
        } else {
            setIcon(null);
        }
    } 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
 
        g.setColor(new Color(210, 180, 140)); 
        g.fillRect(2, 2, getWidth() - 4, getHeight() - 4); 
        Icon icon = getIcon();
        if (icon != null) {
 
            int x = (getWidth() - icon.getIconWidth()) / 2;
            int y = (getHeight() - icon.getIconHeight()) / 2; 
 
            icon.paintIcon(this, g, x, y);
        }
    }
}
