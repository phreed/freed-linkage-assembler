(ns isis.geom.action.in-plane-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :refer [marker->invariant?]] ))


(defn in-plane->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (cond (marker->invariant? inv m1 :loc)  m2
        (and (marker->invariant? inv m2 :loc)
             (marker->invariant? inv m2 :z))
        m1))


