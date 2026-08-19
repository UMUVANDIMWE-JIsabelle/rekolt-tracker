# Grade boundary verification 

Manually tested each grade cutoff by running the program and entering
each boundary value as the quality score.

| Score | Expected grade | Result | Net payable  |
|---|---|--------|--------------|
| 84 | B | ✔ B    | (nonzero)    |
| 85 | A | ✔ A    | (nonzero)    |
| 69 | C | ✔ C    | (nonzero)    |
| 70 | B | ✔ B    | (nonzero)    |
| 49 | REJECT | ✔ REJECT | 0.00 (Fixed) |
| 50 | C | ✔ C     | (nonzero)    |

**Bug found and fixed during this testing:** a REJECT delivery (score49) initially returned a *negative* net payable (-160.00 MUR) because
  the transport levy was still being subtracted after the grade
  multiplier had already zeroed the value out. Fixed by returning 0.0
  immediately once a delivery is graded REJECT, before any commission
  or levy calculation runs.


## Mass boundary verification 

| Mass entered | Expected | Result                            |
|---|---|-----------------------------------|
| 5000 | Accepted (inclusive upper bound) | ✔ Accepted                        |
| 5005 | Rejected, re-prompt | ✔Rejected, re-prompted, no crash  |
| 0 | Rejected, re-prompt (mass must be strictly > 0) | ✔ Rejected, re-prompted, no crash |

Also confirmed the menu itself never crashes on bad input: entering
"a" as the menu choice printed a clear error and re-asked, rather
than throwing an exception.