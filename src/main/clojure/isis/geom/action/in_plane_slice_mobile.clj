(ns isis.geom.action.in-plane-slice-mobile
  "The table of rules for the in-plane constraint where
  the point marker is MOBILE and the plane is fixed."
  (:require [clojure.pprint :as pp]
            [isis.geom.position-dispatch :as ms]
            [isis.geom.machine [geobj :as ga]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))


(def slicer "in-plane-mobile")

(defn assemble!->t0-r0
  "PFT entry: (0,0,in-plane)  (M_1 moves)

  Initial status:
  0-TDOF(?link, ?point)
  0-RDOF(?link)

  Plan fragment:
  begin
  R[0] = vec-diff(gmp(?M_2), gmp(?M_1));
  R[1] = inner-prod(R[0], gmz(?M_2));
  unless zero?(R[1])
  error(R[1], estring[9]);
  end;

  New status:
  0-TDOF(?link, ?point)
  0-RDOF(?link)

  Explanation:
  Geom ?link is fixed, so the in-plane constraint
  can only be checked for consistency.
  "[kb m1 m2]
  ;; (pp/pprint ["t0r0 - in-plane-slice-mobile" "m1" m1 "m2" m2])
  (let [ gmp2 (ga/gmp m2 kb)
         gmz2 (ga/gmz m2 kb)
         line2 (ga/line gmp2 gmz2)

         gmp1 (ga/gmz m1 kb)
         separate (ga/separation gmp1 line2)
         [[m1-link-name _] _] m1
         m1-link @(get-in kb [:link m1-link-name])]
    m1-link))

(defn assemble!->t0-r1 [kb m1 m2]  (ms/unimpl :t0-r1 slicer kb m1 m2))
(defn assemble!->t0-r2 [kb m1 m2]  (ms/unimpl :t0-r2 slicer kb m1 m2))
(defn assemble!->t0-r3 [kb m1 m2]  (ms/unimpl :t0-r3 slicer kb m1 m2))

(defn assemble!->t1-r0 [kb m1 m2]  (ms/unimpl :t1-r0 slicer kb m1 m2))
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unimpl :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unimpl :t1-r2 slicer kb m1 m2))
(defn assemble!->t1-r3
  " PFT entry: (1,3,in-plane) (?M_1 moves)

  Initial status:
  1-TDOF(?link, ?point, ?line, ?lf)
  3-RDOF(?link)

  Explanation:
  This entry has no application. "

  [kb m1 m2] "this entry has no application - in-plane mobile t1-r3")

(defn assemble!->t2-r0 [kb m1 m2]  (ms/unimpl :t2-r0 slicer kb m1 m2))
(defn assemble!->t2-r1 [kb m1 m2]  (ms/unimpl :t2-r1 slicer kb m1 m2))
(defn assemble!->t2-r2 [kb m1 m2]  (ms/unimpl :t2-r2 slicer kb m1 m2))
(defn assemble!->t2-r3
  " PFT entry: (2,3,in-plane) (?M_1 moves)

  Initial status:
  2-TDOF(?link, ?point, ?plane, ?lf)
  3-RDOF(?link)

  Explanation:
  This entry can arise when a point is constrainded to
  three orthogonal planes, where the planes are fixed
  in space.  This will apply on the second constraint. "
  [kb m1 m2] "this entry has no application - in-plane mobile t3-r0")


(defn assemble!->t3-r0 [kb m1 m2]  (ms/unimpl :t3-r0 slicer kb m1 m2))
(defn assemble!->t3-r1 [kb m1 m2]  (ms/unimpl :t3-r1 slicer kb m1 m2))
(defn assemble!->t3-r2 [kb m1 m2]  (ms/unimpl :t3-r2 slicer kb m1 m2))

(defn assemble!->t3-r3
  " PFT entry: (3,3,in-plane) (?M_1 moves)

  Initial status:
  3-TDOF(?m1-link)
  3-RDOF(?m1-link)

  Plan fragment:
  begin
  R[0] = plane(gmp(?M_2), gmz(?M_2));
  R[1] = perp-dist(gmp(?M_1), R[0]);
  R[2] = vec-diff(R[1], gmp(?M_1));
  translate(?link, R[2]);
  R[3] = gmp(?M_1);
  end;

  New status:
  2-TDOF(?m1-link, R[3], R[0], R[3])
  3-RDOF(?m1-link)

  Explanation:
  Geom ?m1-link is free to translate, so the translation
  vector is measured and the ?m1-link is moved.
  No checks are required.
  "
  [kb m1 m2]
  ;; (ms/dump nil {:tdof 3 :rdof 3 :motive :mobile} "in-plane-mobile" kb m1 m2)

  (let [ gmp2 (ga/gmp m2 kb)
         gmz2 (ga/gmz m2 kb)
         gmp1 (ga/gmp m1 kb)

         plane2 (ga/plane gmp2 gmz2)
         reject (ga/rejection gmp1 plane2)
         xlate (ga/vec-diff reject gmp1)

         [[m1-link-name m1-proper-name] _] m1
         m1-link (get-in kb [:link m1-link-name])]
    (dosync
     (alter m1-link merge (ga/translate @m1-link xlate))
     (let [gmp1 (ga/gmp m1 kb)]
       (alter m1-link assoc
              :tdof {:# 2
                     :point gmp1
                     :plane plane2
                     :lf gmp1 } ) ))) )

