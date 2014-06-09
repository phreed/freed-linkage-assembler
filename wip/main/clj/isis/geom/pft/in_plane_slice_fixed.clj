"
---
PFT entry: ALL
---

Constraint:
  in-plane(?M_1, ?M_2)

---
PFT entry: (?M_1 is fixed)
---

Assumptions:
  marker-has-invariant-position(?M_1)
  geom-has_marker(?geom, ?M_2)


---
PFT entry: (0,0,in-plane)  (M_1 is fixed)
---

Initial status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = vec-diff(gmp(?M_2), gmp(?M_1));
  R[1] = dot-prod(R[0], gmp(?M_1));
  unless zero?(R[1])
    error(R[1], estring[9]);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom is fixed, so the in-plane constraint
  can only be checked for consistency.


---
PFT entry: (1,0,in-plane) (?M_1 is fixed)
---

Initial status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = axis(?line);
  R[1] = line(gmp(?M_2), gmz(?M_2));
  R[2] = line(gmp(?M_1), R[0]);
  R[3] = intersect(R[1], R[2], 0);
  unless point?(R[3])
    error(perp-dist(R[1], R[2]),
    estring[7]);
  translate(?geom,
    vec-diff(gmp(?M_1), R[3]));
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom cannot rotate.
  Therefore, the in-plane constraint must be satisfied by a translation.
  In the degenerate case, the plane defined by ?M_2 is parallel to ?line,
  which causes inaccuracies in the degrees of freedom tally.


---
PFT entry: (2,0,in-plane) (?M_1 is fixed)
---

Initial status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  0-RDOF(?geom)

Explanation:
  This entry has no application.


---
PFT entry: (3,0,in-plane) (?M_1 is fixed)
---

Initial status:
  3-TDOF(?geom)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = plane(gmp(?M_2), gmz(?M_2));
  R[1] = perp-base(gmp(?M_1), R[0]),
  R[2] = vec-diff(gmp(?M_1), R[1]);
  translate(?geom, R[2]);
  R[3] = gmp(?M_1);
  end;

New status:
  1-TDOF(?geom, R[3], R[0], R[0])
  0-RDOF(?geom)

Explanation:
  Geom ?geom is free to translate,
  so the translation vector is measured and the geom is moved.
  No checks are required.

---
PFT entry: (h,h,in-plane) (?M_1 is fixed)
---

Initial status:
  h-TDOF(?geom, ?point, ?line, ?point)
  h-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Branch variables:
  q_0, denoting a potentially infinite branch

Plan fragment:
  begin
  R[0] = plane(gmp(?M_2), gmz(?M_2));
  R[1] = pc-locus(?line, ?point, gmp(?M_1));
  R[2] = intersect(R[1], R[0], q_0);
  unless point?(R[2])
    error(perp-dist(R[1], R[0]),
    estring[7]);
  R[3] = centerline(?line);
  R[4] = perp-base(R[2], R[3]);
  R[5] = vec-diff(R[2], R[3]);
  R[6] = perp-base(gmp(?M_1, R[3]);
  R[7] = vec-diff(gmp(?M_1), R[3]);
  translate(?geom, vec-diff(R[6], R[4]));
  R[8] = vec-angle(R[5], R[7], ?axis);
  if null?(?axis_1) and null?(?axis_2)
    then rotate(?geom, ?point, ?axis, R[8])
    else 2r/a(?geom, ?point, R[8],
      ?axis, ?axis_1, ?axis_2);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Points on ?geom are confined to a helix.
  Geom ?geom is translated and rotated, both
  along and about the screw axis.
  First, the point on the line defimed by ?M_2
  and its z-axis which will coincide with ?M_1 is found;
  then, the geom is moved to make that point coincident.


---
PFT entry: (0,1,in-plane) (?M_1 is fixed)
---

Initial status:
  0-TDOF(?geom, ?point)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Branch variables:
  q_0, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = line(?point, ?axis);
  R[1] = perp-base(gmp(?M_1), R[0]);
  R[2] = vec-diff(gmp(?M_1), R[1]);
  R[3] = circle(R[1], ?axis, mag(R[2]));
  R[4] = plane(gmp(?M_2), gmz(?M_2));
  R[2] = intersect(R[3], R[4], q_0);
  unless point?(R[5])
    error(perp-dist(R[3], R[4]),
    estring[4]);
  1r/p-p(?geom, ?point, R[5], gmp(?M_1),
    ?axis, ?axis_1, ?axis_2);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has only one rotational degree of freedom.
  Therefore it must be rotated about its known point and known axis.
  First, the point on the line defined by ?M_2 and its z-axis
  which will coincide with ?M_1 is found;
  then, the geom is rotated to make that point coincident.
  In general, there are two distinct solutions to this problem,
  so a branch variable q_0 is used to select the desired solution.


---
PFT entry: (1,1,in-plane) (?M_1 is fixed)
---

Initial status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = line(?point, ?axis_1);
  R[0] = perp-dist(gmp(?M_1), R[0]);
  R[2] = cylinder(?line, ?axis, mag(R[1]));
  R[3] = plane(gmp(?M_2), gmz(?M_2));
  R[4] = intersect(R[2], R[3], 0);
  unless point?(R[4])
    error(perp-dist(R[2], R[3]),
    estring[6]);
  R[5] = a-point(R[4]);
  1t-1r/p-p(?geom, ?point, ?line,
    ?axis, ?axis_1, ?axis_2,
    R[5], gmp(?M_1), ?lf, 0);
  R[6] = ellipse+r(R[4], gmp(?M_2), R[5]);
  end;

New status:
  h-TDOF(?geom, R[5], R[7], R[7] )
  h-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Explanation:
  Geom ?geom has only one rotational and one translational degree of freedom.
  First, the point on the plane defined by ?M_2 and its z-axis which
  will coincide with ?M_1 is found;
  then, the geom is translated and rotated to make that
  point coincident with ?M_1.
  A single degree of freedom combining rotation and translation remains.


---
PFT entry: (2,1,in-plane)  (?M_1 is fixed)
---

Initial status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)


