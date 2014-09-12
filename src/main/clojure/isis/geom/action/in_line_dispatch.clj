(ns isis.geom.action.in-line-dispatch
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant :as invariant] ))


(defn in-line->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  [m1 m2 inv]
  (cond (invariant/marker? inv m1 :loc)  m2
        (and (invariant/marker? inv m2 :loc)
             (invariant/marker? inv m2 :dir))
        m1))
