(ns isis.geom.new-action-dispatch
  "Is it possible to have a single dispatch functions for performing actions?"
  )


(defn- constraint-attempt-dispatch
  "The function which specifies which implementation to use."
  [ikb constraint]
  (let [[ctype m1 m2] constraint
        [[m1-link-name _] _] m1
        [[m2-link-name _] _] m2
        m1-link @(m1-link-name (:l ikb))
        m2-link @(m2-link-name (:l ikb))]
    [ctype
     :m1 [:tdof (:# (:tdof m1-link))
          :rdof (:# (:rdof m1-link))]
     :m2 [:tdof (:# (:tdof m2-link))
          :rdof (:# (:rdof m2-link))]]))

(defn- constraint-attempt?<-default
  [ikb constraint]
  nil)

(defmulti constraint-attempt?
  "Make invariant one or more properties of the indicated marker.
  Update the geometry invariant to indicate the new DoF predicates."
  #'constraint-attempt-dispatch
  :default constraint-attempt?<-default)
