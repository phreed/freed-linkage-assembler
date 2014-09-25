(ns isis.geom.action.offset-z-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.machine [geobj :as ga]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))

(def slicer "offset-z-slice")

(defn assemble!->t0-r0 [kb m1 m2]  (ms/unimpl  :t0r0 slicer kb m1 m2))
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
(defn assemble!->t3-r3 [kb m1 m2]  (ms/unimpl  :t3r3 slicer kb m1 m2))

