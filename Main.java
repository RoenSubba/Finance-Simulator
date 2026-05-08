import java.util.Scanner;
import java.util.ArrayList;
public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        System.out.println("====================================");
        System.out.println("  WELCOME TO LIFEFInance SIMULATOR  ");
        System.out.println("====================================\n");
        
        System.out.print("Enter your character's name: ");
        String playerName = input.nextLine();
        
        Person player = new Person(playerName, 22, 50000.0);
        Budget playerBudget = new Budget(1200.0, 400.0, 300.0, 200.0);
        
        
        ArrayList<String> lifeHistory = new ArrayList<>();
        
        System.out.println("\nStarting simulation for " + playerName + "...\n");
        
        for (int year = 1; year <= 10; year++) {
            System.out.println("=== YEAR " + year + " (Age " + (21 + year) + ") ===");
            
            player.addAge();
            double yearlySavings = playerBudget.calculateAnnualSavings(50000.0);
            
            LifeEvent thisYearsEvent = LifeEvent.generateRandomEvent();
            System.out.println("Event: " + thisYearsEvent.getEventName() + " (Impact: $" + thisYearsEvent.getFinancialImpact() + ")");
            
            
            lifeHistory.add("Year " + year + ": " + thisYearsEvent.getEventName());
            
            double netSavingsThisYear = yearlySavings + thisYearsEvent.getFinancialImpact();
            
            
            System.out.println("\nYou have $" + netSavingsThisYear + " left over this year.");
            System.out.println("What do you want to do with it?");
            System.out.println("1. Put it in a Savings Account");
            System.out.println("2. Invest it in the Stock Market");
            System.out.print("Enter choice (1 or 2): ");
            
            int choice = input.nextInt();
            input.nextLine(); 
            
            if (choice == 1) {
                System.out.println("-> You kept your money safe in the bank.");
                player.updateSavings(netSavingsThisYear);
            } else if (choice == 2) {
                System.out.println("-> You invested! (Partner A is building the math for this, so for now it just goes to savings).");
                player.updateSavings(netSavingsThisYear); // Partner A will replace this line later!
            } else {
                System.out.println("-> Invalid choice. Defaulting to safe savings.");
                player.updateSavings(netSavingsThisYear);
            }
            
            System.out.println("\nEnd of Year Summary:");
            player.printFinancialSummary();
            
            if (year < 10) {
                System.out.print("Press [ENTER] to advance to the next year...");
                input.nextLine(); 
                System.out.println("\n");
            }
        }
        
        System.out.println("🎉 10-YEAR SIMULATION COMPLETE! 🎉");
        
        
        System.out.println("\n--- " + playerName + "'s 10-Year Life History ---");
        for (int i = 0; i < lifeHistory.size(); i++) {
            System.out.println(lifeHistory.get(i));
        }
        
        input.close();
    }
}