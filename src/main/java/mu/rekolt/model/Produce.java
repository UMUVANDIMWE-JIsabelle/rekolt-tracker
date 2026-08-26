package mu.rekolt.model;

import java.util.Objects;

public abstract class Produce {

    private final String produceCode;
    private final double basePricePerKg;

    protected Produce(String produceCode, double basePricePerKg) {
        if (produceCode == null || produceCode.isBlank()) {
            throw new IllegalArgumentException("Produce code cannot be empty.");
        }
        if (basePricePerKg <= 0) {
            throw new IllegalArgumentException("Base price per kg must be positive.");
        }
        this.produceCode = produceCode;
        this.basePricePerKg = basePricePerKg;
    }

    public String getProduceCode() {
        return produceCode;
    }

    public double getBasePricePerKg() {
        return basePricePerKg;
    }

    /** Each produce category values a delivery differently. */
    public abstract double categoryMultiplier();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Produce other)) return false;
        return produceCode.equals(other.produceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produceCode);
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f MUR/kg, x%.2f)", produceCode, basePricePerKg, categoryMultiplier());
    }
}