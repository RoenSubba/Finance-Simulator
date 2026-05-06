public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to LifeFinance Simulator!");
        
        // Instantiate a new Person object
        Person player = new Person("Alex", 22, 50000.0);
        
        // Test the method
        player.printFinancialSummary();
    }
}