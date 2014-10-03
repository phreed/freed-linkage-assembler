(ns isis.geom.action.parallel-z-slice
  "The table of rules."
  (:require [clojure.pprint :as pp]
            [isis.geom.position-dispatch :as ms]
            [isis.geom.machine
             [geobj :as ga]
             [tolerance :as tol]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))

(def slicer "parallel-z-slice")

(defn assemble!->t0-r0 [kb m1 m2] :consistent)
(defn assemble!->t0-r1 [kb m1 m2]  (ms/unimpl :t0-r1 slicer kb m1 m2))
(defn assemble!->t0-r2 [kb m1 m2]  (ms/unimpl :t0-r2 slicer kb m1 m2))
(defn assemble!->t0-r3 [kb m1 m2]  (ms/unimpl :t0-r3 slicer kb m1 m2))

(defn assemble!->t1-r0 [kb m1 m2]  (ms/unimpl :t1-r0 slicer kb m1 m2))
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unimpl :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unreal :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r3 [kb m1 m2]  (ms/unimpl :t1-r3 slicer kb m1 m2))

(defn assemble!->t2-r0 [kb m1 m2]  (ms/unimpl :t2-r0 slicer kb m1 m2))
(defn assemble!->t2-r1 [kb m1 m2]  (ms/unimpl :t2-r1 slicer kb m1 m2))
(defn assemble!->t2-r2 [kb m1 m2]  (ms/unreal :t2-r1 slicer kb m1 m2))

(defn assemble!->t2-r3
  "PFT entry: (2,3,parallel-z)

  Initial status:
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
     (invariant/set-marker! kb [m2-link-name m2-proper-name] :dir)
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

(defn assemble!->t3-r0 [kb m1 m2]  (ms/unimpl :t3-r0 slicer kb m1 m2))
(defn assemble!->t3-r1 [kb m1 m2]  (ms/unimpl :t3-r1 slicer kb m1 m2))
(defn assemble!->t3-r2 [kb m1 m2]  (ms/unreal :t3-r2 slicer kb m1 m2))
(defn assemble!->t3-r3 [kb m1 m2]  (ms/unimpl :t3-r3 slicer kb m1 m2))

