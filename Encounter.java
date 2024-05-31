import java.util.List;
import java.util.Arrays; 
public class Encounter {
    private List<Dialogue> allDialogues = Arrays.asList(
            new Dialogue("Mysterious Stranger", Arrays.asList("The path ahead is fraught with danger...")),
            new Dialogue("Wise Old Woman", Arrays.asList("Beware the shadows, they hold secrets."))
    ); 
    private Dialogue generateDialogue() {
        int randomIndex = (int) (Math.random() * allDialogues.size());
        return allDialogues.get(randomIndex);
    } 
    public EncounterResult generateEncounter() {
        double probability = Math.random(); 
        if (probability < 0.85) {
            return new EncounterResult(EncounterType.COMBAT, null, null, 0);
        } else if (probability < 0.999) {
            Dialogue npcDialogue = generateDialogue();
            return new EncounterResult(EncounterType.DIALOGUE, null, npcDialogue, 0);
        } else {
            return new EncounterResult(EncounterType.BONUS, null, null, 0);
        }
    }
}