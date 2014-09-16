(ns isis.geom.action.parallel-z-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action
             [parallel-z-slice :as xlice]]
            [clojure.pprint :as pp]))


(defn- precondition?
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
    (pp/pprint ["line-1" m1 "line-2" m2])
    (let [[[link-name _] _] m2
          link @(get (:link kb) link-name)
          tdof (get-in link [:tdof :#])
          rdof (get-in link [:rdof :#]) ]
      {:tdof tdof :rdof rdof}))
  :default nil)


(defmethod master/constraint-attempt?
  :parallel-z
  [kb constraint]
  (let [{m1 :m1 m2 :m2} constraint
        result (precondition? kb m1 m2) ]
    (pp/pprint ["parallel-z constraint-attempt" result])
    (when result
      (let [[m1 m2] result]
        (transform! kb m1 m2))
      true)))


(defmethod transform!
  {:tdof 0 :rdof 0}
  [kb m1 m2] kb)

(defmethod transform!
  {:tdof 0 :rdof 1}
  [kb m1 m2] (xlice/transform!->t0-r1 kb m1 m2))

(defmethod transform!
  {:tdof 0 :rdof 2}
  [kb m1 m2] (xlice/transform!->t0-r2 kb m1 m2))

(defmethod transform!
  {:tdof 0 :rdof 3}
  [kb m1 m2] (xlice/transform!->t0-r3 kb m1 m2))


(defmethod transform!
  {:tdof 1 :rdof 0}
  [kb m1 m2] (xlice/transform!->t1-r0 kb m1 m2))

(defmethod transform!
  {:tdof 1 :rdof 1}
  [kb m1 m2] (xlice/transform!->t1-r1 kb m1 m2))

(defmethod transform!
  {:tdof 1 :rdof 2}
  [kb m1 m2] (xlice/transform!->t1-r2 kb m1 m2))

(defmethod transform!
  {:tdof 1 :rdof 3}
  [kb m1 m2] (xlice/transform!->t1-r3 kb m1 m2))


(defmethod transform!
  {:tdof 2 :rdof 0}
  [kb m1 m2] (xlice/transform!->t2-r0 kb m1 m2))

(defmethod transform!
  {:tdof 2 :rdof 1}
  [kb m1 m2] (xlice/transform!->t2-r1 kb m1 m2))

(defmethod transform!
  {:tdof 2 :rdof 2}
  [kb m1 m2] (xlice/transform!->t2-r2 kb m1 m2))

(defmethod transform!
  {:tdof 2 :rdof 3}
  [kb m1 m2] (xlice/transform!->t2-r3 kb m1 m2))



(defmethod transform!
  {:tdof 3 :rdof 0}
  [kb m1 m2] (xlice/transform!->t3-r0 kb m1 m2))

(defmethod transform!
  {:tdof 3 :rdof 1}
  [kb m1 m2] (xlice/transform!->t3-r1 kb m1 m2))

(defmethod transform!
  {:tdof 3 :rdof 2}
  [kb m1 m2] (xlice/transform!->t3-r2 kb m1 m2))

(defmethod transform!
  {:tdof 3 :rdof 3}
  [kb m1 m2] (xlice/transform!->t3-r3 kb m1 m2))
