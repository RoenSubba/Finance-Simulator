import java.util.ArrayList;
import java.util.List;

public class Person {
    private String name;
    private int age = 16;
    private double cash = 1000; 
    private double investments = 0;
    private int happiness = 60;
    
    private int careerTier = 0; 
    private String careerPath = "Undeclared";
    
    private int educationLevel = 0; 
    private boolean isEnrolled = false;
    private int yearsInDegree = 0;
    private int grades = 75; 
    private String universityTier = "None";
    private int houseTier = 0; 
    private int carTier = 0;  
    
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

    public boolean hasPartTimeJob() { return careerTier == 1; }
    public void setPartTimeJob(boolean b) { if(b) careerTier = 1; else careerTier = 0; }
    public boolean hasCareer() { return careerTier >= 2; }
    public void setCareer(boolean b) { if(b) careerTier = 2; else careerTier = 0; }
    public int getCareerTier() { return careerTier; }
    public void setCareerTier(int tier) { this.careerTier = tier; }
    public String getCareerPath() { return careerPath; }
    public void setCareerPath(String path) { this.careerPath = path; }

    public int getEducationLevel() { return educationLevel; }
    public void setEducationLevel(int e) { this.educationLevel = e; }
    public boolean isEnrolled() { return isEnrolled; }
    public void setEnrolled(boolean b) { this.isEnrolled = b; }
    public int getYearsInDegree() { return yearsInDegree; }
    public void addYearInDegree() { this.yearsInDegree++; }
    public void resetYearsInDegree() { this.yearsInDegree = 0; }
    public int getGrades() { return grades; }
    public void modifyGrades(int amount) {
        this.grades += amount;
        if(this.grades > 100) this.grades = 100;
        if(this.grades < 0) this.grades = 0;
    }
    public String getUniversityTier() { return universityTier; }
    public void setUniversityTier(String t) { this.universityTier = t; }

    public int getHouseTier() { return houseTier; }
    public void setHouseTier(int tier) { this.houseTier = tier; }
    public int getCarTier() { return carTier; }
    public void setCarTier(int tier) { this.carTier = tier; }
    
    public boolean hasKids() { return hasKids; }
    public void setHasKids(boolean b) { this.hasKids = b; }

    public void addLifeEvent(String event) { history.add("Age " + age + ": " + event); }
    public void printHistory() {
        System.out.println("--- Life History for " + name + " ---");
        for(String s : history) System.out.println(s);
    }
}