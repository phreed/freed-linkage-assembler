(ns isis.geom.algebra.c3ga-ro
  "The functions for working with rounds."
  (:require [isis.geom.algebra
             [c3ga :as master]
             [versor :as versor]]))

(defn point
  "A null vector : "
  [x y z]
  ((:pnt master/c3ga) x y z 1.0
   (/ (+ (* x x) (* y y) (* z z)) 2.0)))

(defn ipoint
  [x y z]
  ((:pnt master/c3ga) x y z -1.0
   (/ (+ (* x x) (* y y) (* z z)) 2.0)))

(defn dual-sphere
  [x y z r]
  (update-in
   (point x y z) [4]
   (fn [r]
     ((if (pos? r) - +)
      (* 0.5 r r)))))

(defn circle
  [cen dir r]
  (let [{cx :cx cy :cy cz :cz} cen]
    (versor/ip
     (dual-sphere cx cy cz r)
     (versor/op cen (versor/dual dir))) ))

(defn size
  [a]
  )
