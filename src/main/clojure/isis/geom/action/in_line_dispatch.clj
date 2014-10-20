(ns isis.geom.action.in-line-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action
             [in-line-fixed-slice :as fixed]
             [in-line-mobile-slice :as mobile]]
            [clojure.pprint :as pp]))


(defn precondition
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [kb point line]
  (cond (invariant/marker-position? kb line)  [kb point line :mobile]
        (and (invariant/marker-position? kb point)
             (invariant/marker-direction? kb point)) [kb point line :fixed]
        :else nil))


(defn assemble-dispatch
  [kb point line motive]
  (let [[[link-name _] _] (case motive
                            :fixed line
                            :mobile point)
        link @(get (:link kb) link-name)
        tdof (get-in link [:tdof :#])
        rdof (get-in link [:rdof :#]) ]
    #_(pp/pprint ["in-line assemble!" (str tdof ":" rdof "-" motive)
                  "point" point "line" line])
    {:tdof tdof :rdof rdof :motive motive}))

(defmulti assemble!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  assemble-dispatch
  :default nil)


(defmethod ms/constraint-attempt?
  :in-line
  [kb constraint]
  (let [{point :m1 line :m2} constraint
        precon (precondition kb point line) ]
    (if-not precon
      :pre-condition-not-met
      (let [[kb point line motive] precon]
        (pp/fresh-line)
        (pp/pprint (str "in-line"
                        (assemble-dispatch kb point line motive)))
        (assemble! kb point line motive)))))


(ms/defmethod-asymetric-transform assemble!)

