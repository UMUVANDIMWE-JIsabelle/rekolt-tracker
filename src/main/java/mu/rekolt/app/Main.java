package mu.rekolt.app;

import mu.rekolt.util.ConsoleInput;

import java.util.Scanner;

import mu.rekolt.model.Delivery;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final double COMMISSION_RATE = 0.05;
    private static final double TRANSPORT_LEVY_PER_KG = 2.0;
    private static final double MAX_MASS_KG = 5000.0;

    private static final List<Delivery> deliveries = new ArrayList<>();

    private static final Map<String, Double> totalPaymentPerMember = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleInput input = new ConsoleInput(scanner);

        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.readIntInRange("Choose an option: ", 1, 4);

            switch (choice) {
                case 1 -> recordDelivery(input);
                case 2 -> showSeasonFigures();
                case 3 -> System.out.println("Report generating ...");
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

    //asks for one delivery's details, records it and prints its net payable.
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

        Delivery delivery = new Delivery(memberId, memberName, produceCode, massKg, qualityScore, week, grade, netPayable);
        deliveries.add(delivery);

        double previousTotal = totalPaymentPerMember.getOrDefault(memberId, 0.0);
        totalPaymentPerMember.put(memberId, previousTotal + netPayable);

        printDeliveryResult(memberId, memberName, grade, netPayable);
    }

    //  Prints the outcome of a single recorded delivery.
    private static void printDeliveryResult(String memberId, String memberName, String grade, double netPayable) {
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
        String grade = gradeFor(qualityScore);

        // A REJECT delivery earns nothing, and nothing is deducted from it either.
        if (grade.equals("REJECT")) {
            return 0.0;
        }

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
        double afterGrade = baseValue * gradeMultiplierFor(grade);

        double categoryMultiplier = switch (category) {
            case "CEREAL" -> 1.00;
            case "PERISHABLE" -> 0.90;
            case "CASH_CROP" -> 1.10;
            default -> 1.00;
        };
        double afterCategory = afterGrade * categoryMultiplier;

        double commission = afterCategory * COMMISSION_RATE;
        double transportLevy = massKg * TRANSPORT_LEVY_PER_KG;

        return round2(afterCategory - commission - transportLevy);
    }

    private static double round2(double value) {
        return (double) Math.round(value * 100) / 100;
    }

    // A season's worth of deliveries, hardcoded for testing. Each index across these arrays describes one delivery (same position = same delivery).
    private static final String[] SEASON_PRODUCE = {
            "BNS", "MZE", "POT", "TEA", "MZE", "BNS",
            "POT", "TEA", "MZE", "BNS", "POT", "MZE"
    };
    private static final double[] SEASON_MASS = {
            236.0, 180.0, 150.0, 88.3, 232.5, 210.0,
            95.0, 60.0, 300.0, 175.5, 120.0, 140.0
    };
    private static final int[] SEASON_QUALITY = {
            91, 78, 65, 40, 88, 72,
            55, 30, 95, 60, 82, 68
    };
    private static final int[] SEASON_WEEK = {
            1, 1, 2, 2, 3, 3,
            4, 4, 5, 5, 6, 6
    };
    private static final String[] PRODUCE_CODES = {"MZE", "BNS", "POT", "TEA"};

    private static void showSeasonFigures() {
        System.out.println();
        System.out.println("Total payment per member (MUR)");
        for (Map.Entry<String, Double> entry : totalPaymentPerMember.entrySet()) {
            System.out.printf("  %s  %10.2f%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
        System.out.println("Weekly volume grid (kg)");

        int weekCount = 6;
        // rows = weeks, columns = the four produce codes, built with a NESTED loop
        double[][] weeklyGrid = new double[weekCount][PRODUCE_CODES.length];

        for (int i = 0; i < SEASON_PRODUCE.length; i++) {
            int weekIndex = SEASON_WEEK[i] - 1;
            int produceIndex = indexOfProduce(SEASON_PRODUCE[i]);
            weeklyGrid[weekIndex][produceIndex] += SEASON_MASS[i];
        }

        System.out.print("  Week ");
        for (String code : PRODUCE_CODES) {
            System.out.printf("%8s", code);
        }
        System.out.println("   Total");

        for (int week = 0; week < weekCount; week++) {
            System.out.printf("  %4d ", week + 1);
            double rowTotal = 0;
            for (int col = 0; col < PRODUCE_CODES.length; col++) {
                System.out.printf("%8.1f", weeklyGrid[week][col]);
                rowTotal += weeklyGrid[week][col];
            }
            System.out.printf("%9.1f%n", rowTotal);
        }

        System.out.println();
        System.out.println("Deliveries processed this season: " + SEASON_PRODUCE.length);
    }
// Finds which column a produce code belongs to in the fixed grid. Returns -1 if not found.
    private static int indexOfProduce(String code) {
        return indexOfProduce(code, PRODUCE_CODES);
    }
    //Overload: finds a code's index in ANY array you give it, not just PRODUCE_CODES.
    private static int indexOfProduce(String code, String[] codes) {
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(code)) {
                return i;
            }
        }
        return -1;
    }
}