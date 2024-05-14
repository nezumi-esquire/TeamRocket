public class Player {
    private String name;
    private int health;
    private int strength;
    private int maxHealth;
    private int mana;
    private int maxMana;
    
    public Player(String name) {
        this.name = name;
        this.health = 100;
        this.strength = 10;
        this.maxHealth = 100;
        this.mana = 100;
        this.maxMana = 100;
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
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
    
}
