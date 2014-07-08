(ns isis.geom.action.parallel-z-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :refer [marker->invariant?
                                               marker->add-invariant!]] ))


(defn parallel-z->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (when (marker->invariant? inv m1 :loc) m2))






(defn parallel-z->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ m2 inv] (marker->add-invariant! inv m2 :z))

