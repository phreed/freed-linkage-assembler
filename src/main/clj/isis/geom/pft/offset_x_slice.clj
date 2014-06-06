"
---
PFT entry: ALL
---

Constraint:
  offset-x(?M_1, ?M_2, ?a)

Assumptions:
  marker-has-invariant-z(?M_1)
  marker-has-invariant-z(?M_2)
  marker-has-invariant-x(?M_1)
  geom-has_marker(?geom, ?M_2)


---
PFT entry: (0,0,offset-x)
---

Initial status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = dot-product(gmx(?M_1), gmx(?M_2));
  R[1] = cos(it ?a)
  unless zero?(R[0] - R[1])
    error(R[0] - R[1], estring[9]);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the offset-x constraint can only be checked for consistency.


---
PFT entry: (1,0,offset-x)
---

Initial status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = dot-product(gmx(?M_1), gmx(?M_2));
  R[1] = cos(it ?a)
  unless zero?(R[0] - R[1])
    error(R[0] - R[1], estring[9]);
  end;

New status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the offset-x constraint can only be checked for consistency.


---
PFT entry: (2,0,offset-x)
---

Initial status:
  2-TDOF(?geom, ?point, ?line, ?lf)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = dot-product(gmx(?M_1), gmx(?M_2));
  R[1] = cos(it ?a)
  unless zero?(R[0] - R[1])
    error(R[0] - R[1], estring[9]);
  end;

New status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the offset-x constraint can only be checked for consistency.


---
PFT entry: (3,0,offset-x)
---

Initial status:
  3-TDOF(?geom)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = dot-product(gmx(?M_1), gmx(?M_2));
  R[1] = cos(it ?a)
  unless zero?(R[0] - R[1])
    error(R[0] - R[1], estring[9]);
  end;

New status:
  3-TDOF(?geom)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the offset-x constraint can only be checked for consistency.

---
PFT entry: (h,h,offset-x)
---

Initial status:
  h-TDOF(?geom, ?point, ?line, ?point)
  h-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = vec-angle(gmx(?M_2), gmx(?M_1), ?axis);
  R[1] = R[0] - it ?a;

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[1])
    else 2r/a(?geom, ?point, R[1], ?axis, ?axis_1, ?axis_2);

  R[2] = pc-check(?line, 0, R[1], 0);
  unless zero?(R[2])
    error(R[2], estring[4]);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom is rotated about ?axis.
  The rotation must be consistent with the translation
  and ?line parameters.


---
PFT entry: (0,1,offset-x)
---

Initial status:
  0-TDOF(?geom, ?point)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = vec-angle(gmx(?M_2), gmx(?M_1), ?axis);
  R[1] = R[0] - it ?a;

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[1])
    else 2r/a(?geom, ?point, R[1], ?axis, ?axis_1, ?axis_2);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom is rotated about ?axis to
  give the marker x-axes their proper relative angle.


---
PFT entry: (1,1,offset-x)
---

Initial status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = vec-angle(gmx(?M_2), gmx(?M_1), ?axis);
  R[1] = R[0] - it ?a;

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[1])
    else 2r/a(?geom, ?point, R[1], ?axis, ?axis_1, ?axis_2);
  end;

New status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  0-RDOF(?geom)

Explanation:
  Geom ?geom is rotated about ?axis to
  give the marker x-axes their proper relative angle.



---
PFT entry: (2,1,offset-x)
---

Initial status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = vec-angle(gmx(?M_2), gmx(?M_1), ?axis);
  R[1] = R[0] - it ?a;

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[1])
    else 2r/a(?geom, ?point, R[1], ?axis, ?axis_1, ?axis_2);
  end;

New status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  0-RDOF(?geom)

Explanation:
  Geom ?geom is rotated about ?axis to
  give the marker x-axes their proper relative angle.


---
PFT entry: (3,1,offset-x)
---

Initial status:
  3-TDOF(?geom)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = vec-angle(gmx(?M_2), gmx(?M_1), ?axis);
  R[1] = R[0] - it ?a;

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[1])
    else 2r/a(?geom, ?point, R[1], ?axis, ?axis_1, ?axis_2);
  end;

New status:
  3-TDOF(?geom)
  1-RDOF(?geom)

Explanation:
  Geom ?geom is rotated about ?axis to
  give the marker x-axes their proper relative angle.


"
