(ns isis.geom.action.in-plane-dispatch
  "The dispatcher of rules for the in-plane constraint.
   In in-plane constraint means that there are two markers,
  one representing a point, M_1, and a second representing a
  plane, M_2.  The point is constrained to lie on the plane."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action
             [in-plane-slice-fixed :as fixed]
             [in-plane-slice-mobile :as mobile]]
            [clojure.pprint :as pp]))


(defn- precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained followed by the marker that is constrained.
  The :motive refers to the first marker, the point."
  [kb point plane]
  (pp/pprint ["in-plane precondition" "point" point "plane" plane "invariant" (:invar kb)])
  (cond (invariant/marker? kb plane :loc) [point plane :mobile]
        (invariant/marker? kb point :loc) [point plane :fixed]
        :else false))


(defmulti transform!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  (fn [kb point m2 mo]
    (let [[[link-name _] _] m2
          link @(get (:link kb) link-name)
          tdof (get-in link [:tdof :#])
          rdof (get-in link [:rdof :#]) ]
      (println tdof "-" rdof "- in-plane")
      {:tdof tdof :rdof rdof :motive mo}))
  :default nil)


(defmethod master/constraint-attempt?
  :in-plane
  [kb constraint]
  (let [{m1 :m1 m2 :m2} constraint
        result (precondition? kb m1 m2) ]
    (when result
      (let [[ma1 ma2 mo] result]
        (transform! kb ma1 ma2 mo)
        true))))


(defmethod transform!
  {:tdof 0 :rdof 1 :motive :fixed}
  [kb m1 m2]
  (kb)
  (fixed/transform!->t3-r3 kb m1 m2))


