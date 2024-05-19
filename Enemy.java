 

 
public class Enemy {
    private String name;
    private int health;
    private int strength;
    private int maxHealth;
    private int level;
    private CharacterAnimation animationSet;
    
    public Enemy(String name, int health, int strength, int level){
        this.name = name;
        this.health = health;
        this.strength = strength;
        this.maxHealth = maxHealth;
        this.level = level;
        this.animationSet = null;
    }
    public Enemy(String name, int health, int strength, int level, CharacterAnimation animationSet) {
        this(name, health, strength, level); // Call the existing constructor
        this.animationSet = animationSet;
    }
    public boolean isAlive() { 
        return health > 0;
    }
    
    public String getName(){
        return name;
    }
    
    public int getHealth(){
        return health;
    }
    public int getMaxHealth(){
        return maxHealth;
    }
    public int getStrength(){
        return strength;
    }
    public int getLevel(){return level;}
    public void setHealth(int health){
        this.health = health;
    }
    public void takeDamage(int damage) {
    health -= damage; 
    if (health < 0) { 
        health = 0;
        }
    }
    public CharacterAnimation getAnimationSet() {
        return animationSet;
    }
}

