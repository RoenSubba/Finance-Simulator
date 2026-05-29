public class LifeEvent {
    private String eventName;
    private double financialImpact; // Negative means it costs money, positive means you gain money

    
    public LifeEvent(String name, double impact) {
        this.eventName = name;
        this.financialImpact = impact;
    }

    public String getEventName() {
        return eventName;
    }

    public double getFinancialImpact() {
        return financialImpact;
    }

    
    public static LifeEvent generateRandomEvent() {
        
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