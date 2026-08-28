package mu.rekolt.model;

import java.util.ArrayList;
import java.util.List;

// Aggregates every member's records for one season. I also used it to generate the season-wide totals and hand data to the document writer.

public class SeasonReport implements Reportable {

    private final List<Member> members = new ArrayList<>();

    public void addMember(Member member) {
        members.add(member);
    }

    public List<Member> getMembers() {
        return members;
    }

    public double getSeasonTotal() {
        double total = 0;
        for (Member member : members) {
            total += member.getNetPayable();
        }
        return total;
    }

    @Override
    public String toReportSection() {
        StringBuilder sb = new StringBuilder();
        for (Member member : members) {
            sb.append(member.toReportSection()).append(System.lineSeparator());
        }
        sb.append(String.format("SEASON TOTAL: %.2f MUR%n", getSeasonTotal()));
        return sb.toString();
    }
}