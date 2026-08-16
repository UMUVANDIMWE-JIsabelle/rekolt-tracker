package mu.rekolt.app;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("REKOLT PRODUCE TRACKER ");
        System.out.println();

        System.out.print("Produce code (MZE/BNS/POT/TEA): ");
        String produceCode = scanner.nextLine().trim().toUpperCase();

        System.out.print("Mass in kg: ");
        double massKg = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Quality score (0-100): ");
        int qualityScore = Integer.parseInt(scanner.nextLine().trim());

        //  Step 1: base value
        double basePricePerKg;
        String category;
        switch (produceCode) {
            case "MZE" -> { basePricePerKg = 30; category = "CEREAL"; }
            case "BNS" -> { basePricePerKg = 90; category = "CEREAL"; }
            case "POT" -> { basePricePerKg = 45; category = "PERISHABLE"; }
            case "TEA" -> { basePricePerKg = 25; category = "CASH_CROP"; }
            default -> throw new IllegalArgumentException("Unknown produce code: " + produceCode);
        }
        double baseValue = massKg * basePricePerKg;

        //  Step 2: applying the grade multiplier
        double gradeMultiplier;
        String grade;
        if (qualityScore >= 85) {
            gradeMultiplier = 1.15;
            grade = "A";
        } else if (qualityScore >= 70) {
            gradeMultiplier = 1.00;
            grade = "B";
        } else if (qualityScore >= 50) {
            gradeMultiplier = 0.85;
            grade = "C";
        } else {
            gradeMultiplier = 0.00;
            grade = "REJECT";
        }
        double afterGrade = baseValue * gradeMultiplier;

        // Step 3: added the category multiplier
        double categoryMultiplier = switch (category) {
            case "CEREAL" -> 1.00;
            case "PERISHABLE" -> 0.90;
            case "CASH_CROP" -> 1.10;
            default -> 1.00;
        };
        double afterCategory = afterGrade * categoryMultiplier;

        // Step 4: commission (cooperative keeps 5%)
        double commission = afterCategory * 0.05;

        //  Step 5: transport levy (2 MUR per kg delivered)
        double transportLevy = massKg * 2;

        //  Net payable
        double netPayable = afterCategory - commission - transportLevy;

        //  Display: allowed rounding to happen ONLY here
        System.out.println();
        System.out.printf("Delivery recorded. Grade %s%n", grade);
        System.out.printf("  Base value        %.1f x %.2f       = %12.2f%n", massKg, basePricePerKg, baseValue);
        System.out.printf("  Grade %-6s              x %.2f     = %12.2f%n", grade, gradeMultiplier, afterGrade);
        System.out.printf("  Category                   x %.2f     = %12.2f%n", categoryMultiplier, afterCategory);
        System.out.printf("  Commission 5%%                       - %12.2f%n", commission);
        System.out.printf("  Transport levy     %.1f x  2.00       - %12.2f%n", massKg, transportLevy);
        System.out.printf("  NET PAYABLE                          = %12.2f MUR%n", round2(netPayable));

        scanner.close();
    }

    private static double round2(double value) {
        return (double) Math.round(value * 100) / 100;
    }
}