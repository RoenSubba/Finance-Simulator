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

    // 3. THE TIME MACHINE (1-Year Loop) - Now returns emergency message strings
    public static String simulateOneYear(Person p) {
        if (p.getAge() >= 65) return null;
        double oldInvest = p.getInvestments();
        boolean oldCareer = p.hasCareer();

        // Apply core cash flow loop
        p.addCash(getAnnualCashFlow(p));
        
        if (p.hasPartTimeJob()) p.modifyHappiness(-5);
        if (p.hasCareer())      p.modifyHappiness(-10);

        // Process interest and stock returns
        applyCompoundInterest(p);
        simulateMarketYear(p);
        p.addAge(1);

        // RANDOM LIFE EMERGENCIES (15% chance per year once they hit adulthood)
        if (rand.nextDouble() < 0.15 && p.getAge() >= 22) {
            int eventType = rand.nextInt(4);
            switch (eventType) {
                case 0:
                    p.addCash(-3000);
                    p.modifyHappiness(-15);
                    p.addLifeEvent("EMERGENCY: Medical bill copay after an unexpected injury! (-$3,000)");
                    return "EMERGENCY: Medical bill copay after an unexpected injury! (-$3,000)";
                case 1:
                    p.addCash(-1500);
                    p.modifyHappiness(-10);
                    p.addLifeEvent("EMERGENCY: Car transmission failed. Mechanics are expensive! (-$1,500)");
                    return "EMERGENCY: Car transmission failed. Mechanics are expensive! (-$1,500)";
                case 2:
                    if (oldCareer) {
                        p.setCareer(false); // YOU GOT LAID OFF
                        p.modifyHappiness(-30);
                        p.addLifeEvent("EMERGENCY: Corporate downsizing. You lost your career! Income dropped to $0.");
                        return "EMERGENCY: Corporate downsizing. You lost your career! Income dropped to $0.";
                    }
                    break;
                case 3:
                    if (oldInvest > 10000) {
                        double crash = oldInvest * 0.30;
                        p.addInvestments(-crash);
                        p.modifyHappiness(-20);
                        p.addLifeEvent("EMERGENCY: Stock market correction! Your investment portfolio dropped 30%.");
                        return "EMERGENCY: Stock market correction! Your investment portfolio dropped 30%.";
                    }
                    break;
            }
        }

        return null; // Normal year, no emergency
    }

    // 4. THE TIME MACHINE (10-Year Loop) - Returns the latest emergency encountered, if any
    public static String simulateTenYears(Person p) {
        String latestEmergency = null;
        for (int i = 0; i < 10; i++) {
            if (p.getAge() >= 65) break;
            String result = simulateOneYear(p);
            if (result != null) {
                latestEmergency = result; // Remembers the disaster to report to the GUI
            }
        }
        return latestEmergency;
    }

    // 5. HELPER: CALCULATE NET CASH FLOW
    public static double getAnnualCashFlow(Person p) {
        double flow = 0;
        
        if (p.hasPartTimeJob()) flow += 10000; 
        if (p.hasCareer())      flow += 48000; 

        if (p.getAge() >= 22) {
            flow -= 15000; // Mandatory living cost
        } else {
            flow -= 2000;  
        }

        if (p.ownsHouse()) flow -= 18000; 
        if (p.hasKids())   flow -= 15000; 

        return flow;
    }

    // 6. INSTANT ACTIONS 
    public static void takeInstantAction(Person p, String action) {
        switch (action) {
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
