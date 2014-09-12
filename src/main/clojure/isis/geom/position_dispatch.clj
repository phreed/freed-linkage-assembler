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

