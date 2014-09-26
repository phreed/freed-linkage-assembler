(ns isis.geom.model.meta-constraint
  "These joints are introduced as part of the AVM/META project."
  (:require
   [isis.geom.machine.geobj :as ga]
   [isis.geom.machine.tolerance :as tol]
   [clojure.pprint :as pp] ))

(defn- expand-point
  "expand the :point constraint into an array[1] of :coincident constraints."
  [constraint]
  [(assoc-in constraint [:type] :coincident)])


(defn- expand-csys
  "When a component is placed with a csys it
  can be implemented with three
  standardized coincident constraints.
  These constraints are: origin, point on rotated
  x-axis and rotated z-axis."
  [constraint]
  (let [{c-type :type, m1 :m1, m2 :m2} constraint
        [[m1-link-name m1-proper-name] m1-values] m1
        [[m2-link-name m2-proper-name] m2-values] m2
        {m1-e :e, m1-q :q, m1-pi :pi} m1-values
        {m2-e :e, m2-q :q, m2-pi :pi} m2-values
        m1-quat (ga/axis-pi-angle->quaternion m1-q m1-pi)
        m2-quat (ga/axis-pi-angle->quaternion m2-q m2-pi)

        m1-3x (ga/vec-sum m1-e (ga/quat-sandwich m1-quat [300.0 0.0 0.0]))
        m2-3x (ga/vec-sum m2-e (ga/quat-sandwich m2-quat [300.0 0.0 0.0]))

        m1-4y (ga/vec-sum m1-e (ga/quat-sandwich m1-quat [0.0 400.0 0.0]))
        m2-4y (ga/vec-sum m2-e (ga/quat-sandwich m2-quat [0.0 400.0 0.0]))
        ]

    [ { :type :coincident
        :m1 [[m1-link-name (str m1-proper-name "-origin")] {:e m1-e}]
        :m2 [[m2-link-name (str m2-proper-name "-origin")] {:e m2-e}]}
      { :type :coincident
        :m1 [[m1-link-name (str m1-proper-name "-3x")] {:e m1-3x}]
        :m2 [[m2-link-name (str m2-proper-name "-3x")] {:e m2-3x}]}
      { :type :coincident
        :m1 [[m1-link-name (str m1-proper-name "-4y")] {:e m1-4y}]
        :m2 [[m2-link-name (str m2-proper-name "-4y")] {:e m2-4y}]}
      ]))


(defn- expand-planar
  "expansion of :planar constraints is a primitive expansion."
  [constraint] [constraint])

(defn- expand-unidentified [constraint] [constraint])


(defn- expand
  "Mutate and expand a constraint as needed."
  [constraint]
  (case (:type constraint)
    :point (expand-point constraint)
    :csys (expand-csys constraint)
    :planar (expand-planar constraint)
    (expand-unidentified constraint)))


(defn expand-collection
  "Mutate and expand the constraints."
  [collection]
  (loop [constraints collection, result []]
    (if (empty? constraints)
      result

      (let [reformed (expand (first constraints)) ]
        ;; (clojure.pprint/pprint ["reformed" reformed])
        (recur (rest constraints) (into result reformed)) ) )))
