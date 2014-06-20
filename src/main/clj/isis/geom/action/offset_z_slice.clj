(ns isis.geom.action.offset-z-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
            [isis.geom.model.invariant :refer [marker->invariant?
                                               marker->add-invariant!]] ))



(defn offset-z->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (when (marker->invariant? inv m1 :p) m2))

(defmethod master/precondition?
  :offset-z
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
    (offset-z->precondition? m1 m2 invariants)))




(defn offset-z->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ _ _])

(defmethod master/assert-postcondition!
  :offset-z
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
    (offset-z->postcondition! m1 m2 invariants)))
