(ns isis.geom.action.offset-z-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant] ))



(defn precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [kb m1 m2]
  (when (invariant/marker-position? kb m1) m2))





