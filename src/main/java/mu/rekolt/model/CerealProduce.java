package mu.rekolt.model;

public class CerealProduce extends Produce {

    public CerealProduce(String produceCode, double basePricePerKg) {
        super(produceCode, basePricePerKg);
    }

    @Override
    public double categoryMultiplier() {
        return 1.00;
    }
}