(ns isis.geom.action.in-line-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action
             [in-line-slice-fixed :as fixed]
             [in-line-slice-mobile :as mobile]]
            [clojure.pprint :as pp]))


(defn precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [kb point line]
  (cond (invariant/marker-position? kb point)  [point line]
        (and (invariant/marker-position? kb line)
             (invariant/marker-direction? kb line)) [point line]
        :else nil))


(defmulti transform!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  (fn [kb point line motive]
    (pp/pprint ["line" line "point" point "motive" motive ])
    (let [[[link-name _] _] line
          link @(get (:link kb) link-name)
          tdof (get-in link [:tdof :#])
          rdof (get-in link [:rdof :#]) ]
      {:tdof tdof :rdof rdof :motive motive}))
  :default nil)


(defmethod ms/constraint-attempt?
  :in-line
  [kb constraint]
  (let [{point :m1 line :m2} constraint
        result (precondition? kb point line) ]
    ;; (pp/pprint ["in-line constraint-attempt" result])
    (when result
      (let [[point line motive] result]
        (transform! kb point line motive))
      true)))


(ms/defmethod-asymetric-transform transform!)

