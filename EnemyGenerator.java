import java.util.List;
import java.util.ArrayList;

public class EnemyGenerator {
    private List<Enemy> enemyList = new ArrayList<>();
    public void refreshEnemyList() {
        enemyList.clear();
        enemyList.add(new Enemy("Goblin", 15, 3, 1));
        enemyList.add(new Enemy("Gymbro Goblin", 15, 5, 2));
        enemyList.add(new Enemy("Him-blin", 15, 8, 3));
        enemyList.add(new Enemy("Drake", 15, 13, 5));
    }
    public Enemy generateEnemy() {
        return enemyList.get((int) (Math.random() * enemyList.size()));
    }
    public List<Enemy> getEnemyList() {
        return enemyList;
    }
}