(ns isis.geom.action-dispatch
  "the dispatch functions for performing actions."
  )



(defn- transform-dispatch
  "the function which specifies which implementation to use."
  [constraint invariants]
  (:type constraint) )

(defmulti precondition?->transform!
  "Make invariant one or more properties of the indicated marker.
  Update the geometry invariant to indicate the new DoF predicates."
  #'transform-dispatch
  :default nil)

