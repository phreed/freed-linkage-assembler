(ns isis.geom.action.in-plane-dispatch
  "The dispatcher of rules for the in-plane constraint.
  In in-plane constraint means that there are two markers,
  one representing a point, M_1, and a second representing a
  plane, M_2.  The point is constrained to lie in the plane."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action
             [in-plane-fixed-slice :as fixed]
             [in-plane-mobile-slice :as mobile]]
            [clojure.pprint :as pp]))


(defn- precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is under-constrained followed by the marker that is fully-constrained.
  The :motive refers to the first marker, the point, thus

  :mobile indicates that the point *is-not* fully-constrained
  but the plane *is* fully-constrained.

  :fixed indicates that the plane *is-not* fully-constrained
  but the point *is* fully-constrained.

  nil indicates that there are insufficient constraints to
  make any inference."
  [kb point plane]
  (cond (invariant/marker-position? kb plane) [point plane :mobile]
        (and (invariant/marker-position? kb point)
             (invariant/marker-direction? kb point)) [point plane :fixed]
        :else nil))

(defn- assemble-dispatch [kb point plane motive]
    (let [[[link-name _] _] (case motive :fixed plane :mobile point)
          link @(get (:link kb) link-name)
          tdof (get-in link [:tdof :#])
          rdof (get-in link [:rdof :#]) ]
      #_(pp/pprint ["in-plane assemble!" (str tdof ":" rdof "-" motive)
                  "point" point "plane" plane])
      {:tdof tdof :rdof rdof :motive motive}))

(defmulti assemble!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  assemble-dispatch
  :default nil)


(defmethod ms/constraint-attempt?
  :in-plane
  [kb constraint]
  (let [{point :m1 plane :m2} constraint
        precon (precondition? kb point plane) ]
    (if-not precon
      :pre-condition-not-met
      (let [[point plane motive] precon]
        (pp/fresh-line)
        (pp/pprint (str "in-plane"
                        (assemble-dispatch kb point plane motive)))
        (assemble! kb point plane motive) ))))


(ms/defmethod-asymetric-transform assemble!)

