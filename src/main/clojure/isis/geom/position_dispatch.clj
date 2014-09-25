(ns isis.geom.position-dispatch
  "The dispatch functions for performing actions."
  (:require  [clojure.pprint :as pp]) )



(defmulti constraint-attempt?
  "Attempt to make invariant one or more properties
  of the links referenced by the indicated markers.
  Update the geometry invariant to indicate the
  realized DoF predicates and  other invariants.

  Return - nil or false indicating that the
  attempt failed and the constraint could not be
  satisfied.  Any other result indicates that the
  constraint has been applied."
  (fn [kb constraint] (:type constraint))

  :default
  (fn [kb constraint]
    (pp/pprint "constraint-attempt-default")
    nil))

(defmacro defmethod-asymetric-transform
  "Generate the asymetric defmethods for the multifn.
  e.g.
  (defmethod transform!
  {:tdof 0 :rdof 0 :motive :fixed}
  [kb point line motive] (fixed/transform!->t0-r0 kb m1 m2))"
  [multifn]
  `(do
     ~@(for [tdof [0 1 2 3]
             rdof [0 1 2 3]
             motive [:fixed :mobile]]
         `(defmethod ~multifn
            {:tdof ~tdof :rdof ~rdof :motive ~motive}
            [~'kb ~'m1 ~'m2 ~'motive]
            (~(symbol (str (name motive) "/transform!->t" tdof "-r" rdof))
              ~'kb ~'m1 ~'m2 )))))

(defmacro defmethod-symetric-transform
  "Generate the symetric defmethods for the multifn.
  e.g.
  (defmethod transform!
  {:tdof 0 :rdof 0}
  [kb point line motive] (xlice/transform!->t0-r0 kb m1 m2))"
  [multifn]
  `(do
     ~@(for [tdof [0 1 2 3]
             rdof [0 1 2 3]]
         `(defmethod ~multifn
            {:tdof ~tdof :rdof ~rdof}
            [~'kb ~'m1 ~'m2]
            (~(symbol (str "xlice/transform!->t" tdof "-r" rdof))
              ~'kb ~'m1 ~'m2 )))))

(defn unimpl
  "Print a message indicating that the transform is not implemented"
  [xform nspace kb m1 m2]
  (let [[[m1-link-name _] _] m1
        [[m2-link-name _] _] m2
        m1-link @(get-in kb [:link m1-link-name])
        m2-link @(get-in kb [:link m2-link-name])]
    (pp/pprint [(str "not-implemented " nspace " " xform)
                "m1" m1 m1-link
                "m2" m2 m2-link]) ))

