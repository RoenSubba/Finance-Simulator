import java.util.ArrayList;
import java.util.List;

public class Person {
    private String name;
    private int age = 16;
    private double cash = 1000; // Start with some high school pocket money
    private double investments = 0;
    private int happiness = 50;
    
    // --- NEW CAREER SYSTEM ---
    private int careerTier = 0; // 0: Unemployed, 1: Part-Time, 2: Entry, 3: Mid, 4: Executive
    private String careerPath = "Undeclared";
    
    private boolean ownsHouse = false;
    private boolean hasKids = false;
    private List<String> history = new ArrayList<>();

    public Person(String name) { this.name = name; }

    public String getName() { return name; }
    public int getAge() { return age; }
    public void addAge(int a) { this.age += a; }
    
    public double getCash() { return cash; }
    public void addCash(double amount) { this.cash += amount; }
    
    public double getInvestments() { return investments; }
    public void addInvestments(double amount) { this.investments += amount; }
    
    public double getNetWorth() { return cash + investments; }
    
    public int getHappiness() { return happiness; }
    public void modifyHappiness(int amount) { 
        this.happiness += amount; 
        if (this.happiness > 100) this.happiness = 100;
        if (this.happiness < 0) this.happiness = 0;
    }

    // Bridging old logic to the new RPG Tier system
    public boolean hasPartTimeJob() { return careerTier == 1; }
    public void setPartTimeJob(boolean b) { if(b) careerTier = 1; else careerTier = 0; }
    
    public boolean hasCareer() { return careerTier >= 2; }
    public void setCareer(boolean b) { if(b) careerTier = 2; else careerTier = 0; }
    
    public int getCareerTier() { return careerTier; }
    public void setCareerTier(int tier) { this.careerTier = tier; }
    
    public String getCareerPath() { return careerPath; }
    public void setCareerPath(String path) { this.careerPath = path; }

    public boolean ownsHouse() { return ownsHouse; }
    public void setOwnsHouse(boolean b) { this.ownsHouse = b; }
    
    public boolean hasKids() { return hasKids; }
    public void setHasKids(boolean b) { this.hasKids = b; }

    public void addLifeEvent(String event) { history.add("Age " + age + ": " + event); }
    public void printHistory() {
        System.out.println("--- Life History for " + name + " ---");
        for(String s : history) System.out.println(s);
    }
}