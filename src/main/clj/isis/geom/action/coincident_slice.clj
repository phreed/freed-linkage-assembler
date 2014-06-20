(ns isis.geom.action.coincident-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
            [isis.geom.model.invariant :refer [marker->invariant?
                                     marker->add-invariant!]] ))


(defn- coincident->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (when (marker->invariant? inv m1 :p) m2))

(defmethod master/precondition?
  :coincident
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
    (coincident->precondition? m1 m2 invariants)))




(defn- coincident->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ m2 inv] (marker->add-invariant! inv m2 :p))

(defmethod master/assert-postcondition!
  :coincident
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
  (coincident->postcondition! m1 m2 invariants)))
