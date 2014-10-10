(ns isis.geom.action.parallel-z-slice
  "The table of rules."
  (:require [clojure.pprint :as pp]
            [isis.geom.position-dispatch :as ms]
            [isis.geom.machine [tolerance :as tol]]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))

(def slicer "parallel-z-slice")

(defn assemble!->t0-r0 [kb m1 m2] :consistent)
(defn assemble!->t0-r1 [kb m1 m2]  (ms/unimpl :t0-r1 slicer kb m1 m2))
(defn assemble!->t0-r2 [kb m1 m2]  (ms/unimpl :t0-r2 slicer kb m1 m2))
(defn assemble!->t0-r3 [kb m1 m2]  (ms/unimpl :t0-r3 slicer kb m1 m2))

(defn assemble!->t1-r0 [kb m1 m2] :consistent)
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unimpl :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unreal :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r3 [kb m1 m2]  (ms/unimpl :t1-r3 slicer kb m1 m2))

(defn assemble!->t2-r0 [kb m1 m2] :consistent)
(defn assemble!->t2-r1 [kb m1 m2]  (ms/unimpl :t2-r1 slicer kb m1 m2))
(defn assemble!->t2-r2 [kb m1 m2]  (ms/unreal :t2-r1 slicer kb m1 m2))

(defn assemble!->t2-r3
  "PFT entry: (2,3,parallel-z)

  Initial status:
  0-TDOF(?m1-link)
  0-RDOF(?m1-link)

  2-TDOF(?m2-link, ?m2-point, ?m2-plane, ?lf)
  3-RDOF(?m2-link)

  Branch variables:
  q_0, denoting a 2-way branch

  Plan fragment:
  begin
  R[0] = outer-prod(gmz(?M_1), gmz(?M_2));
  R[1] = vec-angle(gmz(?M_1), gmz(?M_2), R[0]);

  rotate(?m2-link, ?m2-point, R[0], R[1])

  R[2] = gmz(?M_1);
  end;

  New status:
  2-TDOF(?m2-link, ?m2-point, ?m2-plane, ?lf)
  1-RDOF(?m2-link, R[2], nil, nil)

  Explanation:
  All rotational degrees of freedom are available for ?m2-link.
  Thus, the action will be a pure rotation.
  The angle between the two marker z-axes is measured,
  and the geom is rotated about an axis equal to the
  cross product of the two z-axes. "
  [kb m1 m2]
  (let [ [[m2-link-name m2-proper-name] _] m2
         m2-link (get-in kb [:link m2-link-name])
         m2-point (get-in @m2-link [:tdof :point])
         m2-plane (get-in @m2-link [:tdof :plane])
         gmz1 (ga/gmz m1 kb)
         gmz2 (ga/gmz m2 kb)
         axis (ga/outer-prod gmz1 gmz2)
         angle (ga/vec-angle gmz1 gmz2 axis) ]
    (dosync
     (alter m2-link merge
            (ga/rotate @m2-link m2-point
                       axis
                       angle))
     (alter m2-link assoc
            :tdof {:# 2
                   :point m2-point
                   :plane m2-plane}
            :rdof {:# 1
                   :dir (ga/gmz m1 kb)} ) ))
  :progress-made)

(defn assemble!->t3-r0 [kb m1 m2] :consistent)

