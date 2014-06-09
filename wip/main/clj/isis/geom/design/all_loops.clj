(ns isis.geom.design.all-loops)

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

(def asymetric-joint-primitives
  "The two joint primitives that are asymetric."
  #{:in-line :in-plane})

(defn asymetric-joint-primitive?
  "'true' if the specified constraint type is an
  asymetric joint primitive."
  [?constraint-type]
  (some asymetric-joint-primitives ?constraint-type))

(defn legal-loop?
  "Returns a loop descriptor if the joint-list forms
  a legal 3 geom loop.
  Returns nil otherwise.

  In order for a loop to be legal:

  Its mobility is non-positive.

  In order to have a closed-form
  solution, the geom cannot have more than one
  joint having the form '[[:coincident :offset-z] 4]'.
  Pairs of such joints must be solved numerically.

  If more than one :in-plane constraint is present,
  there must be a rotational degree-of-freedom in the system."
  [?joint-list ?mobility]
  (cond
   (pos? ?mobility)
   nil ;; (println "immobile " ?joint-list ?mobility)

   (< 1 (reduce + 0 (for [x ?joint-list :when (= x [[:coincident :offset-z] 4])] 1)))
   nil ;; (println "not-closed : " ?joint-list ?mobility)

   (< 1 (reduce + 0 (for [x ?joint-list :when (some #(= % :in-plane) (first x))] 1)))
   nil ;; (println "in-plane : " ?joint-list ?mobility)

   :else [?joint-list ?mobility]))


(defn all-triple-loops
  "Returns a list of all possible three geom loops.
  Note that this list contains all cyclic and mirror
  variations of each loop.
  The evaluation of this procedure returns a list of 382 loops.
  These are all the cases that must be considered in the proof."
  []
  (for [j1 legal-joints
        j2 legal-joints
        j3 legal-joints
        :when (legal-loop?  [j1 j2 j3]
                     (- 12 (second j1) (second j2) (second j3)))]
    [j1 j2 j3]))



