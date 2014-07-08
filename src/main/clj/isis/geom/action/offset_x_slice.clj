(ns isis.geom.action.offset-x-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :refer [marker->invariant?
                                               marker->add-invariant!]] ))


(defn offset-x->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (when (and (marker->invariant? inv m1 :z)
             (marker->invariant? inv m2 :z)
             (marker->invariant? inv m1 :x))
    m2))




(defn offset-x->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ m2 inv] (marker->add-invariant! inv m2 :z))

