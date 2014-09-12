(ns isis.geom.action.helical-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :refer [marker->invariant?]] ))



(defn helical->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (when (and (marker->invariant? inv m1 :z)
             (marker->invariant? inv m2 :z)
             (marker->invariant? inv m1 :x))
    m2))


