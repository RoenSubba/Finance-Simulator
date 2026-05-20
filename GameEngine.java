import java.util.Random;

public class GameEngine {
    private static Random rand = new Random();

    public static void applyCompoundInterest(Person p) {
        double interest = p.getCash() * 0.04;
        p.addCash(interest);
    }

    public static void simulateMarketYear(Person p) {
        if (p.getInvestments() > 0) {
            double marketReturn = (rand.nextGaussian() * 0.15) + 0.08; 
            double profitOrLoss = p.getInvestments() * marketReturn;
            p.addInvestments(profitOrLoss);
        }
    }

    public static void simulateOneYear(Person p) {
        if (p.getAge() >= 65) return; // Prevent aging past 65!

        applyAnnualCashFlow(p);
        applyCompoundInterest(p);
        simulateMarketYear(p);
        p.addAge(1);
    }

    public static void simulateTenYears(Person p) {
        for (int i = 0; i < 10; i++) {
            if (p.getAge() >= 65) break; // Stop loop if they hit 65
            simulateOneYear(p);
        }
    }

    public static double getAnnualCashFlow(Person p) {
        double flow = 0;
        if (p.hasPartTimeJob()) flow += 12000;
        if (p.hasCareer()) flow += 65000;
        if (p.ownsHouse()) flow -= 18000;
        if (p.hasKids()) flow -= 15000;
        return flow;
    }

    private static void applyAnnualCashFlow(Person p) {
        p.addCash(getAnnualCashFlow(p));
        if (p.hasPartTimeJob()) p.modifyHappiness(-5);
        if (p.hasCareer()) p.modifyHappiness(-10);
    }

    public static void takeInstantAction(Person p, String action) {
        switch (action) {
            // --- CAREER & LIFE MILESTONES ---
            case "Get Part-Time Job":
                p.setPartTimeJob(true);
                p.addLifeEvent("Hired for a part-time job.");
                break;
            case "Start Career":
                p.setPartTimeJob(false);
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
                p.addCash(-20000);
                p.modifyHappiness(15);
                p.addLifeEvent("Paid off a chunk of student loans.");
                break;
            case "Buy House":
                p.setOwnsHouse(true);
                p.addCash(-50000);
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
                p.addLifeEvent("Bought a boat. The two best days of your life...");
                break;
        }
    }
}
