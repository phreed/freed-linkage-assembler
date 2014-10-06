(ns dof.coincident-test
  "Sample assembly for rotate (and translate)."
  (:require [midje.sweet :as tt]
            [isis.geom.machine
             [geobj :as ga]
             [tolerance :as tol]]
            [isis.geom.action
             [auxiliary :as dof]]))

(let [link {:versor {:xlate [0.0 0.0 0.0]
                     :rotate [1.0 0.0 0.0 0.0]}
            :tdof {:# 0 :point [1.0 0.0 0.0]}
            :rdof {:# 3}}
      inv-pnt (get-in link [:tdof :point])

      from-point-2 [0.0 1.0 0.0]
      to-point-2 [0.0 1.0 0.0]

      link-2 (assoc
               (dof/r3:p->p link inv-pnt from-point-2 to-point-2)
               :rdof {:# 1
                      :axis (ga/normalize
                             (ga/vec-diff to-point-2 inv-pnt))})
      inv-axis (get-in link-2 [:rdof :axis])

      from-point-3 [0.0 0.0 1.0]
      to-point-3 [0.0 0.0 1.0]

      link-3 (dof/r1:p->p link-2 inv-pnt from-point-3 to-point-3
                              inv-axis nil nil) ]

  (tt/facts "mimic dof/r1:p->p (0 degree rotation)"
         (tt/fact "link 2"
               link-2 =>
               '{:versor {:xlate [0.0 0.0 0.0]
                          :rotate [1.0 0.0 0.0 0.0]}
                 :tdof {:# 0 :point [1.0 0.0 0.0]}
                 :rdof {:# 1 :axis [-0.7071067811865475 0.7071067811865475 0.0]}} )

         (tt/fact "link 3"
               link-3 =>
               '{:versor {:xlate [0.0 0.0 0.0]
                          :rotate [1.0 0.0 0.0 0.0]}
                 :tdof {:# 0 :point [1.0 0.0 0.0]}
                 :rdof {:# 1 :axis [-0.7071067811865475 0.7071067811865475 0.0]}} ) ))
