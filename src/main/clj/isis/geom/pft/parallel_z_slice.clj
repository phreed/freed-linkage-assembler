
---
PFT entry: ALL
---

Constraint:
  parallel-z(?M_1, ?M_2)

Assumptions:
  marker-has-invariant-position(?M_1)
  geom-has_marker(?geom, ?M_2)


---
PFT entry: (0,0,parallel-z)
---

Initial status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = cross-prod(gmp(?M_1), gmz(?M_2));
  unless zero?(R[0])
    error(R[0], estring[9]);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the parallel-z constraint can only be checked for consistency.


---
PFT entry: (1,0,parallel-z)
---

Initial status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = cross-prod(gmp(?M_1), gmz(?M_2));
  unless zero?(R[0])
    error(R[0], estring[9]);
  end;

New status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the parallel-z constraint can only be checked for consistency.


---
PFT entry: (2,0,parallel-z)
---

Initial status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = cross-prod(gmz(?M_1), gmz(?M_2));
  unless zero?(R[0])
    error(R[0], estring[9]);
  end;

New status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the parallel-z constraint can only be checked for consistency.


---
PFT entry: (3,0,parallel-z)
---

Initial status:
  3-TDOF(?geom)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = cross-prod(gmz(?M_1), gmz(?M_2));
  unless zero?(R[0])
    error(R[0], estring[9]);
  end;

New status:
  3-TDOF(?geom)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the parallel-z constraint can only be checked for consistency.

---
PFT entry: (h,h,parallel-z)
---

Initial status:
  h-TDOF(?geom, ?point, ?line, ?point)
  h-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = vec-sum(?point, gmz(?M_1));
  R[1] = vec-sum(?point, gmz(?M_2));
  R[2] = perp-base(R[0], ?line);
  R[3] = perp-base(R[1], ?line);
  R[4] = vec-diff(R[0], R[2]);
  R[5] = vec-diff(R[1], R[3]);
  R[6] = vec-angle(R[5], R[4], ?axis);
  R[7] = pc-check(?line, nil, R[6], 0);
  unless R[7]
    error(pc-error(?line, nil, R[6]),
    estring[4]);

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[6])
    else 2r/a(?geom, ?point, R[6], ?axis, ?axis_1, ?axis_2);

  R[8] = mag(R[7]) - perp-dist(R[2], R[3]);

  unless zero?(R[8])
    error(R[8], estring[8]);
  translate(?geom, R[7]);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom is translated along ?line and rotated about ?axis.
  The rotation must be consistent with the translation
  and ?line parameters.


---
PFT entry: (0,1,parallel-z)
---

Initial status:
  0-TDOF(?geom, ?point)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = line(?point, ?axis);
  R[1] = vec-sum(?point, gmz(?M_1));
  R[2] = vec-sum(?point, gmz(?M_2));
  R[3] = perp-base(R[1], R[0]);
  R[4] = perp-base(R[2], R[0]);

  unless equal?(R[3], R[4])
    error(R[3]-R[4]), estring[4]);

  R[5] = vec-diff(R[1], R[3]);
  R[6] = vec-diff(R[2], R[4]);
  R[7] = vec-angle(R[6], R[5], ?axis);

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[7])
    else 2r/a(?geom, ?point, R[7], ?axis, ?axis_1, ?axis_2);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has only one rotational degree of freedom.
  Therefore it must be rotated about its known point and known axis.
  The z-axes of the two markers must have the same projection
  onto the line defined by ?point and ?axis.


---
PFT entry: (1,1,parallel-z)
---

Initial status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = line(?point, ?axis);
  R[1] = vec-sum(?point, gmz(?M_1));
  R[2] = vec-sum(?point, gmz(?M_2));
  R[3] = perp-base(R[1], R[0]);
  R[4] = perp-base(R[2], R[0]);

  unless equal?(R[3], R[4])
    error(R[3]-R[4]), estring[8]);

  R[5] = vec-diff(R[1], R[3]);
  R[6] = vec-diff(R[2], R[4]);
  R[7] = vec-angle(R[6], R[5], ?axis);

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[7])
    else 2r/a(?geom, ?point, R[7], ?axis, ?axis_1, ?axis_2);
  end;

New status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has only one rotational and one translational degree of freedom.
  Therefore ?geom must be rotated about its known point and know axis.
  The z-axes of the two markers must havew the same projection onto
  the line defined by ?point and ?axis.
  The one translational degree of freedom is unaffected by this action.


---
PFT entry: (2,1,parallel-z)
---

Initial status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Branch variables:
  q_0, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = line(?point, ?axis);
  R[1] = vec-sum(?point, gmz(?M_1));
  R[2] = vec-sum(?point, gmz(?M_2));
  R[3] = perp-base(R[1], R[0]);
  R[4] = perp-base(R[2], R[0]);

  unless equal?(R[3], R[4])
    error(R[3]-R[4]), estring[8]);

  R[5] = vec-diff(R[1], R[3]);
  R[6] = vec-diff(R[2], R[4]);
  R[7] = vec-angle(R[6], R[5], ?axis);

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[7])
    else 2r/a(?geom, ?point, R[7], ?axis, ?axis_1, ?axis_2);
  end;

