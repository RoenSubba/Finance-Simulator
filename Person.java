import java.util.ArrayList;

public class Person {
    private String name;
    private int age;
    private double cash;
    private double investments;
    private int happiness;
    private ArrayList<String> lifeHistory;

    // --- LIFE STATUS FLAGS ---
    private boolean hasPartTimeJob;
    private boolean hasCareer;
    private boolean ownsHouse;
    private boolean hasKids;

    public Person(String name) {
        this.name = name;
        this.age = 16;
        this.cash = 1000.0;
        this.investments = 0.0;
        this.happiness = 100;
        this.lifeHistory = new ArrayList<>();
        
        this.hasPartTimeJob = false;
        this.hasCareer = false;
        this.ownsHouse = false;
        this.hasKids = false;
    }

    // --- GETTERS ---
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getCash() { return cash; }
    public double getInvestments() { return investments; }
    public double getNetWorth() { return cash + investments; }
    public int getHappiness() { return happiness; }
    
    public boolean hasPartTimeJob() { return hasPartTimeJob; }
    public boolean hasCareer() { return hasCareer; }
    public boolean ownsHouse() { return ownsHouse; }
    public boolean hasKids() { return hasKids; }

    // --- SETTERS / MODIFIERS ---
    public void setPartTimeJob(boolean b) { this.hasPartTimeJob = b; }
    public void setCareer(boolean b) { this.hasCareer = b; }
    public void setOwnsHouse(boolean b) { this.ownsHouse = b; }
    public void setHasKids(boolean b) { this.hasKids = b; }

    public void addAge(int years) { this.age += years; }
    public void addCash(double amount) { this.cash += amount; }
    public void addInvestments(double amount) { this.investments += amount; }
    
    public void modifyHappiness(int amount) {
        this.happiness += amount;
        if (this.happiness > 100) this.happiness = 100;
        if (this.happiness < 0) this.happiness = 0;
    }

    // --- HISTORY METHODS ---
    public void addLifeEvent(String event) {
        lifeHistory.add("Age " + age + ": " + event);
    }

    public void printHistory() {
        System.out.println("--- " + name + "'s Life History ---");
        for (String event : lifeHistory) {
            System.out.println(event);
        }
    }
}