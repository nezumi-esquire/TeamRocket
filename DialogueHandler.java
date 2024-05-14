import java.util.Scanner;

public class DialogueHandler {
    private Player player;
    private LifeQuest game;
    private Scanner scanner;

    public DialogueHandler(Player player, LifeQuest game, Scanner scanner) {
        this.player = player;
        this.game = game;
        this.scanner = scanner;
    }

    public void runDialogue(Dialogue dialogue) {
        String choice = displayDialogue(dialogue);
        handleDialogueChoice(choice);
    }

    public String displayDialogue(Dialogue dialogue) {
        if (dialogue == null) {
            System.out.println("No dialogue for this encounter.");
            return "";
        }

        TextTyper.typeText("------------------------", 35);
        TextTyper.typeText(dialogue.getNpcName() + ":", 35);

        for (String line : dialogue.getDialogueLines()) {
            TextTyper.typeText("> " + line, 35);
        }
        TextTyper.typeText("------------------------", 35);
        TextTyper.typeText("What will you do?", 35);
        TextTyper.typeText("(O)kay  (S)ilent", 35);

        String choice = scanner.nextLine().trim().toUpperCase();
        while (!choice.equals("O") && !choice.equals("S")) {
            System.out.println("Invalid input. Please enter (O)kay or (S)ilent");
            choice = scanner.next().toUpperCase();
        }
        return choice;
    }

    private void handleDialogueChoice(String choice) {
 
        if (choice.equals("O")) {
 
        } else if (choice.equals("S")) {
 
        }
    }
}
