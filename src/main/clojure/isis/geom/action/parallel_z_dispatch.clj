(ns isis.geom.action.parallel-z-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action.parallel-z-slice :as xlice]
            [clojure.pprint :as pp]))


(defn precondition?
  "Associated with each constraint type is a function which
  checks the preconditions.  If one of the markers has
  a fixed direction then the constrained
  marker is placed in the first position.
  The focus is on the constrained marker so that
  overconstrained conditions are still checked."
  [kb m1 m2]
  (cond (invariant/marker-direction? kb m1) [m1 m2]
        (invariant/marker-direction? kb m2) [m2 m1]
        :else nil))


(defmulti assemble!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  (fn [kb m1 m2]
    (let [[[link-name _] _] m2
          link @(get (:link kb) link-name)
          tdof (get-in link [:tdof :#])
          rdof (get-in link [:rdof :#]) ]
      #_(pp/pprint [":parallel-z assemble!"
                  (str tdof ":" rdof )
                  "constrained-dir" m1 "unconstrained-dir" m2])
      {:tdof tdof :rdof rdof}))
  :default nil)


(defmethod ms/constraint-attempt?
  :parallel-z
  [kb constraint]
  (let [{m1 :m1 m2 :m2} constraint
        precon (precondition? kb m1 m2) ]
    (when precon
      (pp/fresh-line)
      (let [[ma mb] precon
            new-link (assemble! kb ma mb)]
        (pp/pprint ["new-xform" new-link ])
        new-link)
      true)))

(ms/defmethod-symetric-transform assemble!)
