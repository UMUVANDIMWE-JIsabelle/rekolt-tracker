package mu.rekolt.service;

import mu.rekolt.model.Grade;
import mu.rekolt.model.Produce;

public class PaymentService {

    private static final double COMMISSION_RATE = 0.05;
    private static final double TRANSPORT_LEVY_PER_KG = 2.0;

    // Runs one delivery through the five payment steps. See assignment section 2.
    public static double calculateNetPayable(Produce produce, Grade grade, double massKg) {
        if (grade == Grade.REJECT) {
            return 0.0;
        }

        double baseValue = massKg * produce.getBasePricePerKg();
        double afterGrade = baseValue * grade.getMultiplier();
        double afterCategory = afterGrade * produce.categoryMultiplier(); // polymorphic call

        double commission = afterCategory * COMMISSION_RATE;
        double transportLevy = massKg * TRANSPORT_LEVY_PER_KG;

        return round2(afterCategory - commission - transportLevy);
    }

    private static double round2(double value) {
        return (double) Math.round(value * 100) / 100;
    }
}