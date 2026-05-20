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

    public static String simulateOneYear(Person p) {
        if (p.getAge() >= 65) return null;

        double oldInvest = p.getInvestments();
        boolean oldCareer = p.hasCareer();

        // Process annual cash flow
        p.addCash(getAnnualCashFlow(p));
        if (p.hasPartTimeJob()) p.modifyHappiness(-5);
        if (p.hasCareer())      p.modifyHappiness(-10);

        applyCompoundInterest(p);
        simulateMarketYear(p);
        p.addAge(1);

        // Check if the random layoff triggered during core flow mechanics
        if (oldCareer && !p.hasCareer()) {
            return "EMERGENCY: Corporate downsizing! You lost your career. Income dropped to $0.";
        }

        // --- RANDOM EVENT ENGINE (Adulthood) ---
        if (p.getAge() >= 22) {
            double eventRoll = rand.nextDouble();
            
            // 15% Chance of an Emergency (Negative)
            if (eventRoll < 0.15) {
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
                        if (p.hasCareer()) {
                            p.setCareer(false); // YOU GOT LAID OFF
                            p.modifyHappiness(-30);
                            p.addLifeEvent("EMERGENCY: Corporate downsizing. You lost your career!");
                            return "EMERGENCY: Corporate downsizing. You lost your career! Income dropped to $0. Look for a new job immediately!";
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
            // 15% Chance of a WINDFALL (Positive!)
            else if (eventRoll > 0.85) {
                int windfallType = rand.nextInt(3);
                switch (windfallType) {
                    case 0:
                        if (p.hasCareer()) {
                            p.addCash(5000);
                            p.modifyHappiness(20);
                            p.addLifeEvent("WINDFALL: Crushed your quarterly targets! Received a $5,000 job bonus.");
                            return "WINDFALL: Excellent performance! Your boss handed you a $5,000 holiday bonus. (+20 Happiness)";
                        }
                        break;
                    case 1:
                        p.addCash(2000);
                        p.modifyHappiness(15);
                        p.addLifeEvent("WINDFALL: Filed your taxes accurately. Received a $2,000 IRS refund.");
                        return "WINDFALL: Tax season victory! You received a $2,000 government tax refund check.";
                    case 2:
                        if (p.getInvestments() > 5000) {
                            double surge = p.getInvestments() * 0.25;
                            p.addInvestments(surge);
                            p.modifyHappiness(25);
                            p.addLifeEvent("WINDFALL: One of your stock assets went viral! Investments surged 25%.");
                            return "WINDFALL: Market Rally! One of your index holdings exploded. Portfolio value surged +25%! (+25 Happiness)";
                        }
                        break;
                }
            }
        }

        return null; 
    }

    public static String simulateTenYears(Person p) {
        String latestNotification = null;
        for (int i = 0; i < 10; i++) {
            if (p.getAge() >= 65) break;
            String result = simulateOneYear(p);
            if (result != null) {
                latestNotification = result; 
            }
        }
        return latestNotification;
    }

    public static double getAnnualCashFlow(Person p) {
        double flow = 0;
        if (p.hasPartTimeJob()) flow += 10000; 
        if (p.hasCareer())      flow += 48000; 

        if (p.getAge() >= 22) {
            flow -= 15000; 
        } else {
            flow -= 2000;  
        }

        if (p.ownsHouse()) flow -= 18000; 
        if (p.hasKids())   flow -= 15000; 

        return flow;
    }

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