(defn assemble!->t3-r1
  "
  PFT entry: (3,1,parallel-z)

  Initial status:
  0-TDOF(?m1-link)
  0-RDOF(?m1-link)

  3-TDOF(?m2-link)
  1-RDOF(?m2-link, ?m2-axis, ?m2-axis_1, ?m2-axis_2)

  Plan fragment:
  begin
  R[0] = line(gmp(?M_1), ?m2-axis);
  R[1] = vec-sum(gmp(?M_2), gmz(?M_1));
  R[2] = vec-sum(gmp(?M_2), gmz(?M_2));
  R[3] = perp-base(R[1], R[0]);
  R[4] = perp-base(R[2], R[0]);

  unless equal?(R[3], R[4])
  error(R[3]-R[4]), estring[8]);

  R[5] = vec-diff(R[1], R[3]);
  R[6] = vec-diff(R[2], R[4]);
  R[7] = vec-angle(R[6], R[5], ?axis);

  if null?(?m2-axis_1) and null?(?m2-axis_2)
  then rotate(?m2-link, gmp(?M_2), ?m2-axis, R[7])
  else 2r/a(?m2-link, gmp(?M_2), R[7], ?m2-axis, ?m2-axis_1, ?m2-axis_2);
  end;

  New status:
  3-TDOF(?m2-link)
  0-RDOF(?m2-link)

  Explanation:
  Geom ?m2-link has one rotational and three translational degrees of freedom.
  The ?m2-link is rotated about marker ?M_2 and the known rotational axis.
  The z-axes of the two markers must have the same projection onto
  the line defined by ?M_2 and ?m2-axis.
  The translational degrees of freedom are unaffected by this action. "
  [kb m1 m2]
  (let [ [[m2-link-name m2-proper-name] _] m2
         m2-link (get-in kb [:link m2-link-name])

         m2-axis (get-in @m2-link [:rdof :axis])
         m2-axis-1 (get-in @m2-link [:rdof :axis-1])
         m2-axis-2 (get-in @m2-link [:rdof :axis-2])

         gmp1 (ga/gmp m1 kb)
         gmp2 (ga/gmp m2 kb)

         gmz1 (ga/gmz m1 kb)
         gmz2 (ga/gmz m2 kb)

         axis (ga/line gmp1 m2-axis)
         from-pnt (ga/vec-sum gmp2 gmz1)
         to-pnt (ga/vec-sum gmp2 gmz2)

         from-base (ga/projection from-pnt axis)
         to-base (ga/projection to-pnt axis)

         ;; check the sizes are the same
         ;; unless equal?(R[3], R[4])
         ;; error(R[3]-R[4]), estring[8]);

         from-dir (ga/vec-diff from-pnt from-base)
         to-dir (ga/vec-diff to-pnt to-base)

         angle (ga/vec-angle to-dir from-dir (:d axis)) ]

    (dosync
     (if (every? nil? [m2-axis-1 m2-axis-2])
       (alter m2-link merge
              (ga/rotate @m2-link gmp1 (:d axis) angle))
       (dof/r2:a m2-link gmp2 angle m2-axis m2-axis-1 m2-axis-2))

     (invariant/set-marker! kb [m2-link-name m2-proper-name] :dir)
     (alter m2-link assoc
            :tdof {:# 3}
            :rdof {:# 0} ) ))
  :progress-made)


(defn assemble!->t3-r2 [kb m1 m2]  (ms/unreal :t3-r2 slicer kb m1 m2))

(defn assemble!->t3-r3
  "
  PFT entry: (3,3,parallel-z)

  Initial status:
  0-TDOF(?m1-link)
  0-RDOF(?m1-link)

  3-TDOF(?m2-link)
  3-RDOF(?m2-link)

  Plan fragment:
  begin
  R[0] = outer-prod(gmz(?M_1), gmz(?M_2));
  R[1] = vec-angle(gmz(?M_1), gmz(?M_2), R[0]);

  rotate(?m2-link, gmp(?M_1), R[0], R[1])

  R[2] = gmz(?M_1);
  end;

  New status:
  3-TDOF(?m2-link)
  2-RDOF(?m2-link, R[2], nil, nil)

  Explanation:
  All rotational degrees of freedom are available for ?m2-link.
  Thus, the action will be a pure rotation.
  The angle between the two marker z-axes is measured,
  and the geom is rotated about an axis
  perpendicular to the two z-axes."
  [kb m1 m2]
  (let [ [[m2-link-name m2-proper-name] _] m2
         m2-link (get-in kb [:link m2-link-name])

         gmp1 (ga/gmp m1 kb)

         gmz1 (ga/gmz m1 kb)
         gmz2 (ga/gmz m2 kb)

         axis (ga/outer-prod gmz1 gmz2)
         angle (ga/vec-angle gmz1 gmz2 axis) ]

    (dosync
     (alter m2-link merge
            (ga/rotate @m2-link gmp1
                       axis angle))
     (alter m2-link assoc
            :tdof {:# 3}
            :rdof {:# 1
                   :axis (ga/gmz m1 kb)} ) ))
  :progress-made)


