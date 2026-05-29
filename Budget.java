public class Budget {
    private double rent;
    private double food;
    private double transport;
    private double entertainment;

    
    public Budget(double rent, double food, double transport, double entertainment) {
        this.rent = rent;
        this.food = food;
        this.transport = transport;
        this.entertainment = entertainment;
    }

    public double getTotalMonthlyExpenses() {
        return rent + food + transport + entertainment;
    }

    public double getTotalYearlyExpenses() {
        return getTotalMonthlyExpenses() * 12;
    }

    public double calculateAnnualSavings(double yearlyIncome) {
        double yearlyExpenses = getTotalYearlyExpenses();
        return yearlyIncome - yearlyExpenses;
    }

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