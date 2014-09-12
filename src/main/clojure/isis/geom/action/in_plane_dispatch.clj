(ns isis.geom.action.in-plane-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action
             [in-plane-slice-fixed :as fixed]
             [in-plane-slice-moves :as moves]]
            [clojure.pprint :as pp]))


(defn- precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained followed by the marker that is constrained."
  [kb m1 m2]
  (pp/pprint ["in-plane precondition" "m1" m1 "m2" m2 "mark" (:mark kb)])
  (cond (invariant/marker? kb m2 :loc) [m2 m1]
        (invariant/marker? kb m1 :loc) [m1 m2]
        :else false))


(defn transform-dispatch
  "Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  [kb m1 m2]
  (let [[[link-name _] _] m2
        link @(get (:link kb) link-name)
        tdof (get-in link [:tdof :#])
        rdof (get-in link [:rdof :#]) ]
    (println tdof "-" rdof "- in-plane")
    {:tdof tdof :rdof rdof :motive :fixed}))

(defmulti transform!
  "Transform the links and kb so that the constraint is met."
  #'transform-dispatch
  :default nil)


(defmethod master/constraint-attempt?
  :in-plane
  [kb constraint]
  (let [{m1 :m1 m2 :m2} constraint
        result (precondition? kb m1 m2) ]
    (when result
      (let [[ma1 ma2] result]
        (transform! kb ma1 ma2)
        true))))


(defmethod transform!
  {:tdof 0 :rdof 1 :motive :fixed}
  [kb m1 m2]
  (kb)
  #_(fixed/transform!->0-1 kb m1 m2))


