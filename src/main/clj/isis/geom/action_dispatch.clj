(ns isis.geom.action-dispatch
  "the dispatch functions for performing actions."
  )



(defn- precondition-dispatch
  "the function which specifies which implementation to use."
  [constraint invariants]
  (:type constraint) )

(defmulti precondition?
  "Associated with each constraint type is a function which
  checks the preconditions are satisfied."
  #'precondition-dispatch
  :default nil)



(defn- transform-dispatch
  "the function which specifies which implementation to use."
  [constraint invariants]
  (:type constraint) )

(defmulti transform!
  "Make invariant one or more properties of the indicated marker.
  Update the geometry invariant to indicate the new DoF predicates."
  #'transform-dispatch
  :default nil)



(defn- assert-postcondition-dispatch
  "the function which specifies which implementation to use."
  [constraint invariants]
  (:type constraint) )

(defmulti assert-postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  #'assert-postcondition-dispatch
  :default nil)
