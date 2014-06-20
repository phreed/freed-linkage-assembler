(ns isis.geom.action.offset-x-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
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

(defmethod master/precondition?
  :offset-x
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
    (offset-x->precondition? m1 m2 invariants)))


(defn offset-x->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ m2 inv] (marker->add-invariant! inv m2 :z))

(defmethod master/assert-postcondition!
  :offset-x
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
    (offset-x->postcondition! m1 m2 invariants)))