Explanation:
  This entry has no application.


---
PFT entry: (3,1,in-plane) (?M_1 is fixed)
---

Initial status:
  3-TDOF(?geom)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = plane(gmp(?M_2), gmz(?M_2));
  R[1] = perp-dist(gmp(?M_1), R[0]);
  R[2] = vec-diff(gmp(?M_1), R[1]);
  translate(?geom, R[2]);
  R[3] = gmp(?M_2);
  end;

New status:
  2-TDOF(?geom, R[2], R[0], R[0])
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Explanation:
  Geom ?geom is free to translate, so the translation
  vector is measured and the geom is moved.
  No checks are required.


---
PFT entry: (0,2,in-plane) (?M_1 is fixed)
---

Initial status:
  0-TDOF(?geom, ?point)
  2-RDOF(?geom, ?axis_1, ?axis_2)

Branch variables:
  q_0, denoting a 2-way branch
  q_1, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = vec-diff(gmp(?M_1), ?point);
  R[1] = sphere(?point, mag(R[0]));
  R[2] = plane(gmp(?M_2), gmz(?M_2));
  R[3] = intersect(R[1], R[2], q_0);
  unless circle?(R[3])
    error(perp-dist(R[1], R[2]),
    estring[7]);

  2r/p-p(?geom, ?point,
    R[3], gmp(?M_1),
    ?axis_1, ?axis_2, q_1);

  R[4] = perp-base(?point, R[2]);
  R[5] = vec-diff(R[4], ?point);
  end;


New status:
  0-TDOF(?geom, ?point)
  1-RDOF(?geom, R[5], ?axis_1, ?axis_2)

Explanation:
  Geom ?geom has two rotational degrees of fredom.
  The line defined by marker ?M_2 and its z-axis is
  constructed, and the point that will coincide with
  marker ?M_1 is found.
  Then, ?geom is rotated to put the point into
  coincidence with ?M_1.
  After this action, a single degree of freedom remains.


---
PFT entry: (0,3,in-plane) (?M_1 is fixed)
---

Initial status:
  0-TDOF(?geom, ?point)
  3-RDOF(?geom)

Plan fragment:
  begin
  R[0] = vec-diff(gmp(?M_1), ?point);
  R[1] = sphere(?point, mag(R[0]));
  R[2] = plane(gmp(?M_2), gmz(?M_2));
  R[3] = intersect(R[1], R[2], q_0);
  unless circle?(R[3])
    error(perp-dist(R[1], R[2]),
    estring[7]);

  R[4] = a-point(R[3]);

  3r/p-p(?geom, ?point, R[4], gmp(?M_1));

  R[5] = perp-base(?point, R[2]);
  R[6] = vec-diff(R[5], ?point);
  R[7] = vec-diff(?point, gmp(?M_1));
  end;

New status:
  0-TDOF(?geom, ?point)
  2-RDOF(?geom, R[6], R[7])

Explanation:
  Geom ?geom cannot translate, so the in-plane
  constraint is satisfied by a rotation.
  There remain two rotational degrees of freedom.


---
PFT entry: (1,3,in-plane) (?M_1 is fixed)
---

Initial status:
  1-TDOF(?geom, ?point, ?line, ?lf)
  3-RDOF(?geom)

Explanation:
  This entry has no application.


---
PFT entry: (2,3,in-plane) (?M_1 is fixed)
---

Initial status:
  2-TDOF(?geom, ?point, ?plane, ?lf)
  3-RDOF(?geom)

Explanation:
  This entry has no application.



---
PFT entry: (3,3,in-plane) (?M_1 is fixed)
---

Initial status:
  3-TDOF(?geom)
  3-RDOF(?geom)

Plan fragment:
  begin
  R[0] = plane(gmp(?M_2), gmz(?M_2));
  R[1] = perp-dist(gmp(?M_1), R[0]);
  R[2] = vec-diff(gmp(?M_1), R[1]);
  translate(?geom, R[2]);
  R[3] = gmp(?M_1);
  end;

New status:
  2-TDOF(?geom, R[3], R[0], R[0])
  3-RDOF(?geom)

Explanation:
  Geom ?geom is free to translate, so the translation
  vector is measured and the ?geom is moved.
  No checks are required.

"
