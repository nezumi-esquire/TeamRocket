import java.util.ArrayList;
import java.util.List;
import java.util.Map; 
public class Player {
    private String name;
    private int health;
    private int strength;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int experience; 
    private int level; 
    private int experienceToNextLevel;
    private List<Item> inventory = new ArrayList<>();
    private Item equippedWeapon;
    private Item equippedArmor;  
    public Player(Map<String, Object> playerStats){
        this.health = (int) playerStats.get("health");
        this.maxHealth = (int) playerStats.get("maxHealth");
        this.mana = (int) playerStats.get("mana");
        this.maxMana = (int) playerStats.get("maxMana");
        this.strength = (int) playerStats.get("strength");
        this.experience = (int) playerStats.getOrDefault("experience", 0); 
        this.level = (int) playerStats.getOrDefault("level", 1); 
        this.experienceToNextLevel = calculateExperienceToNextLevel();
    }
    public List<Item> getInventory() {
        return inventory;
    }
    public void addItemToInventory(Item item) {
        inventory.add(item);
    }
    public void gainExperience(int amount) {
        experience += amount;
        while (experience >= experienceToNextLevel) {
            levelUp();
        }
    }
    private void levelUp() {
        level++;
        experience -= experienceToNextLevel;
        experienceToNextLevel = calculateExperienceToNextLevel(); 
 
        strength += 1; 
 
        maxHealth += 3;
        health = maxHealth;  
  
        System.out.println("Congratulations! You reached level " + level + "!");
    }
 
    private int calculateExperienceToNextLevel() {
        return 100 * level; 
    }
    public boolean isAlive() {
        return health > 0;
    } 
    public int getHealth() {
        return health;
    }
    public int getStrength() {
        return strength;
    } 
    public int getMana() {
        return mana;
    }
    public int getMaxMana() {
        return maxMana;
    }
    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    } 
 
    public int getExperience() {
        return experience;
    } 
 
    public int getLevel() {
        return level;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    public void setStrength(int strength) {
        this.strength = strength;
    } 
    public void setMana(int mana) { this.mana = mana; }
    public void setMaxMana(int maxMana) { this.maxMana = maxMana; }
    public void takeDamage(int damage) {
    health -= damage; 
    if (health < 0) { 
        health = 0;
        }
    }
     public void heal(int amount) {
        health += amount;
        if (health > maxHealth) { 
            health = maxHealth;
        }
    }
    public boolean isItemEquipped(Item item) {
        return item.equals(equippedWeapon) || item.equals(equippedArmor);
    } 
    public void equipItem(Item item) {
        if (item.getType().equalsIgnoreCase("Weapon")) {
            equippedWeapon = item; 
            setStrength(getStrength() + item.getAtk());
        } else if (item.getType().equalsIgnoreCase("Armor")) {
            equippedArmor = item; 
            heal(item.getHp());
        }
    } 
    public void unequipItem(Item item) {
        if (item.equals(equippedWeapon)) {
            equippedWeapon = null; 
            setStrength(getStrength() - item.getAtk());
        } else if (item.equals(equippedArmor)) {
            equippedArmor = null; 
            setHealth(getHealth() - item.getHp()); 
        }
    }
    
}
