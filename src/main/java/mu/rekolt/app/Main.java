package mu.rekolt.app;

import mu.rekolt.model.Delivery;
import mu.rekolt.model.Grade;
import mu.rekolt.model.Member;
import mu.rekolt.model.Produce;
import mu.rekolt.service.PaymentService;
import mu.rekolt.service.ProduceCatalog;
import mu.rekolt.util.ConsoleInput;
import mu.rekolt.model.SeasonReport;
import mu.rekolt.service.ReportWriterService;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final List<Delivery> deliveries = new ArrayList<>();
    private static final Map<String, Member> members = new HashMap<>();
    private static final Set<String> memberIds = new HashSet<>();

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
                case 3 -> generateSeasonReport();
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

        Produce produce = ProduceCatalog.findByCode(produceCode);
        Grade grade = Grade.fromQualityScore(qualityScore);
        double netPayable = PaymentService.calculateNetPayable(produce, grade, massKg);

        Delivery delivery = new Delivery(memberId, memberName, produceCode, massKg, qualityScore, week, grade, netPayable);
        deliveries.add(delivery);

        Member member = members.computeIfAbsent(memberId, id -> new Member(id, memberName));
        member.addDelivery(delivery);
        memberIds.add(memberId);

        System.out.println();
        System.out.printf("Delivery recorded for %s (%s). Grade %s%n", memberName, memberId, grade);
        System.out.printf("  NET PAYABLE = %.2f MUR%n", netPayable);
    }

    private static void showSeasonFigures() {
        System.out.println();
        System.out.printf("Distinct members this season: %d%n", memberIds.size());

        System.out.println();
        System.out.println("Total payment per member (MUR)");
        for (Member member : members.values()) {
            System.out.printf("  %s  %10.2f%n", member.getMemberId(), member.getNetPayable());
        }

        System.out.println();
        System.out.println("Highest Deliveries");
        List<Delivery> sortedByValue = new ArrayList<>(deliveries);
        java.util.Collections.sort(sortedByValue);
        for (Delivery d : sortedByValue) {
            System.out.println("  " + d);
        }

        System.out.println();
        System.out.println("All deliveries made by a member");
        List<Delivery> sortedByMemberThenWeek = new ArrayList<>(deliveries);
        sortedByMemberThenWeek.sort(
                Comparator.comparing(Delivery::getMemberId).thenComparingInt(Delivery::getWeek)
        );
        for (Delivery d : sortedByMemberThenWeek) {
            System.out.println("  " + d);
        }

        System.out.println();
        System.out.println("Search test: deliveries for M-0999");
        printMemberDeliveries("M-0999");
        System.out.println("Search test: deliveries for M-9999 (should not exist)");
        printMemberDeliveries("M-9999");
    }

    private static void printMemberDeliveries(String memberId) {
        Member member = members.get(memberId);
        if (member == null || member.getDeliveries().isEmpty()) {
            System.out.println("  No deliveries found for this member.");
            return;
        }
        for (Delivery d : member.getDeliveries()) {
            System.out.println("  " + d);
        }
    }

    private static void generateSeasonReport() {
        SeasonReport report = new SeasonReport();
        for (Member member : members.values()) {
            report.addMember(member);
        }

        System.out.println("Writing output/season-report.docx ...");
        try {
            ReportWriterService.generateDocument(report, "output/season-report.docx");
            System.out.printf("%d member sections, done.%n", report.getMembers().size());
        } catch (IOException e) {
            System.out.println("Could not write the report. Check that the output folder exists and is not open in another program, then try again.");
        }
    }

    // Removes all REJECT-graded deliveries using an Iterator, keeping the season and each Member in sync.
    private static int removeRejectedDeliveries() {
        int removedCount = 0;
        Iterator<Delivery> iterator = deliveries.iterator();
        while (iterator.hasNext()) {
            Delivery d = iterator.next();
            if (d.getGrade() == Grade.REJECT) {
                iterator.remove();
                Member member = members.get(d.getMemberId());
                if (member != null) {
                    member.removeDelivery(d);
                }
                removedCount++;
            }
        }
        return removedCount;
    }
}