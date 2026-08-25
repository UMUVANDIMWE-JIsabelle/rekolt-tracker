package mu.rekolt.model;

public class CashCropProduce extends Produce {

    public CashCropProduce(String produceCode, double basePricePerKg) {
        super(produceCode, basePricePerKg);
    }

    @Override
    public double categoryMultiplier() {
        return 1.10;
    }
}