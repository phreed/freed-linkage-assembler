(ns isis.geom.action.in-line-slice-mobile
  "The table of rules for the in-line constraint where
  the point marker is MOBILE and the line is fixed."
  (:require [isis.geom.machine [geobj :as geo]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))

(def slicer *ns*)

(defn transform!->t0-r0 [kb m1 m2]  (println slicer :t0r0) )
(defn transform!->t0-r1 [kb m1 m2]  (println slicer :t0r1) )
(defn transform!->t0-r2 [kb m1 m2]  (println slicer :t0r2) )
(defn transform!->t0-r3 [kb m1 m2]  (println slicer :t0r3) )

(defn transform!->t1-r0 [kb m1 m2]  (println slicer :t1r0) )
(defn transform!->t1-r1 [kb m1 m2]  (println slicer :t1r1) )
(defn transform!->t1-r2 [kb m1 m2]  (println slicer :t1r2) )
(defn transform!->t1-r3 [kb m1 m2]  (println slicer :t1r3) )

(defn transform!->t2-r0 [kb m1 m2]  (println slicer :t2r0) )
(defn transform!->t2-r1 [kb m1 m2]  (println slicer :t2r1) )
(defn transform!->t2-r2 [kb m1 m2]  (println slicer :t2r2) )
(defn transform!->t2-r3 [kb m1 m2]  (println slicer :t2r3) )

(defn transform!->t3-r0 [kb m1 m2]  (println slicer :t3r0) )
(defn transform!->t3-r1 [kb m1 m2]  (println slicer :t3r1) )
(defn transform!->t3-r2 [kb m1 m2]  (println slicer :t3r2) )
(defn transform!->t3-r3 [kb m1 m2]  (println slicer :t3r3) )
