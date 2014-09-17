(ns isis.geom.position-dispatch
  "The dispatch functions for performing actions."
  (:require  [clojure.pprint :as pp]) )


(defn- constraint-attempt-dispatch
  "The function which specifies which implementation to use."
  [kb constraint]
  (:type constraint))

(defn- constraint-attempt-default
  [kb constraint]
  (pp/pprint "constraint-attempt-default")
  nil)

(defmulti constraint-attempt?
  "Make invariant one or more properties of the indicated marker.
  Update the geometry invariant to indicate the new DoF predicates."
  #'constraint-attempt-dispatch
  :default constraint-attempt-default)


(defmacro def-transform-asymetric-methods
  "generate the defmethods"
  []
  `(do
     ~@(for [tdof [0 1 2 3]
             rdof [0 1 2 3]
             motive [:fixed :mobile]]
         `(defmethod transform!
            {:tdof ~tdof :rdof ~rdof :motive ~motive}
            [kb# m1# m2# motive#]
            (~(symbol (str (name motive) "/transform!->t" tdof "-r" rdof))
            kb# m1# m2# )))))
