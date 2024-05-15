import javax.swing.*;

public class Battle {
    private Player player;
    private LifeQuest game;
    private LifeQuestUI ui;
    public boolean isPlayerTurn = true;
    public Enemy enemy;
    private final Object turnLock = new Object();

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
            synchronized (turnLock) { // Added synchronized block
                while (isPlayerTurn) {
                    try {
                        turnLock.wait(); // Wait for notification
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (player.isAlive() && enemy.isAlive() && game.isPlayerPlaying()) {
                enemyTurn(enemy);
                TextTyper.typeText("Enemy attacks!", 35);
                isPlayerTurn = true;
            } else {
                if (!enemy.isAlive()){
                    SwingUtilities.invokeLater(() -> {
                        ui.removeEnemyAndInfoBox();
                    }); // Remove on EDT
                    System.out.println("Battle: Enemy defeated. Removing enemy and info box.");
                }
                break; // Exit loop if game over after player's turn
            }
            // Set player's turn after checking for game over
            isPlayerTurn = true;
            if (!player.isAlive() || !enemy.isAlive()) {
                if (enemy.getHealth() <= 0) {
                    TextTyper.typeText("You won!", 35);
                } else {
                    TextTyper.typeText("You lost!", 35);
                }
                break;
            }
        }
        System.out.println("Exiting battle loop.");
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
        ui.attackButton.setEnabled(false);
        isPlayerTurn = false;
        synchronized (turnLock) {
            turnLock.notifyAll();
        }
    }
    private void enemyTurn(Enemy enemy) {
        int damage = enemy.getStrength();
        player.takeDamage(damage);

        ui.updateHealthBar();
        ui.updateManaBar();
        isPlayerTurn = true;
    }

    public Enemy getEnemy() {
        return enemy;
    }
}