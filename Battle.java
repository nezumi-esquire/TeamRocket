import javax.swing.*;
import java.util.*;
import java.util.Timer;

public class Battle {
    private Player player;
    private LifeQuest game;
    private LifeQuestUI ui;
    public boolean isPlayerTurn = true;
    public Enemy enemy;
    private final Object turnLock = new Object();
    public static CharacterAnimation skeletonAnimation;
    private static final int TURN_DELAY = 2000;

    public Battle(Player player, LifeQuest game, LifeQuestUI ui, Enemy enemy) {
        this.player = player;
        this.game = game;
        this.ui = ui;
        this.enemy = enemy;
    }
    public void runBattleLoop() {
        while (player.isAlive() && enemy.isAlive() && game.isPlayerPlaying()) {
            displayBattleStatus(enemy);
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

            if (player.isAlive() && enemy.isAlive() && game.isPlayerPlaying()) {
                Timer turnDelayTimer = new Timer();
                TimerTask turnDelayTask = new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> {
                            enemyTurn(enemy);
                            ui.skeletonAnimation.startEnemyWalkAnimation(ui.playerAnimation.getX() + 100); // Start the walk animation
                        });
                    }
                };
                turnDelayTimer.schedule(turnDelayTask, TURN_DELAY);
                isPlayerTurn = true;
            } else {
                if (!enemy.isAlive()){
                    System.out.println("You won!");
                    game.enemyDefeated(ui);
                }
                break; 
            }
            if (!player.isAlive() || !enemy.isAlive()) {
                if (enemy.getHealth() <= 0) {
                    TextTyper.typeText("You won!", 35);
                } else {
                    TextTyper.typeText("You lost!", 35);
                }
                break;
            }
        }
    }
    private void displayBattleStatus(Enemy enemy) {
        TextTyper.typeText("------------------------", 35);
        TextTyper.typeText("Enemy: " + enemy.getName(), 35);
        TextTyper.typeText(enemy.getName() + " HP: " + enemy.getHealth(), 35);
        TextTyper.typeText("------------------------", 35);
    }
    public void playerTurn() {
        int damage = player.getStrength();
        enemy.takeDamage(damage);
        System.out.println("You attacked for " + damage + " damage!");
        SwingUtilities.invokeLater(() -> {
            if (enemy.getHealth() <= 0) {
                ui.skeletonAnimation.playDeathAnimation();
            } else {
                ui.skeletonAnimation.playHurtAnimation(0);
            }
        });
        ui.attackButton.setEnabled(false);
        isPlayerTurn = false;
        synchronized (turnLock) {
            turnLock.notifyAll();
        }
    }
    private void enemyTurn(Enemy enemy) {
        int damage = enemy.getStrength();
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

    public Enemy getEnemy() {
        return enemy;
    }
}