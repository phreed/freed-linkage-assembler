
---
PFT entry: ALL
---

Constraint:
  offset-z(?M_1, ?M_2, ?a)

Assumptions:
  marker-has-invariant-z(?M_1)
  geom-has_marker(?geom, ?M_2)


---
PFT entry: (0,0,offset-z)
---

Initial status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Plan fragment:
  begin
  R[0] = dot-product(gmz(?M_1), gmz(?M_2));
  R[1] = cos(it ?a)
  unless zero?(R[0] - R[1])
    error(R[0] - R[1], estring[9]);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has no rotational degrees of freedom,
  so the offset-z constraint can only be checked for consistency.


---
PFT entry: (0,1,offset-z)
---

Initial status:
  0-TDOF(?geom, ?point)
  1-RDOF(?geom, ?axis, ?axis_1, ?axis_2)

Branch variables:
  q_0, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = line(?point, ?axis);
  R[1] = vec-sum(?point, gmz(?M_2));
  R[2] = perp-base(R[1], R[0]);
  R[3] = vec-diff(R[1], R[2]);

  R[4] = circle(R[6], ?axis, mag(R[3]));

  R[5] = vec-scale(gmz(?M_1), cos(it ?a));
  R[6] = vec-sum(?point, R[5]);
  R[7] = circle(R[6], gmz(?M_1), sin(it ?a));

  R[8] = intersect(R[4], R[7], q_0);

  unless point?(R[8])
    error(perp-dist(R[4], R[7]), estring[8]);

  1r/p-p(?geom, ?point, R[1], R[8],
    ?axis, ?axis_1, ?axis_2);
  end;

New status:
  0-TDOF(?geom, ?point)
  0-RDOF(?geom)

Explanation:
  Geom ?geom has only one rotational degree of freedom.
  So, it must be rotated about its known point and known axis.



---
PFT entry: (0,2,offset-z)
---

Initial status:
  0-TDOF(?geom, ?point)
  2-RDOF(?geom, ?axis_1, ?axis_2)


Explanation:
  Requires numerical solution.
  No analytic solution known.


---
PFT entry: (0,3,offset-z)
---

Initial status:
  0-TDOF(?geom, ?point)
  3-RDOF(?geom)

Plan fragment:
  begin
  R[0] = vec-scale(gmz(?M_1), cos(it ?a));
  R[1] = vec-sum(?point, R[0]);
  R[2] = circle(R[1], gmz(?M_1), sin(it ?a));

  R[3] = a-point(R[2]);
  R[4] = vec-sum(?point, gmz(?M_2));

  3r/p-p(?geom, ?point, R[4], R[3]);
  R[5] = gmz(?M_1);
  R[6] = gmz(?M_2);
  end;

New status:
  0-TDOF(?geom, ?point)
  2-RDOF(?geom, R[5], R[6])

Explanation:
  All rotational degrees of freedom are available for ?geom.
  Thus, the action will be a pure rotation.
  The angle between the two marker z-axes is measured,
  and the geom is rotated to give the axes an
  angular separation of it ?a.


