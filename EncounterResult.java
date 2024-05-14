
public class EncounterResult {
    private EncounterType encounterType;
    private Enemy enemy;
    private Dialogue dialogue;
    private int bonusAmount;
    
    public EncounterResult(EncounterType encounterType, Enemy enemy, Dialogue dialogue, int bonusAmount){
        this.encounterType = encounterType;
        this.enemy = enemy;
        this.dialogue = dialogue;
        this.bonusAmount = bonusAmount;
    }
    public EncounterType getEncounterType(){
        return encounterType;
    }
    public Enemy getEnemy(){
        return enemy;
    }
    public Dialogue getDialogue(){
        return dialogue;
    }
    public int getbonusAmount(){
        return bonusAmount;
    }
}
