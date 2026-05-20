import java.util.Random;

public class GameEngine {
    private static Random rand = new Random();

    // 1. COMPOUND INTEREST (Safe Savings)
    public static void applyCompoundInterest(Person p) {
        // 4% annual yield on whatever cash they have
        double interest = p.getCash() * 0.04;
        p.addCash(interest);
    }

    // 2. REALISTIC STOCK MARKET (Investments)
    public static void simulateMarketYear(Person p) {
        if (p.getInvestments() > 0) {
            // Mean return of 8% (0.08), with a volatility/standard deviation of 15% (0.15)
            double marketReturn = (rand.nextGaussian() * 0.15) + 0.08; 
            
            double profitOrLoss = p.getInvestments() * marketReturn;
            p.addInvestments(profitOrLoss);
        }
    }

    // 3. THE TIME MACHINE (1-Year Loop)
    public static void simulateOneYear(Person p, String lifeAction) {
        // A. Apply the action the user chose
        processAction(p, lifeAction);

        // B. Apply Economy
        applyCompoundInterest(p);
        simulateMarketYear(p);

        // C. Age them up
        p.addAge(1);
    }

    // 4. THE TIME MACHINE (10-Year Loop)
    public static void simulateTenYears(Person p, String lifeAction) {
        for (int i = 0; i < 10; i++) {
            simulateOneYear(p, lifeAction);
        }
    }

    // 5. ACTION PROCESSOR & OPPORTUNITY COST
    private static void processAction(Person p, String action) {
        switch (action) {
            case "Part-Time Job":
                p.addCash(5000);          // Teenager income
                p.modifyHappiness(-5);    // Opportunity cost: less free time
                p.addLifeEvent("Worked a part-time job.");
                break;
            case "Save":
                // They just let compound interest do its thing safely
                p.modifyHappiness(2);     // Peace of mind
                break;
            case "Start Career":
                p.addCash(60000);         // Adult salary
                p.modifyHappiness(-10);   // Adulting is stressful
                p.addLifeEvent("Worked a full-time career.");
                break;
            case "Pay Student Loans":
                p.addCash(-10000);        // Paying down debt
                p.modifyHappiness(10);    // Relief of losing debt
                p.addLifeEvent("Paid down student loans.");
                break;
            case "Invest 50%":
                double investAmount = p.getCash() * 0.50;
                p.addCash(-investAmount);
                p.addInvestments(investAmount);
                p.addLifeEvent("Moved 50% of cash into the volatile stock market.");
                break;
            case "Buy House":
                p.addCash(-40000);        // Massive down payment
                p.modifyHappiness(20);    // Huge life milestone
                p.addLifeEvent("Bought a house!");
                break;
            case "Have Kids":
                p.addCash(-15000);        // Kids are very expensive
                p.modifyHappiness(30);    // But emotionally rewarding
                p.addLifeEvent("Had a child. Expenses went up!");
                break;
            case "Max Retirement":
                double retirement = p.getCash() * 0.80; // Shovel everything into investments
                p.addCash(-retirement);
                p.addInvestments(retirement);
                p.addLifeEvent("Maxed out retirement accounts.");
                break;
        }
    }
}
