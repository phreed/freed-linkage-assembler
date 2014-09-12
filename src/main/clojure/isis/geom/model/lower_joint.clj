(ns isis.geom.model.lower-joint
  (:require
   [isis.geom.machine.geobj :as ga]
   [isis.geom.machine.tolerance :as tol]
   [clojure.pprint :as pp] ))

(comment
  "A joint's representation is [[:jprim-1 :jprim-2 ...] dof]
  where dof is the degrees of freedom for the joint." )


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


(declare expand-trivial)

(defmulti expand
  "Joints are conveniently specified at a higher level
  than primitive constraints.  In order to perform position-analysis
  these lower (and higher) joints must be decomposed into their
  constituent primitive-joint constraints."
  (fn [constraint] (:type constraint)) )

(defmethod expand :default [constraint] (expand-trivial constraint))

(defmethod expand :revolute [constraint] (expand-trivial constraint))
(defmethod expand :prismatic [constraint] (expand-trivial constraint))
(defmethod expand :cylindrical [constraint] (expand-trivial constraint))
(defmethod expand :spherical [constraint] (expand-trivial constraint))
(defmethod expand :ball [constraint] (expand-trivial constraint))

(defmethod expand :planar [constraint]
  (let [{j-type :type, ma :m1, mb :m2} constraint]
  [ {:type :in-plane, :m1 ma, :m2 mb}
    {:type :in-plane, :m1 mb, :m2 ma} ]))

(defmethod expand :linear [constraint] (expand-trivial constraint))
(defmethod expand :universal [constraint] (expand-trivial constraint))
(defmethod expand :fixed [constraint] (expand-trivial constraint))

(def lower-joint-map
  "Joints are conveniently specified at a higher level
  than primitive constraints.  In order to perform position-analysis
  these lower (and higher) joints must be decomposed into their
  constituent primitive-joint constraints.
  This map provides that relationship."
  {:revolute [:coincident :parallel-z]
   :prismatic [:in-line :parallel-z :offset-x]
   :cylindrical [:in-line :parallel-z]
   :spherical [:coincident]
   :ball [:coincident]
   :planar [:in-plane :parallel-z]
   :universal [:coincident :perpendicular-z]
   :fixed [:coincident :in-plane :offset-x] })

(defn expand-trivial
  "Expand a single lower joint into a vector of joint-primitives."
  [constraint]
  (let [j-type (:type constraint)
        p-types (get lower-joint-map j-type)]
    (into []
          (for [p-type p-types]
            (assoc-in constraint [:type] p-type)) )))


(defn expand-collection
  "Mutate and expand the lower joints."
  [collection]
  (loop [joints collection, result []]
    (if (empty? joints)
      result

      (let [reformed (expand (first joints)) ]
        (recur (rest joints) (into result reformed)) ))))

