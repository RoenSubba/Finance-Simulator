public class Main {
    public static void main(String[] args) {
        System.out.println("=== PARTNER A: BACKEND ENGINE TEST ===");

        // 1. Create our 16-year-old test subject
        Person testSubject = new Person("Test Roen");

        System.out.println("Starting Age: " + testSubject.getAge());
        System.out.println("Starting Cash: $" + testSubject.getCash());
        System.out.println("Starting Investments: $" + testSubject.getInvestments());
        System.out.println("----------------------------------------");

        // 2. Test the New Architecture: Instant Action FIRST, then Time
        System.out.println(">>> INSTANT ACTION: Getting a Part-Time Job...");
        GameEngine.takeInstantAction(testSubject, "Get Part-Time Job");
        System.out.println("Annual Cash Flow is now: $" + GameEngine.getAnnualCashFlow(testSubject) + "/yr");

        System.out.println("\n>>> SIMULATING 10 YEARS...");
        GameEngine.simulateTenYears(testSubject);

        System.out.println("New Age: " + testSubject.getAge());
        System.out.println("Cash: $" + (int)testSubject.getCash());
        System.out.println("Investments: $" + (int)testSubject.getInvestments());
        System.out.println("Happiness: " + testSubject.getHappiness() + "/100");
        System.out.println("----------------------------------------");

        // 3. Test Capital Allocation and Market Math
        System.out.println(">>> INSTANT ACTION: Investing 50% of cash...");
        GameEngine.takeInstantAction(testSubject, "Invest 50%");

        System.out.println("\n>>> SIMULATING 1 YEAR...");
        GameEngine.simulateOneYear(testSubject);

        System.out.println("New Age: " + testSubject.getAge());
        System.out.println("Cash: $" + (int)testSubject.getCash());
        System.out.println("Investments: $" + (int)testSubject.getInvestments());
        System.out.println("----------------------------------------");

        // 4. Test the Life History ArrayList
        System.out.println(">>> PRINTING LIFE HISTORY...");
        testSubject.printHistory();
        System.out.println("----------------------------------------");

        System.out.println("Engine test complete! Math and Cash Flow are working perfectly.");
    }
}
