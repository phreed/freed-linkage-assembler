(ns versor.rotate-test
  "Sample assembly for rotate (and translate)."
  (:require [expectations :refer :all]
            [isis.geom.machine.geobj :as ga]))

;; imitation of dof-1r:p->p

(let [tau (* 2.0 (. Math PI))
      tau-1:8 (/ tau 8.0)
      link {:versor {:xlate [2.0 0.0 0.0]
                     :rotate (#'ga/quat-exp [0 0 -1]
                                            (ga/half-angle [1 0]))}}
      center [2.0 0.0 0.0]
      from-point [2.0 0.0 4.0]
      to-point [2.0 4.0 0.0]
      axis [1.0 0.0 0.0]

      a-line (ga/line center axis)
      pivot (ga/perp-base to-point a-line)
      to-diff (ga/vec-diff to-point pivot)
      from-diff (ga/vec-diff from-point pivot)
      angle (ga/vec-angle from-diff to-diff axis)
      new-link (ga/rotate link center axis angle)]

  (expect {:versor {:xlate [2.0 0.0 0.0]
                    :rotate [(Math/cos tau-1:8)
                              0.0 0.0 (- (Math/sin tau-1:8))]}}
          link)
  (expect '{:type :line :e [2.0 0.0 0.0] :d [1.0 0.0 0.0]} a-line)
  (expect '[2.0 0.0 0.0] pivot)
  (expect '[0.0 4.0 0.0] to-diff)
  (expect '[0.0 0.0 4.0] from-diff)
  (expect '[-1.0 0.0] angle)
  (expect '{:versor {:xlate [2.0 0.0 0.0]
                :rotate [0.5000000000000001 ;; cos(120/2)
                         -0.5
                         -0.4999999999999999
                         -0.5]}}
     new-link))



(let [tau (* 2.0 (. Math PI))
      tau-1:8 (/ tau 8.0)
      link {:versor {:xlate [5.0 -3.0 0.0]
                     :rotate [1.0 0.0 0.0 0.0]}}
      center [5.0 0.0 0.0]
      from-point [5.0 -3.0 4.0]
      to-point [2.0 4.0 0.0]
      axis [-16.0 -12.0 -9.0]

      a-line (ga/line center axis)
      pivot (ga/perp-base to-point a-line)
      to-diff (ga/vec-diff to-point pivot)
      from-diff (ga/vec-diff from-point pivot)
      angle (ga/vec-angle from-diff to-diff axis)
      new-link (ga/rotate link center axis angle)]

  (expect '{:type :line :e [5.0 0.0 0.0]
            :d [-0.7295372041400852
                -0.5471529031050638
                -0.4103646773287979]} a-line)
  (expect '[5.0 0.0 0.0] pivot)
  (expect '[-3.0 4.0 0.0] to-diff)
  (expect '[0.0 -3.0 4.0] from-diff)
  (expect '[0.8772684879784524 -0.48] angle)
  (expect '{:versor
            {:xlate [3.7371284006643726
                     -0.46702702702702703
                     0.7878077681882626]
             :rotate [0.5099019513592785
                      -0.37199244398022174
                      -0.2789943329851663
                      -0.20924574973887472]}}
     new-link))
