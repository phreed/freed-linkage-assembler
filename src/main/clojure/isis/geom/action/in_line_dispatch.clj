(ns isis.geom.action.in-line-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant] ))


(defn precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [kb m1 m2]
  (cond (invariant/marker-position? kb m1)  [m1 m2]
        (and (invariant/marker-position? kb m2)
             (invariant/marker-direction? kb m2)) [m1 m2]
        :else nil))
