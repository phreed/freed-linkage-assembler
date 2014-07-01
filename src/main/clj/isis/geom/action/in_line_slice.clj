(ns isis.geom.action.in-line-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
            [isis.geom.model.invariant :refer [marker->invariant?
                                               marker->add-invariant!]] ))


(defn in-line->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (cond (marker->invariant? inv m1 :p)  m2
        (and (marker->invariant? inv m2 :p)
             (marker->invariant? inv m2 :z))
        m1))


(defn in-line->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ _ _])

