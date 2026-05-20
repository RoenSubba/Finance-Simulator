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
            // Mean return of 8% (0.08), with a volatility/standard deviation of 15% (0.15)
            double marketReturn = (rand.nextGaussian() * 0.15) + 0.08; 
            double profitOrLoss = p.getInvestments() * marketReturn;
            p.addInvestments(profitOrLoss);
        }
    }

    // 3. THE TIME MACHINE (1-Year Loop)
    public static void simulateOneYear(Person p) {
        if (p.getAge() >= 65) return; // Prevent aging past 65!

        // A. Apply recurring income, taxes, living costs, and random events
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
            if (p.getAge() >= 65) break; // Stop loop if they hit 65
            simulateOneYear(p);
        }
    }

    // 5. HELPER: CALCULATE REALISTIC NET CASH FLOW
    public static double getAnnualCashFlow(Person p) {
        double flow = 0;
        
        // Income (Post-Tax Estimates)
        if (p.hasPartTimeJob()) flow += 10000; // Adjusted for minor withholding
        if (p.hasCareer())      flow += 48000; // $65k salary minus ~26% income tax and health insurance

        // Fixed Expenses (Mandatory Cost of Living)
        if (p.getAge() >= 22) {
            flow -= 15000; // Rent, food, utilities, basic insurance
        } else {
            flow -= 2000;  // Minor expenses while being a teenager
        }

        // Lifestyle Choices (Additional Expenses)
        if (p.ownsHouse()) flow -= 18000; // Additional Mortgage, Property Tax, Maintenance
        if (p.hasKids())   flow -= 15000; // Additional Childcare, Healthcare, Food

        return flow;
    }

    // 6. APPLY CASH FLOW & TIME-BASED COSTS
    private static void applyAnnualCashFlow(Person p) {
        p.addCash(getAnnualCashFlow(p));
        
        // Opportunity costs on happiness for working
        if (p.hasPartTimeJob()) p.modifyHappiness(-5);
        if (p.hasCareer())      p.modifyHappiness(-10);

        // RANDOM LIFE EMERGENCIES (15% chance per year once they hit adulthood)
        if (rand.nextDouble() < 0.15 && p.getAge() >= 22) {
            triggerRandomEmergency(p);
        }
    }

    // 7. NEW: FINANCIAL EMERGENCY ENGINE
    private static void triggerRandomEmergency(Person p) {
        int eventType = rand.nextInt(4);
        switch (eventType) {
            case 0:
                p.addCash(-3000);
                p.modifyHappiness(-15);
                p.addLifeEvent("EMERGENCY: Medical bill copay after an unexpected injury! (-$3,000)");
                break;
            case 1:
                p.addCash(-1500);
                p.modifyHappiness(-10);
                p.addLifeEvent("EMERGENCY: Car transmission failed. Mechanics are expensive! (-$1,500)");
                break;
            case 2:
                if (p.hasCareer()) {
                    p.setCareer(false); // YOU GOT LAID OFF
                    p.modifyHappiness(-30);
                    p.addLifeEvent("EMERGENCY: Corporate downsizing. You lost your career! Income dropped to $0.");
                }
                break;
            case 3:
                if (p.getInvestments() > 10000) {
                    double crash = p.getInvestments() * 0.30;
                    p.addInvestments(-crash);
                    p.modifyHappiness(-20);
                    p.addLifeEvent("EMERGENCY: Stock market correction! Your investment portfolio dropped 30%.");
                }
                break;
        }
    }

    // 8. INSTANT ACTIONS (What happens exactly when a button is clicked)
    public static void takeInstantAction(Person p, String action) {
        switch (action) {
            // --- CAREER & LIFE MILESTONES ---
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
                p.addCash(-20000); // One-time lump-sum payment
                p.modifyHappiness(15);
                p.addLifeEvent("Paid off a chunk of student loans.");
                break;
            case "Buy House":
                p.setOwnsHouse(true);
                p.addCash(-50000); // Upfront down payment
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

            // --- VANITY PURCHASES (LIFESTYLE CREEP) ---
            case "Buy PC":
                p.addCash(-2000);
                p.modifyHappiness(15);
                p.addLifeEvent("Bought a high-end gaming PC.");
                break;
            case "Buy Jewelry":
                p.addCash(-5000);
                p.modifyHappiness(20);
                p.addLifeEvent("Bought fancy jewelry.");
                break;
            case "Buy Sports Car":
                p.addCash(-60000);
                p.modifyHappiness(40);
                p.addLifeEvent("Bought a mid-life crisis sports car.");
                break;
            case "Buy Boat":
                p.addCash(-40000);
                p.modifyHappiness(30);
                p.addLifeEvent("Bought a boat.");
                break;
        }
    }
}
