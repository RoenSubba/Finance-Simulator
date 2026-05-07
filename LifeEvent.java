public class LifeEvent {
    // Instance variables
    private String eventName;
    private double financialImpact; // Negative means it costs money, positive means you gain money

    // Constructor
    public LifeEvent(String name, double impact) {
        this.eventName = name;
        this.financialImpact = impact;
    }

    // Getters
    public String getEventName() {
        return eventName;
    }

    public double getFinancialImpact() {
        return financialImpact;
    }

    // Static method to generate a random event
    // Static means we can call it without creating a blank LifeEvent first!
    public static LifeEvent generateRandomEvent() {
        // Generates a random integer from 0 to 4
        int randomNum = (int) (Math.random() * 5); 

        if (randomNum == 0) {
            return new LifeEvent("Unexpected Car Repair", -500.0);
        } else if (randomNum == 1) {
            return new LifeEvent("Surprise Work Bonus", 1200.0);
        } else if (randomNum == 2) {
            return new LifeEvent("Medical Bill", -800.0);
        } else if (randomNum == 3) {
            return new LifeEvent("Found a $100 bill on the ground", 100.0);
        } else {
            return new LifeEvent("Quiet year, no major events.", 0.0);
        }
    }
}