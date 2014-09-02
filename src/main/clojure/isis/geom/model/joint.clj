(ns isis.geom.model.joint
  (:require
   [isis.geom.machine.geobj :as ga]
   [isis.geom.machine.tolerance :as tol]
   [clojure.pprint :as pp] ))

(comment
  "A joint's representation is [[:jprim-1 :jprim-2 ...] dof]
  where dof is the degrees of freedom for the joint." )

(def turn 6.2831853071)
(def quarter-turn (* 0.25 turn))


(def legal-joints
  "All combinations of joint primitives that restrict
  a combined total of less than six degrees-of-freedom.
  At most one translational joint primitive is used in
  any one joint, and at most one joint primitive affecting
  the z-axis and at most one affecting the x-axis is used."
  [[[:in-plane :parallel-z :offset-z] 4]
   [[:in-plane :parallel-z] 3]
   [[:in-plane] 1]
   [[:in-line :parallel-z :helical] 5]
   [[:in-line :parallel-z :offset-x] 5]
   [[:in-line :parallel-z] 4]
   [[:in-line] 2]
   [[:coincident :parallel-z] 5]
   [[:parallel-z] 2]
   [[:coincident :offset-z] 4]
   [[:offset-z] 1]
   [[:coincident] 3] ])

(def asymetric-joint-primitives
  "The two joint primitives that are asymetric."
  #{:in-line :in-plane})

(defn asymetric-joint-primitive?
  "'true' if the specified constraint type is an
  asymetric joint primitive."
  [constraint-type]
  (some asymetric-joint-primitives constraint-type))

(def joint-primitive-binary
  "the binary primitive joint types"
  #{:coincident :in-line :in-plane :parallel-z} )

(def joint-primitive-ternary
  "the ternary primitive joint types"
  #{:offset-z :offset-x :helical} )

(def joint-primitive-compond
  #{:co-oriented [:parallel-z [:offset-x 0.0]]
    :screw [:in-line :parallel-z :helical]
    :perpendicular-z [[:offset-z quarter-turn]] } )

(def joint-primitive-driving
  "the driving joint types"
  {:angle
   [:offset-x quarter-turn]
   :displacement [] } )


(def joint-primitive-map
  "joints are conveniently specified at a higher level
  than primitive constraints.  In order to perform position-analysis
  these lower (and higher) joints must be decomposed into their
  constituent constraints.  This map provides that relationship."
  {:revolute [:coincident :parallel-z]
   :prismatic [:in-line :parallel-z :offset-x]
   :cylindrical [:in-line :parallel-z]
   :spherical [:coincident]
   :ball [:coincident]
   :planar [:in-plane :parallel-z]
   :universal [:coincident :perpendicular-z]
   :fixed [:coincident :in-plane :offset-x] })


(defn- expand-point-constraint
  "expand the :point constraint into an array[1] of :coincident constraints."
  [constraint]
  [(assoc-in constraint [:type] :coincident)])


(defn- expand-csys-constraint
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


(defn- expand-planar-constraint [constraint] nil)


(defn- expand-higher-constraint
  "Mutate and expand a constraint as needed."
  [constraint]
  (case (:type constraint)
    :point (expand-point-constraint constraint)
    :csys (expand-csys-constraint constraint)
    :planar (expand-planar-constraint constraint)
    constraint))


(defn expand-higher-constraints
  "Mutate and expand the constraints."
  [constraints]
  (pp/pprint "expand-constraints" constraints)
  (loop [constraints constraints, result []]
    (pp/pprint ["expand:" result])
    (if (empty? constraints) result
      (recur (rest constraints) (into result (expand-higher-constraint (first constraints))) ))))
