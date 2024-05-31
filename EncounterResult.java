import java.util.Map; 
public class EncounterResult {
    private EncounterType encounterType;
    private Map<String, Object> enemyData;
    private Dialogue dialogue;
    private int bonusAmount;
    
    public EncounterResult(EncounterType encounterType, Map<String, Object> enemyData, Dialogue dialogue, int bonusAmount){
        this.encounterType = encounterType;
        this.enemyData = enemyData;
        this.dialogue = dialogue;
        this.bonusAmount = bonusAmount;
    }
    public EncounterType getEncounterType(){
        return encounterType;
    }
    public Map<String, Object> getEnemyData(){
        return enemyData;
    }
    public Dialogue getDialogue(){
        return dialogue;
    }
    public int getbonusAmount(){
        return bonusAmount;
    }
}
