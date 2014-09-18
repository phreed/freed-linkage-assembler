(ns isis.geom.action.offset-z-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action [offset-z-slice :as xlice]]))



(defn precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [kb m1 m2]
  (when (invariant/marker-position? kb m1) m2))



(defmulti transform!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  (fn [kb m1 m2]
    (let [[[link-name _] _] m2
          link @(get (:link kb) link-name)
          tdof (get-in link [:tdof :#])
          rdof (get-in link [:rdof :#]) ]
      {:tdof tdof :rdof rdof}))
  :default nil)



(defmethod ms/constraint-attempt?
  :offset-z
  [kb constraint]
  (let [{m1 :m1 m2 :m2} constraint
        result (precondition? kb m1 m2) ]
    (when result
      (let [[ma1 ma2] result]
        (transform! kb ma1 ma2)
        true))))

(ms/defmethod-symetric-transform transform!)




