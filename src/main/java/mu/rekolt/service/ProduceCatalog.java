package mu.rekolt.service;

import mu.rekolt.model.CashCropProduce;
import mu.rekolt.model.CerealProduce;
import mu.rekolt.model.PerishableProduce;
import mu.rekolt.model.Produce;

import java.util.List;


public class ProduceCatalog {

    private static final List<Produce> CATALOG = List.of(
            new CerealProduce("MZE", 30),
            new CerealProduce("BNS", 90),
            new PerishableProduce("POT", 45),
            new CashCropProduce("TEA", 25)
    );

    //Finds a produce entry by code. Throws if the code isn't recognised

    public static Produce findByCode(String produceCode) {
        for (Produce produce : CATALOG) {
            if (produce.getProduceCode().equalsIgnoreCase(produceCode)) {
                return produce;
            }
        }
        throw new IllegalArgumentException("Unknown produce code: " + produceCode);
    }

    //Prints every catalog entry. Purely polymorphic: no instanceof, no downcasting.

    public static void printCatalog() {
        System.out.println("Produce catalog:");
        for (Produce produce : CATALOG) {
            System.out.println("  " + produce);
        }
    }
}