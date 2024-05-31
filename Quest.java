public class Quest {
    private String name;
    private String description;
    private boolean isCompleted;
    private boolean isDeclined; 
    public Quest(String name, String description) {
        this.name = name;
        this.description = description;
        this.isCompleted = false;
        this.isDeclined = false;
    } 
    public String getName() {
        return name;
    } 
    public String getDescription() {
        return description;
    } 
    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    } 
    public void setDeclined(boolean isDeclined) {
        this.isDeclined = isDeclined;
    }
    public boolean isDeclined() {
        return isDeclined;
    }
}
