(ns isis.geom.action.offset-x-slice
  "The table of rules."
  (:require [clojure.pprint :as pp]
            [isis.geom.position-dispatch :as ms]
            [isis.geom.model.invariant :as invariant]
            [isis.geom.action [offset-x-slice :as xlice]]))


(defn precondition
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [kb m1 m2]
  (cond (and (invariant/marker-direction? kb m1)
             (invariant/marker-direction? kb m2)
             (invariant/marker-twist? kb m1)) [kb m1 m2]
        :else nil))


(defn- assemble-dispatch [kb m1 m2]
  (let [[[link-name _] _] m2
        link @(get (:link kb) link-name)
        tdof (get-in link [:tdof :#])
        rdof (get-in link [:rdof :#]) ]
    {:tdof tdof :rdof rdof}))

(defmulti assemble!
  "Transform the links and kb so that the constraint is met.
  Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  assemble-dispatch
  :default nil)


(defmethod ms/constraint-attempt?
  :offset-x
  [kb constraint]
  (let [{m1 :m1 m2 :m2} constraint
        precon (precondition kb m1 m2) ]
    (if-not precon
      :pre-condition-not-met
      (let [[kb+ m1+ m2+] precon]
      (try
        (ms/show-constraint kb+ "offset-angle:  "
                         assemble-dispatch
                         m1+ m2+)
        (assemble! kb m1+ m2+)

        (catch Exception ex
          (ms/dump ex assemble-dispatch
                   "offset-x" kb+ m1+ m2+)
          :exception-thrown ))))) )

(ms/defmethod-symetric-transform assemble!)
