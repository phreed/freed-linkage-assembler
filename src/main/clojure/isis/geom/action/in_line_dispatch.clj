(ns isis.geom.action.in-line-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
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


(defmethod master/constraint-attempt?
  :in-line
  [kb constraint]
  (let [{point :m1 line :m2} constraint
        result (precondition? kb point line) ]
    (pp/pprint ["in-line constraint-attempt" result])
    (when result
      (let [[point line motive] result]
        (transform! kb point line motive))
      true)))



(defmethod transform!
  {:tdof 0 :rdof 0 :motive :fixed}
  [kb point line] kb)

(defmethod transform!
  {:tdof 0 :rdof 1 :motive :fixed}
  [kb point line] (fixed/transform!->t0-r1 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 2 :motive :fixed}
  [kb point line] (fixed/transform!->t0-r2 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 3 :motive :fixed}
  [kb point line] (fixed/transform!->t0-r3 kb point line))


(defmethod transform!
  {:tdof 1 :rdof 0 :motive :fixed}
  [kb point line] (fixed/transform!->t1-r0 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 1 :motive :fixed}
  [kb point line] (fixed/transform!->t1-r1 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 2 :motive :fixed}
  [kb point line] (fixed/transform!->t1-r2 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 3 :motive :fixed}
  [kb point line] (fixed/transform!->t1-r3 kb point line))



(defmethod transform!
  {:tdof 2 :rdof 0 :motive :fixed}
  [kb point line] (fixed/transform!->t2-r0 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 1 :motive :fixed}
  [kb point line] (fixed/transform!->t2-r1 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 2 :motive :fixed}
  [kb point line] (fixed/transform!->t2-r2 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 3 :motive :fixed}
  [kb point line] (fixed/transform!->t2-r3 kb point line))


(defmethod transform!
  {:tdof 3 :rdof 0 :motive :fixed}
  [kb point line] (fixed/transform!->t3-r0 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 1 :motive :fixed}
  [kb point line] (fixed/transform!->t3-r1 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 2 :motive :fixed}
  [kb point line] (fixed/transform!->t3-r2 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 3 :motive :fixed}
  [kb point line] (fixed/transform!->t3-r3 kb point line))



(defmethod transform!
  {:tdof 0 :rdof 1 :motive :mobile}
  [kb point line] (mobile/transform!->t0-r1 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 2 :motive :mobile}
  [kb point line] (mobile/transform!->t0-r2 kb point line))

(defmethod transform!
  {:tdof 0 :rdof 3 :motive :mobile}
  [kb point line] (mobile/transform!->t0-r3 kb point line))


(defmethod transform!
  {:tdof 1 :rdof 0 :motive :mobile}
  [kb point line] (mobile/transform!->t1-r0 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 1 :motive :mobile}
  [kb point line] (mobile/transform!->t1-r1 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 2 :motive :mobile}
  [kb point line] (mobile/transform!->t1-r2 kb point line))

(defmethod transform!
  {:tdof 1 :rdof 3 :motive :mobile}
  [kb point line] (mobile/transform!->t1-r3 kb point line))



(defmethod transform!
  {:tdof 2 :rdof 0 :motive :mobile}
  [kb point line] (mobile/transform!->t2-r0 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 1 :motive :mobile}
  [kb point line] (mobile/transform!->t2-r1 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 2 :motive :mobile}
  [kb point line] (mobile/transform!->t2-r2 kb point line))

(defmethod transform!
  {:tdof 2 :rdof 3 :motive :mobile}
  [kb point line] (mobile/transform!->t2-r3 kb point line))


(defmethod transform!
  {:tdof 3 :rdof 0 :motive :mobile}
  [kb point line] (mobile/transform!->t3-r0 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 1 :motive :mobile}
  [kb point line] (mobile/transform!->t3-r1 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 2 :motive :mobile}
  [kb point line] (mobile/transform!->t3-r2 kb point line))

(defmethod transform!
  {:tdof 3 :rdof 3 :motive :mobile}
  [kb point line] (mobile/transform!->t3-r3 kb point line))
