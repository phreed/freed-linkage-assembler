(ns isis.geom.model.primitive-joint
  (:require
   [isis.geom.machine.geobj :as ga]
   [isis.geom.machine.tolerance :as tol]
   [clojure.pprint :as pp] ))

(comment
  "A joint's representation is [[:jprim-1 :jprim-2 ...] dof]
  where dof is the degrees of freedom for the joint." )

(def turn 6.2831853071)
(def quarter-turn (* 0.25 turn))


(def asymetric-joint-primitives
  "The two joint primitives that are asymetric."
  #{:in-line :in-plane})

(defn asymetric-joint-primitive?
  "'true' if the specified constraint type is an
  asymetric joint primitive."
  [constraint-type]
  (some asymetric-joint-primitives constraint-type))

(def joint-binary
  "the binary primitive joint types"
  #{:coincident :in-line :in-plane :parallel-z} )

(def joint-ternary
  "the ternary primitive joint types"
  #{:offset-z :offset-x :helical} )

(def joint-compond
  #{:co-oriented [:parallel-z [:offset-x 0.0]]
    :screw [:in-line :parallel-z :helical]
    :perpendicular-z [[:offset-z quarter-turn]] } )

(def joint-driving
  "the driving joint types"
  {:angle
   [:offset-x quarter-turn]
   :displacement [] } )

