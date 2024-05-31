import javax.swing.*;
import java.util.*;
import java.util.Timer;
public class Battle {
    private Player player;
    private LifeQuest game;
    private LifeQuestUI ui;
    public boolean isPlayerTurn = true;
    public Map<String, Object> enemyData;
    private final Object turnLock = new Object();
    private static final int TURN_DELAY = 2000;
    private HashMap<String, CharacterAnimation> enemyAnimations;
    public Battle(Player player, LifeQuest game, LifeQuestUI ui, Map<String, Object> enemyData, CharacterAnimation enemyAnimation) {
        this.player = player;
        this.game = game;
        this.ui = ui;
        this.enemyData = enemyData;
    }
    public void runBattleLoop() {
        displayBattleStatus();
        while (player.isAlive() && (int) enemyData.get("health") > 0 && game.isPlayerPlaying()) {

            SwingUtilities.invokeLater(() -> ui.attackButton.setEnabled(true));
            synchronized (turnLock) { 
                while (isPlayerTurn) {
                    try {
                        turnLock.wait(); 
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            String enemyName = (String) enemyData.get("name");
            CharacterAnimation enemyAnimation = enemyAnimations.get(enemyName);
            if (player.isAlive() && (int) enemyData.get("health") > 0 && game.isPlayerPlaying()) {
                Timer turnDelayTimer = new Timer();
                TimerTask turnDelayTask = new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> {
                            enemyTurn();
                            enemyAnimation.startEnemyWalkAnimation(ui.playerAnimation.getX() + 100);
                        });
                    }
                };
                turnDelayTimer.schedule(turnDelayTask, TURN_DELAY);
                isPlayerTurn = true;
            } else {
                if ( (int) enemyData.get("health") <= 0){
                    enemyDefeated(ui);
                    System.out.println("You won!");
                    game.enemyDefeated(ui);
                }
                break; 
            }
        }
    }
    private void displayBattleStatus() {
        TextTyper.typeText("------------------------", 35);
        TextTyper.typeText("Enemy: " + enemyData.get("name"), 35);
        TextTyper.typeText(enemyData.get("name") + " HP: " + enemyData.get("health"), 35);
        TextTyper.typeText("------------------------", 35);
    }
    public void playerTurn() {
        int damage = player.getStrength();
        int currentHealth = (int) enemyData.get("health");
        currentHealth -= damage;
        enemyData.put("health", currentHealth);
        System.out.println("You attacked for " + damage + " damage!");
        String enemyName = (String) enemyData.get("name");

        CharacterAnimation enemyAnimation = enemyAnimations.get(enemyName);
        SwingUtilities.invokeLater(() -> {
            if ((int)enemyData.get("health") <= 0) {
                enemyAnimation.playDeathAnimation();
                game.enemyDefeated(ui);
                return;
            } else {
                enemyAnimation.playHurtAnimation(0);
            }
        });
        ui.attackButton.setEnabled(false);
        isPlayerTurn = false;
        synchronized (turnLock) {
            turnLock.notifyAll();
        }
    }
    private void enemyTurn() {
        int damage = (int) enemyData.get("strength");
        player.takeDamage(damage);
        Timer hurtDelayTimer = new Timer();
        hurtDelayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> ui.playerAnimation.playHurtAnimation(0));
            }
        }, 3000);
        ui.updateHealthBar();
        ui.updateManaBar();
        isPlayerTurn = true;
    }
    public Map<String, Object> getEnemyData() {
        return enemyData;
    }
    public void setEnemyAnimations(HashMap<String,CharacterAnimation> enemyAnimations) {
        this.enemyAnimations = enemyAnimations;
    }
    public void enemyDefeated(LifeQuestUI ui){
        player.gainExperience(15);
        if (enemyData.get("name").equals("Mr. Slime")){
            player.gainExperience(8);
        }
        ui.removeEnemyAndInfoBox();
        ui.updateLevelBar();
    }
}