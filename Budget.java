public class Budget {
    // Encapsulated instance variables for monthly expenses
    private double rent;
    private double food;
    private double transport;
    private double entertainment;

    // Constructor
    public Budget(double rent, double food, double transport, double entertainment) {
        this.rent = rent;
        this.food = food;
        this.transport = transport;
        this.entertainment = entertainment;
    }

    // Calculate total monthly expenses
    public double getTotalMonthlyExpenses() {
        return rent + food + transport + entertainment;
    }

    // Calculate total yearly expenses
    public double getTotalYearlyExpenses() {
        return getTotalMonthlyExpenses() * 12;
    }

    // Calculate how much money is left over to save at the end of the year
    public double calculateAnnualSavings(double yearlyIncome) {
        double yearlyExpenses = getTotalYearlyExpenses();
        return yearlyIncome - yearlyExpenses;
    }

    // Print a budget breakdown
    public void printBudgetSummary() {
        System.out.println("--- Monthly Budget Breakdown ---");
        System.out.println("Rent:          $" + rent);
        System.out.println("Food:          $" + food);
        System.out.println("Transport:     $" + transport);
        System.out.println("Entertainment: $" + entertainment);
        System.out.println("Total Monthly: $" + getTotalMonthlyExpenses());
        System.out.println("Total Yearly:  $" + getTotalYearlyExpenses());
        System.out.println("--------------------------------");
    }
}