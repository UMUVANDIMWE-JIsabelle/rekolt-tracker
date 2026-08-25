package mu.rekolt.model;
public enum Grade {
    A(1.15),
    B(1.00),
    C(0.85),
    REJECT(0.00);

    private final double multiplier;

    Grade(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }

    //Grades a quality score into one of the four fixed grades.
    public static Grade fromQualityScore(int qualityScore) {
        if (qualityScore >= 85) {
            return A;
        } else if (qualityScore >= 70) {
            return B;
        } else if (qualityScore >= 50) {
            return C;
        } else {
            return REJECT;
        }
    }
}