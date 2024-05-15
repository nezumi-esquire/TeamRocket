import javax.swing.*;

public class LifeQuest {
    private EnemyGenerator enemyGenerator;
    private Player player;
    private boolean playerIsPlaying;

    public LifeQuest() {
        player = new Player("Hero");
        enemyGenerator = new EnemyGenerator();
        playerIsPlaying = true;
    }

    public void stopGame() {
        playerIsPlaying = false;
    }

    public boolean isPlayerPlaying() {
        return playerIsPlaying;
    }
    public Player getPlayer() {
        return player;
    }

    public void runGameLoop(LifeQuestUI ui, EnemyInfoBox enemyInfoBox) {
        DialogueHandler dialogueHandler = new DialogueHandler(player, this, ui.getScanner());
        Encounter encounter = new Encounter();

        // Removed while loop - Single encounter handling per call
        EncounterResult result = encounter.generateEncounter();
        switch (result.getEncounterType()) {
            case COMBAT:
                    System.out.println("Combat encounter started.");
                    enemyGenerator.refreshEnemyList();
                    Enemy enemy = enemyGenerator.generateEnemy();

                    Battle battle = new Battle(player, this, ui, enemy);
                    SwingUtilities.invokeLater(() -> {
                        ui.setBattle(battle);
                        ui.createEnemyInfoBox(enemy);
                    });

                    battle.runBattleLoop();
                    break;
                case DIALOGUE:
                    Dialogue dialogue = result.getDialogue();
                    dialogueHandler.runDialogue(dialogue);
                    break;
                case BONUS:
                    TextTyper.typeText("---The gentle scenery relaxes you---", 35);
                    int bonusHealth = 8;
                    player.heal(bonusHealth);
                    ui.updateHealthBar();
                    ui.updateManaBar();
                    TextTyper.typeText("---You regenerated " + bonusHealth + " health---", 35);
                    break;
                default:
                    break;
        }
    }
}