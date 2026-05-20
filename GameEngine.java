import java.util.Random;

public class GameEngine {
    private static Random rand = new Random();

    // 1. COMPOUND INTEREST (Safe Savings)
    public static void applyCompoundInterest(Person p) {
        double interest = p.getCash() * 0.04;
        p.addCash(interest);
    }

    // 2. REALISTIC STOCK MARKET (Investments)
    public static void simulateMarketYear(Person p) {
        if (p.getInvestments() > 0) {
            double marketReturn = (rand.nextGaussian() * 0.15) + 0.08; 
            double profitOrLoss = p.getInvestments() * marketReturn;
            p.addInvestments(profitOrLoss);
        }
    }

    // 3. THE TIME MACHINE (1-Year Loop - NO ACTION REQUIRED)
    public static void simulateOneYear(Person p) {
        // A. Apply recurring income and expenses
        applyAnnualCashFlow(p);

        // B. Apply Market Economy
        applyCompoundInterest(p);
        simulateMarketYear(p);

        // C. Age them up
        p.addAge(1);
    }

    // 4. THE TIME MACHINE (10-Year Loop)
    public static void simulateTenYears(Person p) {
        for (int i = 0; i < 10; i++) {
            simulateOneYear(p);
        }
    }

    // 5. HELPER: CALCULATE NET CASH FLOW FOR THE GUI
    public static double getAnnualCashFlow(Person p) {
        double flow = 0;
        if (p.hasPartTimeJob()) flow += 12000;
        if (p.hasCareer()) flow += 65000;
        if (p.ownsHouse()) flow -= 18000;
        if (p.hasKids()) flow -= 15000;
        return flow;
    }

    // 6. APPLY THE CASH FLOW
    private static void applyAnnualCashFlow(Person p) {
        p.addCash(getAnnualCashFlow(p));
        
        // Apply recurring opportunity costs
        if (p.hasPartTimeJob()) p.modifyHappiness(-5);
        if (p.hasCareer()) p.modifyHappiness(-10);
    }

    // 7. INSTANT ACTIONS (What happens EXACTLY when a button is clicked)
    public static void takeInstantAction(Person p, String action) {
        switch (action) {
            case "Get Part-Time Job":
                p.setPartTimeJob(true);
                p.addLifeEvent("Hired for a part-time job.");
                break;
            case "Start Career":
                p.setPartTimeJob(false); // Quit the teen job
                p.setCareer(true);
                p.addLifeEvent("Started a professional career.");
                break;
            case "Invest 50%":
                double investAmount = p.getCash() * 0.50;
                p.addCash(-investAmount);
                p.addInvestments(investAmount);
                p.addLifeEvent("Moved 50% of cash into investments.");
                break;
            case "Pay Student Loans":
                p.addCash(-20000); // One time lump-sum payment
                p.modifyHappiness(15);
                p.addLifeEvent("Paid off a chunk of student loans.");
                break;
            case "Buy House":
                p.setOwnsHouse(true);
                p.addCash(-50000); // Down Payment
                p.modifyHappiness(20);
                p.addLifeEvent("Bought a house! Down payment made.");
                break;
            case "Have Kids":
                p.setHasKids(true);
                p.modifyHappiness(30);
                p.addLifeEvent("Had a child!");
                break;
            case "Max Retirement":
                double retirement = p.getCash() * 0.80;
                p.addCash(-retirement);
                p.addInvestments(retirement);
                p.addLifeEvent("Maxed out retirement accounts.");
                break;
        }
    }
}
