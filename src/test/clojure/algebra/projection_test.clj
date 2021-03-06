(ns algebra.projection-test
  "Test the projection of versors"
  (:require [midje.sweet :as tt]
            [isis.geom.algebra [geobj :as ga]]))

(let [
      the-center [5.0 0.0 0.0]
      an-axis [-16.0 -12.0 -9.0]
      a-line (ga/line the-center an-axis)]
  (tt/facts "construction of projection objects"
         (tt/fact "check the formation of lines"
               a-line =>
                {:e [5.0 0.0 0.0],
                 :d [-0.7295372041400852 -0.5471529031050638 -0.4103646773287979]})

         (tt/fact "about the projection of a point onto a line"
               (ga/projection [2.0 4.0 0.0] a-line) =>
               '[5.0 0.0 0.0])

         (tt/fact "about the projection of a point onto a line"
               (ga/projection (into [] (map + the-center an-axis)) a-line) =>
               '[-11.0 -12.0 -9.0])
         ))
