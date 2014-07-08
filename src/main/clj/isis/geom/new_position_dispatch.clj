(ns isis.geom.new-position-dispatch
  "Is it possible to have a single dispatch functions for performing actions?"
  )


(defn- constraint-attempt-dispatch
  "The function which specifies which implementation to use."
  [ikb constraint]
  (let [[ctype [[m1-link-name _] _] [[m2-link-name _] _] m2] constraint
        m1-link @(get-in ikb [:link m1-link-name])
        m2-link @(get-in ikb [:link m2-link-name])]
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
