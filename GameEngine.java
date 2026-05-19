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
            // This means most years are between -7% and +23%, mirroring real life.
            double marketReturn = (rand.nextGaussian() * 0.15) + 0.08; 
            
            double profitOrLoss = p.getInvestments() * marketReturn;
            p.addInvestments(profitOrLoss);
        }
    }

    // 3. THE TIME MACHINE (1-Year Loop)
    public static void simulateOneYear(Person p, String lifeAction) {
        // A. Apply the action the user chose (Age-Gated logic goes here)
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
                p.addCash(5000);
                p.modifyHappiness(-5); // Working takes away free time
                break;
            case "Start Career":
                p.addCash(50000);
                p.modifyHappiness(-10); // Adulting is stressful
                break;
            case "Invest 50%":
                double investAmount = p.getCash() * 0.50;
                p.addCash(-investAmount);
                p.addInvestments(investAmount);
                break;
            case "Vacation":
                p.addCash(-3000);
                p.modifyHappiness(20); // Opportunity cost: spent money, gained happiness
                break;
        }
    }
}
