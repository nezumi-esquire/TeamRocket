import javax.swing.*;
import java.awt.*;

public class EnemyInfoBox extends JPanel {
    private JLabel nameLabel;
    private JProgressBar healthBar;
    private JLabel levelLabel;

    public EnemyInfoBox() {
        setLayout(new GridLayout(3, 1)); 
        nameLabel = new JLabel();
        healthBar = new JProgressBar(0, 100); 
        levelLabel = new JLabel();
        healthBar.setStringPainted(true);
        healthBar.setUI(new RoundedProgressBarUI());
        add(nameLabel);
        add(healthBar);
        add(levelLabel);
    }
    public void updateInfo(Enemy enemy) {
        if (enemy != null){
            nameLabel.setText(enemy.getName());
            healthBar.setValue((int) (((double) enemy.getHealth() / enemy.getMaxHealth()) * 100));
            healthBar.setString(enemy.getHealth() + "%");
            levelLabel.setText("Level " + enemy.getLevel());
        } else {
 
            nameLabel.setText("Enemy: Unknown");
            healthBar.setString("1000%");
        }

    }
}
