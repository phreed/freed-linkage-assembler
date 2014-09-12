(ns isis.geom.action.offset-x-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant] ))


(defn offset-x->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (when (and (invariant/marker? inv m1 :dir)
             (invariant/marker? inv m2 :dir)
             (invariant/marker? inv m1 :twist))
    m2))




