import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;
public class LifeQuest {
    private Player player;
    private LifeQuestUI ui;
    private DatabaseConnector databaseConnector;
    private Map<String, Object> playerData;
    private boolean playerIsPlaying = false;
    public List<Quest> activeQuests = new ArrayList<>();
    private int activeQuestCount = 3;
    public LifeQuest(Map<String, Object> playerData, DatabaseConnector databaseConnector) {
        this.playerData = playerData;
        this.databaseConnector = databaseConnector;
        player = new Player(playerData);
    }
    public boolean isPlayerPlaying() {
        return playerIsPlaying;
    }
    public void setPlayerIsPlaying(boolean playing) {
        this.playerIsPlaying = playing;
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
    public void loadQuests(LifeQuestUI ui) {
        try {
            List<Map<String, Object>> questData = databaseConnector.loadQuestData();
            for (Map<String, Object> quest : questData) {
                String name = (String) quest.get("name");
                String description = (String) quest.get("description");
                activeQuests.add(new Quest(name, description));
                if (activeQuests.size() >= activeQuestCount) {
                    break;
                }
            }

            SwingUtilities.invokeLater(() -> ui.updateQuestsUI());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void completeQuest(int questIndex) {
        if (questIndex >= 0 && questIndex < activeQuests.size()) {
            Quest completedQuest = activeQuests.get(questIndex);
            completedQuest.setCompleted(true);

            int expReward = 0;
            try {
                expReward = (int) databaseConnector.loadQuestData().get(questIndex).get("exp");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            player.gainExperience(expReward);

            generateNewQuest(questIndex);
        } else {
            System.err.println("Invalid quest index: " + questIndex);
        }
    }

    public void declineQuest(int questIndex) {
        if (questIndex >= 0 && questIndex < activeQuests.size()) {
            Quest declinedQuest = activeQuests.get(questIndex);
            declinedQuest.setDeclined(true);

            generateNewQuest(questIndex);
        } else {
            System.err.println("Invalid quest index: " + questIndex);
        }
    }

    private void generateNewQuest(int questIndex) {
        Timer newQuestTimer = new Timer();
        newQuestTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    try {
                        if (ui != null) {
                            activeQuests.set(questIndex, loadNewQuestFromDatabase());
                            ui.updateQuestsUI();
                        } else {
                            System.err.println("Error: LifeQuestUI reference is null in generateNewQuest.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 2000);
    }
    public void setUi(LifeQuestUI ui) {
        this.ui = ui;
    }
    private Quest loadNewQuestFromDatabase() throws SQLException {
        Map<String, Object> questData = databaseConnector.loadRandomQuestData();
        if (questData != null) {
            return new Quest((String) questData.get("name"), (String) questData.get("description"));
        } else {

            System.err.println("Error: Failed to load a new quest from the database.");

            return new Quest("No Quest Available", "Please try again later.");
        }
    }
    public void startGameLoop(LifeQuestUI ui) {
        this.ui = ui; // Set the ui reference
        setPlayerIsPlaying(true); // Enable the game loop

        new Thread(() -> {
            while (isPlayerPlaying()) {
                runGameLoop(ui); // Run the game loop on a separate thread
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void runGameLoop(LifeQuestUI ui) {
        if (playerIsPlaying) {
            DialogueHandler dialogueHandler = new DialogueHandler(player, this, ui.getScanner());
            Encounter encounter = new Encounter();

            EncounterResult result = encounter.generateEncounter();
            switch (result.getEncounterType()) {
                case COMBAT:
                    try {
                        Map<String, Object> enemyData = databaseConnector.loadRandomEnemyData();
                        CharacterAnimation enemyAnimation = ui.enemyAnimations.get((String) enemyData.get("name"));
                        Battle battle = new Battle(player, this, ui, enemyData, enemyAnimation);
                        SwingUtilities.invokeLater(() -> {
                            ui.setBattle(battle);
                            ui.createEnemyInfoBox(enemyData);
                        });
                        battle.runBattleLoop();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

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
}