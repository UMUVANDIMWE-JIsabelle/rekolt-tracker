# Deviations from design-v1

## Service classes added: ProduceCatalog and PaymentService
In the design-v1's UML diagram, I initially implied that a payment calculation existed but did
not detail where it lived. During implementation, this logic was
split into two concrete service classes:
- `ProduceCatalog` holds the fixed List<Produce> and looks up a
  produce by code.
- `PaymentService` runs the five-step calculation, using
  `ProduceCatalog` and `Grade` instead of the old switch statements.


## Grade returned as an enum
The code, `Grade` is
returned directly from `Grade.fromQualityScore(qualityScore)`, and
every multiplier lookup goes through `grade.getMultiplier()` rather
than a separate switch. This removed a whole class of "grade string
and multiplier get out of sync" bugs before they could happen.

## Replaced 2 collections by one Map<String, Member>
I originally used two separate collections:
`totalPaymentPerMember` (HashMap<String, Double>) and
`deliveriesByMember` (Map<String, List<Delivery>>). But now,
these were both replaced by a single `Map<String, Member>`, since a
 `Member` object can hold its own deliveries and compute its own
total on demand via `getNetPayable()`. This removes the risk of two collections drifting out of
sync with each other after a removal.

## SeasonReport not yet implemented
design-v1's diagram included a `SeasonReport` class implementing
`Reportable`, aggregating all members, and generating the Word
document. This class has not been built. `Reportable` is currently
implemented by `Delivery` and `Member` only; `SeasonReport` will be
added as a third implementer when the report generation work begins.