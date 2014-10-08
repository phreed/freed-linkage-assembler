(ns versor.geobj-meet-test
  "Sample intersection tests."
  (:require [midje.sweet :as tt]
            [isis.geom.algebra [geobj :as ga]]))

(let [s1 (ga/plane [2.0  2.0   2.0] [-1.0 0.0 0.0])
      s2 (ga/plane [2.0  2.0   2.0] [ 0.0 0.0 1.0])
      s3 (ga/plane [0.0 10.0 -50.0] [ 0.0 1.0 0.0])]
  (tt/facts "meet three planes"
         (tt/fact "using ga/versor-apply"
               (ga/meet s1 s2 s3) => '[2.0 10.0 2.0])))
