(ns isis.geom.action.in-line-fixed-slice
  "The table of rules for the in-line constraint where
  the point marker is FIXED and the line is mobile."
  (:require [clojure.pprint :as pp]
            [isis.geom.position-dispatch :as ms]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))

(def slicer "in-line-fixed")

(defn assemble!->t0-r0
"PFT entry: (0,0,in-line)  (M_1 is fixed)

Initial status:
  0-TDOF(?m1-link, ?m1-point)
  0-RDOF(?m1-link)

  0-TDOF(?m2-link, ?m2-point)
  0-RDOF(?m2-link)

Plan fragment:
  begin
  R[0] = line(gmp(?M_1), gmp(?M_2));
  R[1] = perp-dist(gmp(?M_1), R[0]);
  unless zero?(R[1])
    error(R[1], estring[9]);
  end;

New status: unchanged

Explanation:
  Geom ?m2-link is fixed, so the in-line constraint
  can only be checked for consistency. "
  [kb m1 m2]
  :consistent)


(defn assemble!->t0-r1 [kb m1 m2]  (ms/unimpl :t0-r1 slicer kb m1 m2))
(defn assemble!->t0-r2 [kb m1 m2]  (ms/unimpl :t0-r2 slicer kb m1 m2))
(defn assemble!->t0-r3 [kb m1 m2]  (ms/unimpl :t0-r3 slicer kb m1 m2))

(defn assemble!->t1-r0 [kb m1 m2]  (ms/unimpl :t1-r0 slicer kb m1 m2))
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unimpl :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unreal :t1-r2 slicer kb m1 m2))
(defn assemble!->t1-r3 [kb m1 m2] :no-application)

(defn assemble!->t2-r0 [kb m1 m2]  (ms/unimpl :t2-r0 slicer kb m1 m2))

(defn assemble!->t2-r1
  " PFT entry: (2,1,in-line)  (?M_1 is fixed)

Initial status:
  0-TDOF(?m1-link, ?m1-point)
  0-RDOF(?m1-link)

  2-TDOF(?m2-link, ?m2-point, ?m2-plane, ?m2-lf)
  1-RDOF(?m2-link, ?m2-axis, ?m2-axis_1, ?m2-axis_2)

Branch variables:
  q_0, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = line(gmp(?M_2), gmz(?M_2));
  R[1] = plane(gmp(?M_1), normal(?m2-plane));
  R[2] = intersect(R[0], R[1], 0);
  unless R[2]
    R[2] = gmp(?M_2)

  2t-1r/p-p(?m2-link, ?m2-point, ?m2-plane,
    ?m2-axis, ?m2-axis_1, ?m2-axis_2,
    R[2], gmp(?M_1), ?lf, q_0);

  R[3] = plane(?m2-point, ?m2-axis);
  R[4] = intersect(R[0], R[3], 0);
  R[5] = vec-diff(?m2-point, R[4]);
  R[6] = cylinder(R[0], ?m2-axis, mag(R[5]));
  R[7] = intersect(R[6], ?m2-plane, 0);
  R[8] = a-point(R[7]);
  R[9] = perp-dist(R[8], ?m2-point),
  R[10] = ellipse-+-r(R[7], ?m2-axis, R[9]);
  end;

New status:
  h-TDOF(?m2-link, R[8], R[10], R[8])
  h-RDOF(?m2-link, ?m2-axis, ?m2-axis_1, ?m2-axis_2)

Explanation:
  Geom ?m2-link has one rotational and two translational degrees of freedom.
  Therefore it must be translated along its
  known ?m2-plane and rotated about its known ?m2-axis.
  This effect is achieved by translating ?m2-link to make the
  markers coincident, and then attempting to move ?m2-point back onto ?m2-plane.
  In general, there are two distinct solutions to this problem,
  so a branch variable q_0 is used to select the desired solution."
  [kb m1 m2]
  #_(let [[[m2-link-name m2-proper-name] _] m2
        m2-link (get-in kb [:link m2-link-name])
        m2-point (:point @m2-link)
        m2-plane (:plane @m2-link)

        m1-gmp (ga/gmp m1 kb)
        m2-gmp (ga/gmp m2 kb)
        m2-gmz (ga/gmz m2 kb)

        m2-line (ga/line m2-gmp m2-gmz)
        or-plane (ga/plane m1-gmp (ga/normal m2-plane))
        on-point (ga/meet m2-line or-plane)
        ;; on-point (if on-point on-point m2-gmp)

        ;; t2-r1:p->p:pnt


        reject (ga/rejection m1-gmp m2-plane) ]
    (dosync
     (alter m2-link merge
            (ga/translate @m2-link ga/vec-sum reject))
     (alter m2-link assoc
            :tdof {:# 2
                   :point m1-gmp
                   :plane (ga/gmp m2 kb)}
            :rdof {:# 3 } ) ))
  :progress-made)


(defn assemble!->t2-r2 [kb m1 m2]  (ms/unreal :t2-r2 slicer kb m1 m2))

(defn assemble!->t2-r3 [kb m1 m2] :no-application)

(defn assemble!->t3-r0 [kb m1 m2]  (ms/unimpl :t3-r0 slicer kb m1 m2))
(defn assemble!->t3-r1 [kb m1 m2]  (ms/unimpl :t3-r1 slicer kb m1 m2))
(defn assemble!->t3-r2 [kb m1 m2]  (ms/unreal :t3-r2 slicer kb m1 m2))
(defn assemble!->t3-r3 [kb m1 m2]  (ms/unimpl :t3-r3 slicer kb m1 m2))

