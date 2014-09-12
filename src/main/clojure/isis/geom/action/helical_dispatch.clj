(ns isis.geom.action.helical-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant] ))



(defn helical->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (when (and (invariant/marker? inv m1 :z)
             (invariant/marker? inv m2 :z)
             (invariant/marker? inv m1 :x))
    m2))


