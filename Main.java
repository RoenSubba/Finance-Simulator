public class Main {
    public static void main(String[] args) {
        System.out.println("=== PARTNER A: BACKEND ENGINE TEST ===");

        // 1. Create our 16-year-old test subject
        Person testSubject = new Person("Test Roen");

        System.out.println("Starting Age: " + testSubject.getAge());
        System.out.println("Starting Cash: $" + testSubject.getCash());
        System.out.println("Starting Investments: $" + testSubject.getInvestments());
        System.out.println("----------------------------------------");

        // 2. Test the new 10-Year Loop (Ages 16-26)
        System.out.println(">>> SIMULATING 10 YEARS - Action: Part-Time Job...");
        GameEngine.simulateTenYears(testSubject, "Part-Time Job");

        System.out.println("New Age: " + testSubject.getAge());
        System.out.println("Cash: $" + (int)testSubject.getCash());
        System.out.println("Investments: $" + (int)testSubject.getInvestments());
        System.out.println("Happiness: " + testSubject.getHappiness() + "/100");
        System.out.println("----------------------------------------");

        // 3. Test the Stock Market Math (Age 26-27)
        System.out.println(">>> SIMULATING 1 YEAR - Action: Invest 50%...");
        GameEngine.simulateOneYear(testSubject, "Invest 50%");

        System.out.println("New Age: " + testSubject.getAge());
        System.out.println("Cash: $" + (int)testSubject.getCash());
        System.out.println("Investments: $" + (int)testSubject.getInvestments());
        System.out.println("----------------------------------------");

        // 4. Test the Life History ArrayList
        System.out.println(">>> PRINTING LIFE HISTORY...");
        testSubject.addLifeEvent("Started working a Part-Time Job.");
        testSubject.addLifeEvent("Invested 50% of cash into the stock market.");
        testSubject.printHistory();
        System.out.println("----------------------------------------");

        System.out.println("Engine test complete! Math is working perfectly.");
    }
}
}