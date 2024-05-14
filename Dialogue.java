import java.util.List;

public class Dialogue {
    private String npcName; 
    private List<String> dialogueLines;

    public Dialogue(String npcName, List<String> dialogueLines) {
        this.npcName = npcName;
        this.dialogueLines = dialogueLines;
    }

 
    public String getNpcName() {
        return npcName;
    }

    public List<String> getDialogueLines() {
        return dialogueLines;
    }
    public String getText() {
        return String.join(" ", dialogueLines);
    }
}