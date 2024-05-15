import java.util.List;
import java.util.ArrayList;

public class EnemyGenerator {
    private List<Enemy> enemyList = new ArrayList<>();
    public void refreshEnemyList() {
        enemyList.clear();
        enemyList.add(new Enemy("Skelly", 12, 2, 1));
        enemyList.add(new Enemy("Beefy Skelly", 18, 1, 2));
        enemyList.add(new Enemy("Funny Bone", 9, 5, 2));
        enemyList.add(new Enemy("Wiz", 30, 10, 5));
    }
    public Enemy generateEnemy() {
        return enemyList.get((int) (Math.random() * enemyList.size()));
    }
    public List<Enemy> getEnemyList() {
        return enemyList;
    }
}