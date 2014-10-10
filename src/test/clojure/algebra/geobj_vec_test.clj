(ns algebra.geobj-vec-test
  "Sample ."
  (:require [midje.sweet :as tt]
            [isis.geom.algebra [geobj :as ga]]))

(tt/facts
 "checking geobj vec-angle function"
 (tt/fact
  "degenerate"
  (ga/vec-angle [0.0 0.0 0.0] [0.0 0.0 0.0] [0.0 0.0 1.0]) =>
  [0.0 1.0] ) )

(tt/facts
 "checking geobj vec-diff function"
 (tt/fact
  "degenerate"
  (ga/vec-diff [0.0 0.0 0.0] [0.0 0.0 0.0]) =>
  [0.0 0.0 0.0] ) )

(tt/facts
 "checking geobj normalize function"
 (tt/fact
  "degenerate"
  (ga/normalize [0.0 0.0 0.0]) => nil?))
