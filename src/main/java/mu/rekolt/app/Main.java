package mu.rekolt.app;

import mu.rekolt.util.ConsoleInput;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleInput input = new ConsoleInput(scanner);

        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.readIntInRange("Choose an option: ", 1, 4);

            switch (choice) {
                case 1 -> recordDelivery(input);
                case 2 -> System.out.println("Season figures coming in a later step.");
                case 3 -> System.out.println("Report generation coming in Objective 6.");
                case 4 -> {
                    System.out.println("Goodbye.");
                    running = false;
                }
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("REKOLT PRODUCE TRACKER");
        System.out.println("1. Record a delivery          3. Generate the season report");
        System.out.println("2. Season figures on screen   4. Exit");
        System.out.println();
    }

    //asks for one delivery's details and prints its net payable.
    private static void recordDelivery(ConsoleInput input) {
        String memberId = input.readMatching(
                "Member identifier              : ",
                "M-\\d{4}",
                "Must look like M-0042 (the letter M, a hyphen, then four digits)."
        );
        String memberName = input.readNonEmptyText("Member name                    : ");
        String produceCode = input.readOneOf(
                "Produce code (MZE/BNS/POT/TEA) : ",
                "Must be one of MZE, BNS, POT or TEA.",
                "MZE", "BNS", "POT", "TEA"
        );
        double massKg = input.readMassKg("Mass in kg                     : ");
        int qualityScore = input.readIntInRange("Quality score (0-100)          : ", 0, 100);
        int week = input.readIntInRange("Week of delivery (1-20)        : ", 1, 20);

        double netPayable = calculateNetPayable(produceCode, massKg, qualityScore);
        String grade = gradeFor(qualityScore);

        System.out.println();
        System.out.printf("Delivery recorded for %s (%s). Grade %s%n", memberName, memberId, grade);
        System.out.printf("  NET PAYABLE = %.2f MUR%n", netPayable);
    }

    //Grades a quality score into A, B, C, or REJECT using the fixed boundaries.
    private static String gradeFor(int qualityScore) {
        if (qualityScore >= 85) {
            return "A";
        } else if (qualityScore >= 70) {
            return "B";
        } else if (qualityScore >= 50) {
            return "C";
        } else {
            return "REJECT";
        }
    }

    //Returns the grade multiplier that matches a letter grade.
    private static double gradeMultiplierFor(String grade) {
        return switch (grade) {
            case "A" -> 1.15;
            case "B" -> 1.00;
            case "C" -> 0.85;
            default -> 0.00; // REJECT
        };
    }

    // Runs one delivery through the five payment steps.
    private static double calculateNetPayable(String produceCode, double massKg, int qualityScore) {
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
        double afterGrade = baseValue * gradeMultiplierFor(gradeFor(qualityScore));

        double categoryMultiplier = switch (category) {
            case "CEREAL" -> 1.00;
            case "PERISHABLE" -> 0.90;
            case "CASH_CROP" -> 1.10;
            default -> 1.00;
        };
        double afterCategory = afterGrade * categoryMultiplier;

        double commission = afterCategory * 0.05;
        double transportLevy = massKg * 2;

        return round2(afterCategory - commission - transportLevy);
    }

    private static double round2(double value) {
        return (double) Math.round(value * 100) / 100;
    }
}