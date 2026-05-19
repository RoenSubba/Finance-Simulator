public class Person {
    private String name;
    private int age;
    private double cash;          // Safe money (Savings account)
    private double investments;   // Risky money (Stock Market)
    private int happiness;        // Opportunity cost metric (0-100)

    public Person(String name) {
        this.name = name;
        this.age = 16;            // Start exactly at 16
        this.cash = 1000.0;       // Starting teen cash
        this.investments = 0.0;
        this.happiness = 100;     // Starts happy!
    }

    // --- GETTERS ---
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getCash() { return cash; }
    public double getInvestments() { return investments; }
    public double getNetWorth() { return cash + investments; }
    public int getHappiness() { return happiness; }

    // --- SETTERS / MODIFIERS ---
    public void addAge(int years) { this.age += years; }
    public void addCash(double amount) { this.cash += amount; }
    public void addInvestments(double amount) { this.investments += amount; }
    
    public void modifyHappiness(int amount) {
        this.happiness += amount;
        if (this.happiness > 100) this.happiness = 100;
        if (this.happiness < 0) this.happiness = 0;
    }
}