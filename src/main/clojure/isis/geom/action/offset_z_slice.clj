(ns isis.geom.action.offset-z-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))

(def slicer "offset-z-slice")

(defn assemble!->t0-r0 [kb m1 m2]  :consistent)
(defn assemble!->t0-r1 [kb m1 m2]  (ms/unimpl :t0-r1 slicer kb m1 m2))
(defn assemble!->t0-r2 [kb m1 m2]  (ms/unimpl :t0-r2 slicer kb m1 m2))
(defn assemble!->t0-r3 [kb m1 m2]  (ms/unimpl :t0-r3 slicer kb m1 m2))

(defn assemble!->t1-r0 [kb m1 m2]  (ms/unreal :t1-r0 slicer kb m1 m2))
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unreal :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unreal :t1-r2 slicer kb m1 m2))
(defn assemble!->t1-r3 [kb m1 m2]  (ms/unreal :t1-r3 slicer kb m1 m2))

(defn assemble!->t2-r0 [kb m1 m2]  (ms/unreal :t2-r0 slicer kb m1 m2))
(defn assemble!->t2-r1 [kb m1 m2]  (ms/unreal :t2-r1 slicer kb m1 m2))
(defn assemble!->t2-r2 [kb m1 m2]  (ms/unreal :t2-r2 slicer kb m1 m2))
(defn assemble!->t2-r3 [kb m1 m2]  (ms/unreal :t2-r3 slicer kb m1 m2))

(defn assemble!->t3-r0 [kb m1 m2]  (ms/unreal :t3-r0 slicer kb m1 m2))
(defn assemble!->t3-r1 [kb m1 m2]  (ms/unreal :t3-r1 slicer kb m1 m2))
(defn assemble!->t3-r2 [kb m1 m2]  (ms/unreal :t3-r2 slicer kb m1 m2))
(defn assemble!->t3-r3 [kb m1 m2]  (ms/unreal :t3-r3 slicer kb m1 m2))

