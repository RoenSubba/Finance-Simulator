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
        
        // Handle Schooling progression
        if (p.isEnrolled()) {
            p.addYearInDegree();
            p.modifyHappiness(-5); // School is stressful
            // Check for graduation!
            if (p.getYearsInDegree() >= 4) {
                p.setEnrolled(false);
                p.resetYearsInDegree();
                p.setEducationLevel(p.getEducationLevel() + 1);
                p.modifyHappiness(20);
                return "WINDFALL: You graduated! Education leveled up. (+20 Happiness)";
            }
        }

        if (p.hasCareer()) p.modifyHappiness(-10);

        applyCompoundInterest(p);
        simulateMarketYear(p);
        p.addAge(1);

        if (oldCareer && !p.hasCareer()) {
            return "EMERGENCY: Corporate downsizing! You lost your career.";
        }

        if (p.getAge() >= 22) {
            double eventRoll = rand.nextDouble();
            if (eventRoll < 0.15) {
                int eventType = rand.nextInt(4);
                switch (eventType) {
                    case 0:
                        p.addCash(-3000);
                        p.modifyHappiness(-15);
                        return "EMERGENCY: Medical bill copay after an injury! (-$3,000)";
                    case 1:
                        if (p.getCarTier() > 0) {
                            p.addCash(-1500);
                            p.modifyHappiness(-10);
                            return "EMERGENCY: Car transmission failed. Mechanics are expensive! (-$1,500)";
                        }
                        break;
                    case 2:
                        if (p.hasCareer()) {
                            p.setCareerTier(0); 
                            p.modifyHappiness(-30);
                            return "EMERGENCY: Corporate downsizing. You were laid off!";
                        }
                        break;
                    case 3:
                        if (oldInvest > 10000) {
                            double crash = oldInvest * 0.30;
                            p.addInvestments(-crash);
                            p.modifyHappiness(-20);
                            return "EMERGENCY: Stock market correction! Portfolio dropped 30%.";
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
                            return "WINDFALL: Excellent performance! $5,000 holiday bonus.";
                        }
                        break;
                    case 1:
                        p.addCash(2000);
                        p.modifyHappiness(15);
                        return "WINDFALL: Tax season victory! $2,000 IRS refund.";
                    case 2:
                        if (p.getInvestments() > 5000) {
                            double surge = p.getInvestments() * 0.25;
                            p.addInvestments(surge);
                            p.modifyHappiness(25);
                            return "WINDFALL: Market Rally! Portfolio value surged +25%!";
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
        
        double uniMultiplier = 1.0;
        if (p.getUniversityTier().equals("Private")) uniMultiplier = 1.15;
        if (p.getUniversityTier().equals("Elite")) uniMultiplier = 1.35;

        if (p.getCareerTier() == 1) flow += 15000; 
        else if (p.getCareerTier() == 2) flow += (50000 * uniMultiplier); 
        else if (p.getCareerTier() == 3) flow += (85000 * uniMultiplier); 
        else if (p.getCareerTier() == 4) flow += (140000 * uniMultiplier); 

        // Tuition Costs
        if (p.isEnrolled()) {
            if (p.getUniversityTier().equals("Community")) flow -= 5000;
            else if (p.getUniversityTier().equals("State")) flow -= 15000;
            else if (p.getUniversityTier().equals("Private")) flow -= 45000;
            else if (p.getUniversityTier().equals("Elite")) flow -= 60000;
        }

        if (p.getAge() >= 22 && !p.isEnrolled()) flow -= 25000; 
        
        // Asset Upkeep Costs
        if (p.getHouseTier() == 1) flow -= 12000;
        else if (p.getHouseTier() == 2) flow -= 24000;
        else if (p.getHouseTier() == 3) flow -= 60000;

        if (p.hasKids()) flow -= 18000; 

        return flow;
    }

    public static boolean takeInstantAction(Person p, String action) {
        switch (action) {
            case "Study Hard":
                p.modifyGrades(10);
                p.modifyHappiness(-10);
                return true;
            case "Party":
                p.modifyGrades(-10);
                p.modifyHappiness(15);
                return true;
            case "Drop Out":
                p.setEnrolled(false);
                p.setEducationLevel(1); // Drop out status
                p.resetYearsInDegree();
                return true;
            case "Take Student Loans":
                p.addCash(20000); 
                p.addInvestments(-25000); 
                return true;
            case "Get Part-Time Job":
                p.setCareerTier(1);
                return true;
            case "Seek Promotion":
                if (p.getCareerTier() >= 4) return false;
                
                // EDUCATION GATEKEEPER
                if (p.getCareerTier() == 2 && p.getEducationLevel() < 2) return false; // Need Bachelors for Mid-Level
                if (p.getCareerTier() == 3 && p.getEducationLevel() < 3) return false; // Need Masters for Exec
                
                if (rand.nextDouble() > 0.4) { 
                    p.setCareerTier(p.getCareerTier() + 1);
                    p.modifyHappiness(-10); 
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
            case "Invest $1,000":
                if (p.getCash() < 1000) return false;
                p.addCash(-1000);
                p.addInvestments(1000);
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