(ns versor.projection-test
  "."
  (:require [expectations :refer :all]
            [isis.geom.machine.geobj :as ga]))

(let [a-point [2.0 4.0 0.0]
      the-center [5.0 0.0 0.0]
      an-axis [-16.0 -12.0 -9.0]
      a-line (ga/line the-center an-axis)
      the-pivot (ga/projection a-point a-line)]
  (expect
   '{:type :line, :e [5.0 0.0 0.0],
     :d [-0.7295372041400852 -0.5471529031050638 -0.4103646773287979]}
   a-line)

  (=
   '[2.8113883875797443 2.1886116124202553 0.0]
   the-pivot))
