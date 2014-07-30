(ns versor.projection-test
  "Test the projection of versors"
  (:require [midje.sweet :refer [facts fact]]

            [isis.geom.machine.geobj :as ga]))

(let [a-point [2.0 4.0 0.0]
      the-center [5.0 0.0 0.0]
      an-axis [-16.0 -12.0 -9.0]
      a-line (ga/line the-center an-axis)
      the-pivot (ga/projection a-point a-line)]
  (facts "construction of projection objects"
         (fact "check the formation of lines"
               a-line =>
               '{:type :line, :e [5.0 0.0 0.0],
                 :d [-0.7295372041400852 -0.5471529031050638 -0.4103646773287979]})

         (fact "about the projection of a point onto a line"
               the-pivot =>
               '[5.0 1.0 0.0])))
