import  java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        System.out.println("====================================");
        System.out.println("  WELCOME TO LIFEFInance SIMULATOR  ");
        System.out.println("====================================\n");
        
        // 1. Get User Input
        System.out.print("Enter your character's name: ");
        String playerName = input.nextLine();
        
        // Create the player and a standard budget
        Person player = new Person(playerName, 22, 50000.0);
        Budget playerBudget = new Budget(1200.0, 400.0, 300.0, 200.0);
        
        System.out.println("\nStarting simulation for " + playerName + "...\n");
        
        // 2. The 10-Year Game Loop (AP CSA Goldmine!)
        for (int year = 1; year <= 10; year++) {
            System.out.println("=== YEAR " + year + " (Age " + (21 + year) + ") ===");
            
            player.addAge();
            
            double yearlySavings = playerBudget.calculateAnnualSavings(50000.0);
            
            LifeEvent thisYearsEvent = LifeEvent.generateRandomEvent();
            System.out.println("Event: " + thisYearsEvent.getEventName() + " (Impact: $" + thisYearsEvent.getFinancialImpact() + ")");
            
            double netSavingsThisYear = yearlySavings + thisYearsEvent.getFinancialImpact();
            player.updateSavings(netSavingsThisYear);
            
            System.out.println("You saved $" + netSavingsThisYear + " this year.");
            player.printFinancialSummary();
            
            if (year < 10) {
                System.out.print("Press [ENTER] to advance to the next year...");
                input.nextLine(); 
                System.out.println("\n");
            }
        }
        
        System.out.println("🎉 10-YEAR SIMULATION COMPLETE! 🎉");
        input.close();
    }
}