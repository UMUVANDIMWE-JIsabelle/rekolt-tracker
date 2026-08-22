# Collections rationale

## ArrayList<Delivery> - `deliveries`
Holds every delivery in the order it was recorded. An `ArrayList` was
chosen over a `LinkedList` because deliveries are read far more often
than they're removed from arbitrary positions - printing the season
figures, sorting, and searching all need fast indexed access, which
`ArrayList` provides in O(1). A `LinkedList` would only pay off if we
were frequently inserting or removing from the middle of the list,
which we aren't.

## HashMap<String, Double> - `totalPaymentPerMember`
A running total keyed by member ID. `HashMap` gives O(1) average
lookup and update, which matters because this total is updated on
every single delivery recorded across the whole season. Member IDs
are unique by definition, so there's no need for a structure that
tolerates duplicate keys. Ordering doesn't matter here - we only ever
look a total up by ID, never need it in a particular sequence - so a
`TreeMap` (which keeps keys sorted but is slower to update) would be
unjustified overhead.

## Map<String, List<Delivery>> - `deliveriesByMember`
Groups the full delivery history per member, needed later for
printing each member's delivery table in the season report. A map of
lists was chosen over, say, a list of custom "member record" objects,
because it lets us go straight from a known member ID to their exact
deliveries without scanning the whole season. The alternative
considered and rejected: storing everything in one flat list and
filtering it by member ID on demand every time - this would work, but
turns an O(1) lookup into an O(n) scan every time the report needs a
member's deliveries.

## HashSet<String> - `memberIds`
Tracks which member IDs have appeared this season, with automatic
duplicate elimination. A `Set` was the obvious choice specifically
*because* it enforces uniqueness - the same guarantee an `ArrayList`
cannot offer without extra manual checking on every insert. We don't
care about insertion order for this one, only "how many distinct
members," so `HashSet` (unordered but fastest) was preferred over
`LinkedHashSet` or `TreeSet`, neither of which would add value here.

## Comparable vs Comparator
`Delivery implements Comparable<Delivery>` defines the class's single
natural ordering - highest net payable first - because that's the
most common way anyone would want to rank deliveries by default.
`Comparator` was used separately to sort by member ID then week,
since that ordering is specific to one screen (season figures) and
doesn't belong permanently baked into the class itself. Using a
`Comparator` here instead of adding a second method to `Delivery`
keeps the model class focused on representing data, not on every
possible way someone might want to view it.

## Search and the absent case
`findDeliveriesByMemberId` returns an **empty list**, not `null`, when
a member isn't found. The alternative - returning `null` - would push
a null-check onto every caller of this method; forgetting even one
would crash the program with a `NullPointerException`. An empty list
can always be safely looped over whether or not anything was found.

## Iterator-based removal
Removing REJECT deliveries required an `Iterator` rather than a
for-each loop, because Java throws a `ConcurrentModificationException`
if a collection is structurally modified (an item removed) while a
for-each loop is walking it - the for-each loop's hidden iterator
loses track of where it is. Calling `iterator.remove()` instead of
`deliveries.remove(d)` is what makes the removal safe, since the
iterator adjusts its own internal position immediately afterward. The
same removal also updates `deliveriesByMember`, so the two
collections can't drift out of sync with each other.