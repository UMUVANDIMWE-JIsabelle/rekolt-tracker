# REKOLT Planters' Cooperative Produce Tracker

A console application built for REKOLT Planters' Cooperative, a
smallholder farming cooperative of roughly 400 members in the central
uplands of Mauritius. It's aim is to replace a paper-slip payment process with
one that applies the cooperative's payment rules consistently, and
then generates the end-of-season payment
statements as a single Word document.

## The problem this solves

Previously, a treasurer worked through a season's worth of hand-written
slips with a calculator, which took up to eleven days and led to
disputes and duplicate payments. This application records each
delivery as it happens, calculates the payment automatically using a
fixed set of rules, and produces one reconciled report at the end of
the season.

## Requirements

- JDK 17 or later
- Maven

## How to build and run 

```bash
git clone https://github.com/UMUVANDIMWE-JIsabelle/rekolt-tracker.git
cd rekolt-tracker
```

Open the folder in IntelliJ IDEA ... it detects `pom.xml` automatically
and sets up Maven. Then run the entry point:

- Open `src/main/java/mu/rekolt/app/Main.java`
- Right-click inside the file → **Run 'Main.main()'**

## The payment rules

Every delivery is valued through five fixed steps:

1. **Base value** = mass (kg) × base price for the crop
2. **× grade multiplier** => from the quality score (A/B/C/REJECT)
3. **× category multiplier** => cereal, perishable, or cash crop
4. **− 5% commission**
5. **− transport levy** (2 MUR per kg delivered)

A `REJECT` grade (quality score below 50) is still recorded and
counted in the season's volume statistics, but its value is zero and
no deductions are taken from it.


## Type and precision decisions

- **`double`** is used for mass and all money values, since both can
  have fractional parts (e.g. 236.5 kg, 22,732.70 MUR).
- **`int`** is used for the quality score as a whole number from 0 to 100.
- **Rounding happens only at display time.** Every intermediate
  calculation (base value, grade multiplier, commission, levy) is
  kept as a full-precision `double`, so small rounding errors can't
  accumulate across the five steps. Only the final printed figure is
  rounded to 2 decimals, using an explicit `(double)` cast on the
  result of `Math.round()` (which returns a `long`).


## Author

J'Isabelle UMUVANDIMWE