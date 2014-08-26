(ns isis.geom.algebra.c3ga
   "The functions for developing versors."
  (:require [isis.geom.algebra [versor :as versor]]))


(def c3ga (versor/versor->create
  {
   :conformal true
   :bases #{:s :e1 :e2 :e3 :e4 :e5 }
   :metric [1  1  1  1  -1]
   :types
   { :vec {:name 'univector
           :bases #{:e1 :e2 :e3} }
     :biv {:name 'bivector
           :bases #{:e12 :e13 :e23} }
     :tri {:name 'trivector
           :bases #{:e123} }
     :pss {:name 'pseudoscalar
           :bases #{:e12345} }
     :rot {:name 'rotor
           :bases #{:s :e12 :e13 :e23} }
     :pnt {:name 'point
           :bases #{:e1 :e2 :e3 :e4 :e+} :dual true }
     :dlp {:name 'dual-point
           :bases #{:e1 :e2 :e3 :e5} :dual true }
     :pln {:name 'plane
           :bases #{:e1235 :e1245 :e1345 :e2345 } }
     :sph {:name 'sphere
           :bases #{:e1235 :e1234 :e1245 :e1345 :e2345} }
     :dll {:name 'dual-line
           :bases #{:e12 :e13 :e23 :e15 :e25 :e35} :dual true }
     :lin {:name 'line
           :bases #{:e145 :e245 :e345 :e125 :e135 :e235} }
     :flp {:name 'flat-point
           :bases #{:e15 :e25 :e35 :e45} }
     :par {:name 'point-pair
           :bases #{:e12 :e13 :e23 :e14 :e24 :e34 :e15 :e25 :e35 :e45} :dual true }
     :cir {:name 'circle
           :bases #{:e123 :e145 :e245 :e345 :e124 :e134 :e234 :e125 :e135 :e235} }
     :bst {:name 'booster
           :bases #{:s :e12 :e13 :e23 :e14 :e24 :e34 :e15 :e25 :e35 :e45} }
     :dil {:name 'dilator
           :bases #{:s :e45} }
     :mot {:name 'motor
           :bases #{:s :e12 :e13 :e23 :e15 :e25 :e35 :e1235} }
     :trs {:name 'translator
           :bases #{:s :e15 :e25 :e35} }
     :drv {:name 'drv
           :bases #{:e15 :e25 :e35} }
     :drb {:name 'drb
           :bases #{:e125 :e135 :e235} }
     :drt {:name 'drt
           :bases #{:e1235} }
     :tnv {:name 'tnv
           :bases #{:e14 :e24 :e34} } } }))

(defn cosh [versor]
  (+ (.exp Math versor) (/ (.exp Math (- versor)) 2.0)))

(defn sinh [versor]
  (- (.exp Math versor) (/ (.exp Math (- versor)) 2.0)))

;; (def no ((:e4 c3ga) 1))
;; (def ni ((:e5 c3ga) 1))

