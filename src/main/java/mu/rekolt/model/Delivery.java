package mu.rekolt.model;

public class Delivery {

    private final String memberId;
    private final String memberName;
    private final String produceCode;
    private final double massKg;
    private final int qualityScore;
    private final int week;
    private final String grade;
    private final double netPayable;

    public Delivery(String memberId, String memberName, String produceCode,
                    double massKg, int qualityScore, int week,
                    String grade, double netPayable) {
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
    public String getGrade() { return grade; }
    public double getNetPayable() { return netPayable; }

    @Override
    public String toString() {
        return String.format("%s %s %.1fkg %s %.2f MUR", memberId, produceCode, massKg, grade, netPayable);
    }
}