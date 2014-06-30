(ns isis.geom.action.coincident-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
            [isis.geom.model.invariant
             :refer [marker->invariant?
                     marker->add-invariant!]] ))


(defn- coincident->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained followed by the marker that is constrained."
  [m1 m2 invs]
  (cond (marker->invariant? invs m2 :p) [m2 m1]
        (marker->invariant? invs m1 :p) [m1 m2]
        :else false))

(defn- coincident->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ m2 inv] (marker->add-invariant! inv m2 :p))


(defn coincident->transform-dispatch
  "Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof]."
  [m1 m2 invs]
  (let [links @(:g invs)
        [[link-name _] _] m1
        link (link-name links)]
    (keys link)))

(defmulti coincident->transform!
  "Transform the links and invariants so that the constraint is met."
  [m1 m2 invs]
  #'coincident->transform-dispatch
  :default nil)



(defmethod master/precondition?->transform!
  :coincident
  [constraint invariants]
  (let [{m1 :m1 m2 :m2} constraint]
    (when-let [ [m1 m2] (coincident->precondition? m1 m2 invariants) ]
      (coincident->transform! m1 m2 invariants))))


(defmethod coincident->transform!
  #{:link-has-3-tdof :link-has-3-rdof}
  [m1 m2 invs]
  (let [{m1 :m1 m2 :m2} constraint]
    (when-let [ [m1 m2] (coincident->precondition? m1 m2 invariants) ]
      (coincident->transform! m1 m2 invariants))))
