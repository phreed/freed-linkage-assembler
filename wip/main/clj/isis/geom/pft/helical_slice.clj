"
---
PFT entry: ALL
---

Constraint:
  helical(?M_1, ?M_2, ?pitch)

Assumptions:
  marker-has-invariant-z(?M_1)
  marker-has-invariant-z(?M_2)
  marker-has-invariant-x(?M_1)
  geom-has_marker(?link, ?M_2)


---
PFT entry: (0,0,helical)
---

Initial status:
  0-TDOF(?link, ?point)
  0-RDOF(?link)

Plan fragment:
  begin
  R[0] = line(gmp(?M_2), gmp(?M_2));
  R[1] = screw(R[0], gmp(?M_2), ?pitch);
  R[2] = pc-check(R[1], 0, 0, 0);
  unless zero?(R[2])
    error(R[0], estring[9]);
  end;

New status:
  0-TDOF(?link, ?point)
  0-RDOF(?link)

Explanation:
  Geom ?link has no degrees of freedom,
  so the helical constraint can only be checked for consistency.


---
PFT entry: (1,0,helical)
---

Initial status:
  1-TDOF(?link, ?point, ?line, ?lf)
  0-RDOF(?link)

Plan fragment:
  begin
  R[0] = screw(?line, gmp(?M_2), ?pitch);
  R[1] = pc-check(R[0], nil, 0, 0);
  unless equal?(?point, ?lf)
    then translate(?link, R[1])
    else translate(?link, -R[1]);
  end;

New status:
  0-TDOF(?link, ?point)
  0-RDOF(?link)

Explanation:
  Geom ?link has one translational degree of freedom,
  which is restricted by the helical constraint.



---
PFT entry: (h,h,helical)
---

Initial status:
  h-TDOF(?link, ?point, ?line, ?lf)
  h-RDOF(?link, ?axis, ?axis_1, ?axis_2)

Branch variables:
  q_0, denoting a potenially infinite branch

Plan fragment:
  begin
  R[0] = line(gmp(?M_2), gmz(?M_2));
  R[1] = screw(R[0], gmp(?M_2), ?pitch);
  R[2] = pc-locus(R[1], gmp(?M_1), gmp(?M_1));
  R[3] = pc-locus(?line, ?point, gmp(?M_1));
  R[4] = intersect(R[2], R[3], q_0);

  unless point?(R[4])
    error(perp-dist(R[2], R[3]), estring[7]);

  R[5] = vec-diff(R[4], gmp(?M_1));
  translate(?link, R[5]);
  R[6] = pc-check(?line, R[5], nil, 0);

  if equal?(?point, ?lf)
    then begin
      R[7] = ?axis;
      R[8] = R[6];
    end
    else begin
      R[7] = R[6];
      R[8] = ?axis;
    end
  if null?(?axis_1) and null?(?axis_2)
    then rotate(?link, ?point, R[7], R[8])
    else 2r/a(?link, ?point, R[7], R[8], ?axis_1, ?axis_2);
  end;

New status:
  0-TDOF(?link, ?point)
  0-RDOF(?link)

Explanation:
  Geom ?link has a single combined degree of freedom;
  in this case, the helical constraint can only be
  checked for consistency.


---
PFT entry: (0,1,helical)
---

Initial status:
  0-TDOF(?link, ?point)
  1-RDOF(?link, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = line(gmp(?M_2), gmz(?M_2));
  R[1] = screw(R[0], gmp(?M_2), ?pitch);
  R[2] = pc-check(R[1], nil, 0, 0);
  translate(?link, R[1]);
  end;

New status:
  0-TDOF(?link, ?point)
  0-RDOF(?link)

Explanation:
  Geom ?link has one translational degree of freedom,
  which is restricted by the helical constraint.


---
PFT entry: (1,1,helical)
---

Initial status:
  1-TDOF(?link, ?point, ?line, ?lf)
  1-RDOF(?link, ?axis, ?axis_1, ?axis_2)

Plan fragment:
  begin
  R[0] = line(gmp(?M_2), gmz(?M_2));
  R[1] = vec-angle(R[0], ?line,
    outer-prod(R[0], ?line));
  unless zero?(R[1])
    error(R[1], estring[9]);

  R[2] = perp-dist(gmp(?M_1), ?line);
  unless zero?(R[2])
    error(R[2], estring[9]);

  R[3] = screw(R[0], gmp(?M_2), ?pitch);
  R[4] = pc-check(R[3], nil, 0, 0);

  R[2] = pc-check(R[1], nil, 0, 0);
  translate(?link, R[1]);
  R[0] = vec-angle(gmx(?M_2), gmx(?M_1), ?axis);
  R[1] = R[0] - it ?a;

  if eqal?(?point, ?lf)
    then translate(?link, R[4])
    else translate(?link, -R[4]);
  end;

New status:
  h-TDOF(?link, ?point, r, r)
  h-RDOF(?link, ?axis, ?axis_1, ?axis_2)

Explanation:
  Geom ?link has one translational and one rotational degree of freedom.
  These are reduced to a single coupled degree of freedom by the helical constraint.

"
