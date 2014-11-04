(ns isis.geom.action.in-line-p2o-slice
  "The table of rules for the in-line constraint where
  the point marker is MOBILE and the line is fixed."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))

(def slicer "in-line-mobile")

(defn assemble!->t0-r0 [kb m1 m2]  (ms/unimpl :t0-r0 slicer kb m1 m2))
(defn assemble!->t0-r1 [kb m1 m2]  (ms/unimpl :t0-r1 slicer kb m1 m2))
(defn assemble!->t0-r2 [kb m1 m2]  (ms/unimpl :t0-r2 slicer kb m1 m2))
(defn assemble!->t0-r3 [kb m1 m2]  (ms/unimpl :t0-r3 slicer kb m1 m2))

(defn assemble!->t1-r0 [kb m1 m2]  (ms/unimpl :t1-r0 slicer kb m1 m2))
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unimpl :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unimpl :t1-r2 slicer kb m1 m2))
(defn assemble!->t1-r3 [kb m1 m2]  (ms/unimpl :t1-r3 slicer kb m1 m2))

(defn assemble!->t2-r0 [kb m1 m2]  (ms/unimpl :t2-r0 slicer kb m1 m2))

(defn assemble!->t2-r1
  " PFT entry: (2,1,in-line)  (?M_1 moves)

Initial status:

  2-TDOF(?m1-link, ?m1-point, ?m1-plane, ?m1-lf)
  1-RDOF(?m1-link, ?m1-axis, ?m1-axis_1, ?m1-axis_2)

  0-TDOF(?m2-link, ?m2-point)
  0-RDOF(?m2-link, ?m2-axis, ?m2-angle)

Branch variables:
  q_0, denoting a 2-way branch

Plan fragment:
  begin
  R[0] = line(gmp(?M_2), gmz(?M_2));
  R[1] = plane(gmp(?M_1), normal(?m1-plane));
  R[2] = intersect(R[0], R[1], 0);
  unless R[2]
    R[2] = gmp(?M_2)

  2t-1r/p-p(?m1-link, ?m1-point, ?m1-plane,
    ?m1-axis, ?m1-axis_1, ?m1-axis_2,
    gmp(?M_0), R[2], ?lf, q_0);

  R[3] = plane(?m1-point, ?m1-axis);
  R[4] = intersect(R[0], R[3], 0);
  R[5] = vec-diff(?m1-point, R[4]);
  R[6] = cylinder(R[0], ?m1-axis, mag(R[5]));
  R[7] = intersect(R[6], ?m1-plane, 0);
  R[8] = a-point(R[7]);
  R[9] = perp-dist(R[8], ?m1-point),
  R[10] = ellipse-+-r(R[7], ?axis, R[9]);
  end;

New status:
  h-TDOF(?m1-link, R[8], R[10], R[8])
  h-RDOF(?m1-link, ?m1-axis, ?m1-axis_1, ?m1-axis_2)

Explanation:
  Geom ?m1-link has one rotational and two translational degrees of freedom.
  (a book flat on a table)
  Therefore it must be translated along its
  known ?m1-plane and rotated about its known ?m1-axis.
  This effect is achieved by translating the ?m1-link to make the
  markers coincident, and then attempting to move ?m1-point back onto ?m1-plane.
  In general, there are two distinct solutions to this problem,
  so a branch variable q_0 is used to select the desired solution."

  [kb m1 m2]
  ;; (pp/pprint ["t2r1 - in-line-mobile" "m1" m1 "m2" m2])
  (let [[[m1-link-name m1-proper-name] _] m1
         m1-link (-> kb :link (get m1-link-name))

         gmp2 (ga/gmp m2 kb)
         gmz2 (ga/gmz m2 kb)
         line2 (ga/line gmp2 gmz2)

         gmp1 (ga/gmz m1 kb)
         m1-plane (-> m1-link deref :tdof :plane)
         m1-point (-> m1-link deref :tdof :point)
         m1-lf (-> m1-link deref :tdof :lf)

         m1-axis (-> m1-link deref :rdof :axis)
         m1-axis-1 (-> m1-link deref :rdof :axis-1)
         m1-axis-2 (-> m1-link deref :rdof :axis-2)
         plane1 (ga/plane gmp1 (ga/normal m1-plane))

         meet-pnt (ga/meet line2 m1-plane)
         meet-pnt (if (nil? meet-pnt) gmp2 meet-pnt)

         new-link (dof/t2-r1:p->p m1-link m1-point m1-plane
                                  m1-axis m1-axis-1 m1-axis-2
                                  gmp1 meet-pnt m1-lf :q1) ]
    (dosync
     (alter m1-link merge new-link)
     (alter m1-link assoc
            :tdof {:# 1
                   :point (ga/gmp m1 kb)
                   :line line2 } ) )))

(defn assemble!->t2-r2 [kb m1 m2]  (ms/unimpl :t2-r2 slicer kb m1 m2))
(defn assemble!->t2-r3 [kb m1 m2]  (ms/unimpl :t2-r3 slicer kb m1 m2))

(defn assemble!->t3-r0 [kb m1 m2]  (ms/unimpl :t3-r0 slicer kb m1 m2))
(defn assemble!->t3-r1 [kb m1 m2]  (ms/unimpl :t3-r1 slicer kb m1 m2))
(defn assemble!->t3-r2 [kb m1 m2]  (ms/unimpl :t3-r2 slicer kb m1 m2))


(defn assemble!->t3-r3
  "PFT entry: (3,3,in-line) (?M_1 moves)

Initial status:
  0-TDOF(?m2-link, ?m2-point)
  0-RDOF(?m2-link, )

  3-TDOF(?m1-link)
  3-RDOF(?m1-link)

Plan fragment:
  begin
  R[0] = line(gmp(?M_2), gmz(?M_2));
  R[1] = perp-dist(gmp(?M_1), R[0]);
  R[2] = vec-diff(R[1], gmp(?M_1));
  translate(?m1-link, R[2]);
  R[3] = perp-base(gmp(?M_1), R[0]);
  end;

New status:
  1-TDOF(?m1-link, R[3], R[0], R[3])
  3-RDOF(?m1-link)

Explanation:
  Geom ?m1-link is free to translate, so the translation
  vector is measured and the ?m1-link is moved.
  No checks are required. "
  [kb m1 m2]
  ;; (pp/pprint ["t3r3 - in-line-p2o-slice" "m1" m1 "m2" m2])
  (let [ gmp2 (ga/gmp m2 kb)
         gmz2 (ga/gmz m2 kb)
         line2 (ga/line gmp2 gmz2)

         gmp1 (ga/gmz m1 kb)
         reject (ga/rejection gmp1 line2)
         [[m1-link-name m1-proper-name] _] m1
         m1-link (get-in kb [:link m1-link-name])]
    (dosync
     (alter m1-link merge
            (ga/translate @m1-link ga/vec-sum reject))
     (alter m1-link assoc
            :tdof {:# 1
                   :point (ga/gmp m1 kb)
                   :line line2 } ) )))

