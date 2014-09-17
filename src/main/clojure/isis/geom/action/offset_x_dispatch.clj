(ns isis.geom.action.offset-x-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant] ))


(defn precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [kb m1 m2]
  (cond (and (invariant/marker-direction? kb m1)
             (invariant/marker-direction? kb m2)
             (invariant/marker-twist? kb m1)) [m1 m2]
        :else nil))





;; (master/def-transform-symetric-methods transform!)
