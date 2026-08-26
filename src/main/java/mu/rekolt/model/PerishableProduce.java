package mu.rekolt.model;

public class PerishableProduce extends Produce {

    public PerishableProduce(String produceCode, double basePricePerKg) {
        super(produceCode, basePricePerKg);
    }

    @Override
    public double categoryMultiplier() {
        return 0.90;
    }
}