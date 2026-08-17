package mu.rekolt.util;

import java.util.Scanner;
import java.util.regex.Pattern;


public class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    /** asks for a whole number within [min, max] inclusive, otherwise re-ask*/
    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  Must be a whole number from %d to %d. Please try again.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  That is not a whole number. Please try again.");
            }
        }
    }

    /** asks for a decimal mass strictly greater than 0 and at most 5000. */
    public double readMassKg(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(raw);
                if (value > 0 && value <= 5000) {
                    return value;
                }
                System.out.println("  Mass must be above 0 and not more than 5000. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  That is not a number. Please try again.");
            }
        }
    }

    /** asks for non-empty, non-whitespace text (e.g. a member's name). */
    public String readNonEmptyText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            if (!raw.isEmpty()) {
                return raw;
            }
            System.out.println("  This cannot be empty. Please try again.");
        }
    }

    /**  text matching a given pattern, e.g. the member identifier format. */
    public String readMatching(String prompt, String regex, String errorMessage) {
        Pattern pattern = Pattern.compile(regex);
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            if (pattern.matcher(raw).matches()) {
                return raw;
            }
            System.out.println("  " + errorMessage);
        }
    }

    /** asking for one of a fixed set of codes (e.g. produce codes), case-insensitive. */
    public String readOneOf(String prompt, String errorMessage, String... validCodes) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim().toUpperCase();
            for (String code : validCodes) {
                if (code.equals(raw)) {
                    return raw;
                }
            }
            System.out.println("  " + errorMessage);
        }
    }
}
