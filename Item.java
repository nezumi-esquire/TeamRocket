import javax.swing.*; 
public class Item {
    private String name;
    private int atk;
    private int hp;
    private int mp;
    private ImageIcon icon;
    private String description;
    private String type; 
    public Item(String name, int atk, int hp, int mp, ImageIcon icon, String description, String type) {
        this.name = name;
        this.atk = atk;
        this.hp = hp;
        this.mp = mp;
        this.icon = icon;
        this.description = description;
        this.type = type;
    } 
    public ImageIcon getIcon() {
        return icon;
    }
    public String getName(){
        return name;
    } 
    public int getAtk(){
        return atk;
    } 
    public int getHp(){
        return hp;
    } 
    public int getMp(){
        return mp;
    } 
    public String getDescription(){
        return description;
    } 
    public String getType() {
        return type;
    }
}