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
