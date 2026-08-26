package mu.rekolt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Member implements Payable, Reportable {

    private final String memberId;
    private final String name;
    private final List<Delivery> deliveries = new ArrayList<>();

    public Member(String memberId, String name) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member id cannot be empty.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Member name cannot be empty.");
        }
        this.memberId = memberId;
        this.name = name;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public List<Delivery> getDeliveries() { return deliveries; }

    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
    }

    public boolean removeDelivery(Delivery delivery) {
        return deliveries.remove(delivery);
    }

    @Override
    public double getNetPayable() {
        double total = 0;
        for (Delivery d : deliveries) {
            total += d.getNetPayable();
        }
        return total;
    }

    @Override
    public String toReportSection() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Member %s - %s%n", memberId, name));
        for (Delivery d : deliveries) {
            sb.append(d.toReportSection()).append(System.lineSeparator());
        }
        sb.append(String.format("NET PAYABLE: %.2f MUR%n", getNetPayable()));
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Member other)) return false;
        return memberId.equals(other.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %d deliveries", memberId, name, deliveries.size());
    }
}