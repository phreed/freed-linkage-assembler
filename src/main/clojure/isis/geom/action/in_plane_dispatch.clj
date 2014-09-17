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
  is under-constrained followed by the marker that is fully-constrained.
  The :motive refers to the first marker, the point."
  [kb point plane]
  (cond (invariant/marker-position? kb plane) [point plane :fixed]
        (and (invariant/marker-position? kb point)
             (invariant/marker-direction? kb point)) [point plane :mobile]
        :else nil))


(defmulti transform!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  (fn [kb point plane motive]
    (pp/pprint ["plane" plane "point" point "motive" motive ])
    (let [[[link-name _] _] plane
          link @(get (:link kb) link-name)
          tdof (get-in link [:tdof :#])
          rdof (get-in link [:rdof :#]) ]
      {:tdof tdof :rdof rdof :motive motive}))
  :default nil)


(defmethod master/constraint-attempt?
  :in-plane
  [kb constraint]
  (let [{point :m1 plane :m2} constraint
        result (precondition? kb point plane) ]
    (pp/pprint ["in-plane constraint-attempt" result])
    (when result
      (let [[point plane motive] result]
        (transform! kb point plane motive))
      true)))


;; (def-transform-asymetric-methods)

(defmethod transform!
  {:tdof 0 :rdof 0 :motive :fixed}
  [kb point line motive] (fixed/transform!->t0-r0 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 1 :motive :fixed}
  [kb point line motive] (fixed/transform!->t0-r1 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 2 :motive :fixed}
  [kb point line motive] (fixed/transform!->t0-r2 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 3 :motive :fixed}
  [kb point line motive] (fixed/transform!->t0-r3 kb point line))


(defmethod transform!
  {:tdof 1 :rdof 0 :motive :fixed}
  [kb point line motive] (fixed/transform!->t1-r0 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 1 :motive :fixed}
  [kb point line motive] (fixed/transform!->t1-r1 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 2 :motive :fixed}
  [kb point line motive] (fixed/transform!->t1-r2 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 3 :motive :fixed}
  [kb point line motive] (fixed/transform!->t1-r3 kb point line))



(defmethod transform!
  {:tdof 2 :rdof 0 :motive :fixed}
  [kb point line motive] (fixed/transform!->t2-r0 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 1 :motive :fixed}
  [kb point line motive] (fixed/transform!->t2-r1 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 2 :motive :fixed}
  [kb point line motive] (fixed/transform!->t2-r2 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 3 :motive :fixed}
  [kb point line motive] (fixed/transform!->t2-r3 kb point line))


(defmethod transform!
  {:tdof 3 :rdof 0 :motive :fixed}
  [kb point line motive] (fixed/transform!->t3-r0 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 1 :motive :fixed}
  [kb point line motive] (fixed/transform!->t3-r1 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 2 :motive :fixed}
  [kb point line motive] (fixed/transform!->t3-r2 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 3 :motive :fixed}
  [kb point line motive] (fixed/transform!->t3-r3 kb point line))



(defmethod transform!
  {:tdof 0 :rdof 1 :motive :mobile}
  [kb point line motive] (mobile/transform!->t0-r1 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 2 :motive :mobile}
  [kb point line motive] (mobile/transform!->t0-r2 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 3 :motive :mobile}
  [kb point line motive] (mobile/transform!->t0-r3 kb point line))


(defmethod transform!
  {:tdof 1 :rdof 0 :motive :mobile}
  [kb point line motive] (mobile/transform!->t1-r0 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 1 :motive :mobile}
  [kb point line motive] (mobile/transform!->t1-r1 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 2 :motive :mobile}
  [kb point line motive] (mobile/transform!->t1-r2 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 3 :motive :mobile}
  [kb point line motive] (mobile/transform!->t1-r3 kb point line))



(defmethod transform!
  {:tdof 2 :rdof 0 :motive :mobile}
  [kb point line motive] (mobile/transform!->t2-r0 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 1 :motive :mobile}
  [kb point line motive] (mobile/transform!->t2-r1 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 2 :motive :mobile}
  [kb point line motive] (mobile/transform!->t2-r2 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 3 :motive :mobile}
  [kb point line motive] (mobile/transform!->t2-r3 kb point line))


(defmethod transform!
  {:tdof 3 :rdof 0 :motive :mobile}
  [kb point line motive] (mobile/transform!->t3-r0 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 1 :motive :mobile}
  [kb point line motive] (mobile/transform!->t3-r1 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 2 :motive :mobile}
  [kb point line motive] (mobile/transform!->t3-r2 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 3 :motive :mobile}
  [kb point line motive] (mobile/transform!->t3-r3 kb point line))
