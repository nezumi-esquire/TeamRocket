import javax.swing.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class EnemyGenerator {
    private HashMap<String, CharacterAnimation> enemyAnimations = new HashMap<>();
    private JLayeredPane layeredPane;
    private LifeQuestUI ui;

    private List<Enemy> enemyList = new ArrayList<>();
    public void refreshEnemyList() {
        enemyList.clear();
        enemyAnimations.put("Skelly", new CharacterAnimation(
                new SpriteAnimation("/resources/gifs/skeleton/skelly.gif", 100, 3.5),
                layeredPane, ui // Pass layeredPane and UI to CharacterAnimation
        ));
        enemyAnimations.put("Slime", new CharacterAnimation(
                new SpriteAnimation("/resources/gifs/slime/slime idle.gif", 100, 2.1),
                layeredPane, ui
        ));
        enemyList.add(new Enemy("Skelly", 9, 2, 1, enemyAnimations.get("Skelly")));
        enemyList.add(new Enemy("Slime", 6, 1, 1, enemyAnimations.get("Slime")));
    }
    public Enemy generateEnemy() {
        Enemy enemy = enemyList.get((int) (Math.random() * enemyList.size()));

        // Load animations for the generated enemy
        CharacterAnimation animation = enemyAnimations.get(enemy.getName());
        if (animation != null) {
            // Load animations based on enemy type
            try {
                if (enemy.getName().equals("Skeleton")) {
                    animation.setAttackAnimation(new SpriteAnimation(getClass().getResource("/resources/gifs/skeleton/skelly attack.gif").getPath(), 100, 3.5));
                    animation.setEnemyWalkAnimation(new SpriteAnimation(getClass().getResource("/resources/gifs/skeleton/skelly walk.gif").getPath(), 100, 3.5));
                    animation.setHurtAnimation(new SpriteAnimation(getClass().getResource("/resources/gifs/skeleton/skelly hurt.gif").getPath(), 100, 3.5));
                    animation.setDeathAnimation(new SpriteAnimation(getClass().getResource("/resources/gifs/skeleton/skelly death.gif").getPath(), 100, 3.5));

                } else if (enemy.getName().equals("Slime")) {
                    String slimeAttackPath = URLDecoder.decode(getClass().getResource("/resources/gifs/slime/slime attack.gif").getPath(), "UTF-8");
                    System.out.println("Slime Attack Path: " + slimeAttackPath);
                    animation.setAttackAnimation(new SpriteAnimation(slimeAttackPath, 100, 2.1));

                    String slimeWalkPath = URLDecoder.decode(getClass().getResource("/resources/gifs/slime/slime walk.gif").getPath(), "UTF-8");
                    System.out.println("Slime Walk Path: " + slimeWalkPath);
                    animation.setEnemyWalkAnimation(new SpriteAnimation(slimeWalkPath, 100, 2.1));

                    String slimeHurtPath = URLDecoder.decode(getClass().getResource("/resources/gifs/slime/slime hurt.gif").getPath(), "UTF-8");
                    System.out.println("Slime Hurt Path: " + slimeHurtPath); // Debugging print
                    animation.setHurtAnimation(new SpriteAnimation(slimeHurtPath, 100, 2.1));

                    String slimeDeathPath = URLDecoder.decode(getClass().getResource("/resources/gifs/slime/slime death.gif").getPath(), "UTF-8");
                    System.out.println("Slime Death Path: " + slimeDeathPath); // Debugging print
                    animation.setDeathAnimation(new SpriteAnimation(slimeDeathPath, 100, 2.1));
                }

                // ... (Add similar blocks for other enemy types) ...
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading animations for " + enemy.getName() + ". Check file paths and existence.");
            }
        }

        return enemy;
    }
    public List<Enemy> getEnemyList() {
        return enemyList;
    }
    public EnemyGenerator(JLayeredPane layeredPane, LifeQuestUI ui) {
        this.enemyAnimations = new HashMap<>();
        this.layeredPane = layeredPane;
        this.ui = ui;
    }
    public HashMap<String, CharacterAnimation> getEnemyAnimations() {
        return enemyAnimations;
    }
}