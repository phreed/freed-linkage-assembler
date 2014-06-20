(ns isis.geom.action.parallel-z-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
            [isis.geom.model.invariant :refer [marker->invariant?
                                               marker->add-invariant!]] ))


(defn parallel-z->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (when (marker->invariant? inv m1 :p) m2))

(defmethod master/precondition?
  :parallel-z
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
    (parallel-z->precondition? m1 m2 invariants)))




(defn parallel-z->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ m2 inv] (marker->add-invariant! inv m2 :z))

(defmethod master/assert-postcondition!
  :parallel-z
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
    (parallel-z->postcondition! m1 m2 invariants)))
