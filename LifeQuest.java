import javax.swing.*;

public class LifeQuest {
    private EnemyGenerator enemyGenerator;
    private final Player player;
    private final boolean playerIsPlaying;
    public LifeQuest(JLayeredPane layeredPane, LifeQuestUI ui) {
        player = new Player("Hero");
        enemyGenerator = new EnemyGenerator(layeredPane, ui);
        playerIsPlaying = true;
    }
    public boolean isPlayerPlaying() {
        return playerIsPlaying;
    }
    public Player getPlayer() {
        return player;
    }
    public void enemyDefeated(LifeQuestUI ui) {
        System.out.println("Enemy defeated!");
        SwingUtilities.invokeLater(() -> {
            ui.removeEnemyAndInfoBox();
        });
    }
    public void runGameLoop(LifeQuestUI ui) {
        DialogueHandler dialogueHandler = new DialogueHandler(player, this, ui.getScanner());
        Encounter encounter = new Encounter();

 
        EncounterResult result = encounter.generateEncounter();
        switch (result.getEncounterType()) {
            case COMBAT:
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