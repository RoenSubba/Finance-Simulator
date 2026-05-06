public class Person {
    private String name;
    private int age;
    private double savings;
    private double debt;
    private double income;

    // Constructor
    public Person(String name, int startingAge, double startingIncome) {
        this.name = name;
        this.age = startingAge;
        this.income = startingIncome;
        this.savings = 5000.0; // Everyone starts with $5k
        this.debt = 0.0;
    }

    // Methods
    public void addAge() {
        age++;
    }

    public void printFinancialSummary() {
        System.out.println("\n--- " + name + "'s Finances (Age " + age + ") ---");
        System.out.println("Income:  $" + income);
        System.out.println("Savings: $" + savings);
        System.out.println("Debt:    $" + debt);
        System.out.println("Net Worth: $" + (savings - debt));
        System.out.println("------------------------------------");
    }
}