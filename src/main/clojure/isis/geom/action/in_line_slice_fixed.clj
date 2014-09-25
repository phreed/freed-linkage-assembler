(ns isis.geom.action.in-line-slice-fixed
  "The table of rules for the in-line constraint where
  the point marker is FIXED and the line is mobile."
  (:require [clojure.pprint :as pp]
            [isis.geom.position-dispatch :as ms]
            [isis.geom.machine [geobj :as ga]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))


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
  true)

(def slicer "in-line-slice-fixed")

(defn assemble!->t0-r1 [kb m1 m2]  (ms/unimpl  :t0r1 slicer kb m1 m2))
(defn assemble!->t0-r2 [kb m1 m2]  (ms/unimpl  :t0r2 slicer kb m1 m2))
(defn assemble!->t0-r3 [kb m1 m2]  (ms/unimpl  :t0r3 slicer kb m1 m2))

(defn assemble!->t1-r0 [kb m1 m2]  (ms/unimpl  :t1r0 slicer kb m1 m2))
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unimpl  :t1r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unimpl  :t1r2 slicer kb m1 m2))
(defn assemble!->t1-r3 [kb m1 m2]  (ms/unimpl  :t1r3 slicer kb m1 m2))

(defn assemble!->t2-r0 [kb m1 m2]  (ms/unimpl  :t2r0 slicer kb m1 m2))
(defn assemble!->t2-r1 [kb m1 m2]  (ms/unimpl  :t2r1 slicer kb m1 m2))
(defn assemble!->t2-r2 [kb m1 m2]  (ms/unimpl  :t2r2 slicer kb m1 m2))
(defn assemble!->t2-r3 [kb m1 m2]  (ms/unimpl  :t2r3 slicer kb m1 m2))

(defn assemble!->t3-r0 [kb m1 m2]  (ms/unimpl  :t3r0 slicer kb m1 m2))
(defn assemble!->t3-r1 [kb m1 m2]  (ms/unimpl  :t3r1 slicer kb m1 m2))
(defn assemble!->t3-r2 [kb m1 m2]  (ms/unimpl  :t3r2 slicer kb m1 m2))
(defn assemble!->t3-r3 [kb m1 m2]
    (pp/pprint ["t3r3 - in-line-slice-fixed" "m1" m1 "m2" m2])  )

