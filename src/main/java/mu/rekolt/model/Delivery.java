package mu.rekolt.model;

import java.util.Objects;

public class Delivery implements Payable, Reportable, Comparable<Delivery> {

    private final String memberId;
    private final String memberName;
    private final String produceCode;
    private final double massKg;
    private final int qualityScore;
    private final int week;
    private final Grade grade;
    private final double netPayable;

    public Delivery(String memberId, String memberName, String produceCode,
                    double massKg, int qualityScore, int week,
                    Grade grade, double netPayable) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member id cannot be empty.");
        }
        if (massKg <= 0 || massKg > 5000) {
            throw new IllegalArgumentException("Mass must be above 0 and not more than 5000 kg.");
        }
        if (qualityScore < 0 || qualityScore > 100) {
            throw new IllegalArgumentException("Quality score must be between 0 and 100.");
        }
        if (week < 1 || week > 20) {
            throw new IllegalArgumentException("Week must be between 1 and 20.");
        }
        this.memberId = memberId;
        this.memberName = memberName;
        this.produceCode = produceCode;
        this.massKg = massKg;
        this.qualityScore = qualityScore;
        this.week = week;
        this.grade = grade;
        this.netPayable = netPayable;
    }

    public String getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public String getProduceCode() { return produceCode; }
    public double getMassKg() { return massKg; }
    public int getQualityScore() { return qualityScore; }
    public int getWeek() { return week; }
    public Grade getGrade() { return grade; }

    @Override
    public double getNetPayable() {
        return netPayable;
    }

    @Override
    public String toReportSection() {
        return String.format("  %-4s %6.1fkg  %-6s %10.2f MUR", produceCode, massKg, grade, netPayable);
    }


    @Override
    public int compareTo(Delivery other) {
        return Double.compare(other.netPayable, this.netPayable);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Delivery other)) return false;
        return Double.compare(massKg, other.massKg) == 0
                && qualityScore == other.qualityScore
                && week == other.week
                && memberId.equals(other.memberId)
                && produceCode.equals(other.produceCode)
                && grade == other.grade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, produceCode, massKg, qualityScore, week, grade);
    }

    @Override
    public String toString() {
        return String.format("%s %s %.1fkg %s %.2f MUR", memberId, produceCode, massKg, grade, netPayable);
    }
}