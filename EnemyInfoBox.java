import javax.swing.*;
import java.awt.*;
import java.util.Map; 
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
    public void updateInfo(Map<String, Object> enemyData) {
        if (enemyData != null) {
            nameLabel.setText((String) enemyData.get("name"));
            int currentHealth = (int) enemyData.get("health");
            int maxHealth = (int) enemyData.get("maxHealth");
            healthBar.setValue((int) (((double) currentHealth / maxHealth) * 100));
            healthBar.setString(currentHealth + " / " + maxHealth);
            levelLabel.setText("Level " + enemyData.get("level"));
        } else {
            nameLabel.setText("Enemy: Unknown");
            healthBar.setValue(100); 
            healthBar.setString("???");
        }
    }
}