New status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has one rotational and two translational degrees of freedom.
  Therefore ?geom must be rotated about its known point and know axis.
  The z-axes of the two markers must have the same projection onto
  the line defined by ?point and ?axis.
  The translational degrees of freedom are unaffected by this action.


---
PFT entry: (3,1,parallel-z)
---

Initial status:
  3-TDOF(?geom)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = line(gmp(?M_1), ?axis);
  R[1] = vec-sum(gmp(?M_2), gmz(?M_1));
  R[2] = vec-sum(gmp(?M_2), gmz(?M_2));
  R[3] = perp-base(R[1], R[0]);
  R[4] = perp-base(R[2], R[0]);

  unless equal?(R[3], R[4])
    error(R[3]-R[4]), estring[8]);

  R[5] = vec-diff(R[1], R[3]);
  R[6] = vec-diff(R[2], R[4]);
  R[7] = vec-angle(R[6], R[5], ?axis);

  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, gmp(?M_2), ?axis, R[7])
    else 2r/a(?geom, gmp(?M_2), R[7], ?axis, ?axis_1, ?axis_2);
  end;

New status:
  3-TDOF(?geom)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has one rotational and three translational degrees of freedom.
  The ?geom is rotated about marker ?M_2 and the known rotational axis.
  The z-axes of the two markers must have the same projection onto
  the line defined by ?M_2 and ?axis.
  The translational degrees of freedom are unaffected by this action.


---
PFT entry: (0,2,parallel-z)
---

Initial status:
  0-TDOF(?geom, ?point)
  2-RDOF(?geom, ?axis_1, ?axis_2)

Branch variables:
  q_0, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = vec-sum(?point, gmz(?M_1));
  R[1] = vec-sum(?point, gmz(?M_2));
  2r/p-p(?geom, ?point,
    R[1], R[0],
    ?axis_1, ?axis_2, q_0);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has two rotational degrees of fredom.
  Therefore it must be rotated about ?point and axes ?axis_1 and ?axis_2.


---
PFT entry: (0,3,parallel-z)
---

Initial status:
  0-TDOF(?geom, ?point)
  3-RDOF(?geom)

Plan fragment:
  begin
  R[0] = cross-prod(gmz(?M_1), gmz(?M_2));
  R[1] = vec-angle(gmz(?M_1), gmz(?M_2), R[0]);

  rotate(?geom, ?point, R[0], R[1])

  R[2] = gmz(?M_1);
  end;

New status:
  0-TDOF(?geom, ?point)
  1-RDOF(?geom, R[2], nil, nil)

Explanation:
  All rotational degrees of freedom are available for ?geom.
  Thus, the action will be a pure rotation.
  The angle between the two marker z-axes is measured,
  and the geom is rotated about an axis equal to the
  cross product of the two z-axes.


---
PFT entry: (1,3,parallel-z)
---

Initial status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  3-RDOF(?geom)

Branch variables:
  q_0, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = cross-prod(gmz(?M_1), gmz(?M_2));
  R[1] = vec-angle(gmz(?M_1), gmz(?M_2), R[0]);

  rotate(?geom, ?point, R[0], R[1])

  R[2] = gmz(?M_1);
  end;

New status:
  0-TDOF(?geom, ?point, ?line, ?lf)
  1-RDOF(?geom, R[2], nil, nil)

Explanation:
  All rotational degrees of freedom are available for ?geom.
  Thus, the action will be a pure rotation.
  The angle between the two marker z-axes is measured,
  and the geom is rotated about an axis equal to the
  cross product of the two z-axes.


---
PFT entry: (2,3,parallel-z)
---

Initial status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  3-RDOF(?geom)

Branch variables:
  q_0, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = cross-prod(gmz(?M_1), gmz(?M_2));
  R[1] = vec-angle(gmz(?M_1), gmz(?M_2), R[0]);

  rotate(?geom, ?point, R[0], R[1])

  R[2] = gmz(?M_1);
  end;

New status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  1-RDOF(?geom, R[2], nil, nil)

Explanation:
  All rotational degrees of freedom are available for ?geom.
  Thus, the action will be a pure rotation.
  The angle between the two marker z-axes is measured,
  and the geom is rotated about an axis equal to the
  cross product of the two z-axes.



---
PFT entry: (3,3,parallel-z)
---

Initial status:
  3-TDOF(?geom)
  3-RDOF(?geom)

Plan fragment:
  begin
  begin
  R[0] = cross-prod(gmz(?M_1), gmz(?M_2));
  R[1] = vec-angle(gmz(?M_1), gmz(?M_2), R[0]);

  rotate(?geom, ?point, R[0], R[1])

  R[2] = gmz(?M_1);
  end;

New status:
  3-TDOF(?geom)
  2-RDOF(?geom, R[2], nil, nil)

Explanation:
  All rotational degrees of freedom are available for ?geom.
  Thus, the action will be a pure rotation.
  The angle between the two marker z-axes is measured,
  and the geom is rotated about an axis equal to the
  cross product of the two z-axes.

