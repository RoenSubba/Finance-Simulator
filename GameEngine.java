import java.util.Random;

public class GameEngine {
    private static Random rand = new Random();

    public static void applyCompoundInterest(Person p) {
        double interest = p.getCash() * 0.05; 
        p.addCash(interest);
    }

    public static void simulateMarketYear(Person p) {
        if (p.getInvestments() > 0) {
            double marketReturn = (rand.nextGaussian() * 0.15) + 0.09; 
            double profitOrLoss = p.getInvestments() * marketReturn;
            p.addInvestments(profitOrLoss);
        }
    }

    public static String simulateOneYear(Person p) {
        if (p.getAge() >= 65) return null;

        double oldInvest = p.getInvestments();
        boolean oldCareer = p.hasCareer();

        p.addCash(getAnnualCashFlow(p));
        if (p.hasPartTimeJob()) p.modifyHappiness(-5);
        if (p.hasCareer())      p.modifyHappiness(-10); // Corporate stress

        applyCompoundInterest(p);
        simulateMarketYear(p);
        p.addAge(1);

        if (oldCareer && !p.hasCareer()) {
            return "EMERGENCY: Corporate downsizing! You lost your career. Income dropped to $0.";
        }

        if (p.getAge() >= 22) {
            double eventRoll = rand.nextDouble();
            if (eventRoll < 0.15) {
                int eventType = rand.nextInt(4);
                switch (eventType) {
                    case 0:
                        p.addCash(-3000);
                        p.modifyHappiness(-15);
                        return "EMERGENCY: Medical bill copay after an unexpected injury! (-$3,000)";
                    case 1:
                        p.addCash(-1500);
                        p.modifyHappiness(-10);
                        return "EMERGENCY: Car transmission failed. Mechanics are expensive! (-$1,500)";
                    case 2:
                        if (p.hasCareer()) {
                            p.setCareerTier(0); // FIRED!
                            p.modifyHappiness(-30);
                            return "EMERGENCY: Corporate downsizing. You were laid off! Look for a new job immediately!";
                        }
                        break;
                    case 3:
                        if (oldInvest > 10000) {
                            double crash = oldInvest * 0.30;
                            p.addInvestments(-crash);
                            p.modifyHappiness(-20);
                            return "EMERGENCY: Stock market correction! Your investment portfolio dropped 30%.";
                        }
                        break;
                }
            } else if (eventRoll > 0.85) {
                int windfallType = rand.nextInt(3);
                switch (windfallType) {
                    case 0:
                        if (p.hasCareer()) {
                            p.addCash(5000);
                            p.modifyHappiness(20);
                            return "WINDFALL: Excellent performance! Your boss handed you a $5,000 holiday bonus.";
                        }
                        break;
                    case 1:
                        p.addCash(2000);
                        p.modifyHappiness(15);
                        return "WINDFALL: Tax season victory! You received a $2,000 government tax refund check.";
                    case 2:
                        if (p.getInvestments() > 5000) {
                            double surge = p.getInvestments() * 0.25;
                            p.addInvestments(surge);
                            p.modifyHappiness(25);
                            return "WINDFALL: Market Rally! One of your index holdings exploded. Portfolio value surged +25%!";
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
            if (result != null) latestNotification = result; 
        }
        return latestNotification;
    }

    public static double getAnnualCashFlow(Person p) {
        double flow = 0;
        
        // --- NEW RPG SALARY TIERS ---
        if (p.getCareerTier() == 1) flow += 15000; // Part-Time
        else if (p.getCareerTier() == 2) flow += 50000; // Entry Level
        else if (p.getCareerTier() == 3) flow += 85000; // Mid-Level
        else if (p.getCareerTier() == 4) flow += 140000; // Executive

        if (p.getAge() >= 22) flow -= 25000; // Adult living costs
        else if (p.getAge() >= 18) flow -= 8000; // College living costs
        
        if (p.ownsHouse()) flow -= 24000; 
        if (p.hasKids())   flow -= 18000; 

        return flow;
    }

    public static boolean takeInstantAction(Person p, String action) {
        switch (action) {
            case "Get Part-Time Job":
                p.setCareerTier(1);
                return true;
            case "Buy Beater Car":
                if (p.getCash() < 3000) return false;
                p.addCash(-3000);
                p.modifyHappiness(20);
                return true;
            case "Invest $1,000":
                if (p.getCash() < 1000) return false;
                p.addCash(-1000);
                p.addInvestments(1000);
                return true;
            case "Take Student Loans":
                p.addCash(20000); 
                p.addInvestments(-25000); 
                return true;
            case "Paid Internship":
                p.addCash(5000);
                p.modifyHappiness(-5);
                return true;
            case "Seek Promotion":
                if (p.getCareerTier() >= 4) return false;
                if (rand.nextDouble() > 0.4) { // 60% chance of promotion
                    p.setCareerTier(p.getCareerTier() + 1);
                    p.modifyHappiness(-10); // Stressful
                    return true;
                } else {
                    p.modifyHappiness(-15);
                    return false;
                }
            case "Invest 50%":
                if (p.getCash() <= 0) return false;
                double investAmount = p.getCash() * 0.50;
                p.addCash(-investAmount);
                p.addInvestments(investAmount);
                return true;
            case "Buy House":
                if (p.getCash() < 50000) return false;
                p.setOwnsHouse(true);
                p.addCash(-50000); 
                p.modifyHappiness(20);
                return true;
            case "Have Kids":
                p.setHasKids(true);
                p.modifyHappiness(30);
                return true;
            case "Max Retirement":
                if (p.getCash() <= 0) return false;
                double retirement = p.getCash() * 0.80;
                p.addCash(-retirement);
                p.addInvestments(retirement);
                return true;
            case "Buy Boat":
                if (p.getCash() < 40000) return false;
                p.addCash(-40000);
                p.modifyHappiness(30);
                return true;
        }
        return false;
    }
}