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
      to-vec (ga/vec-diff to-point pivot)
      from-vec (ga/vec-diff from-point pivot)
      angle (ga/vec-angle from-vec to-vec axis)
      new-link (ga/rotate link center axis angle)]

  (expect {:versor {:xlate [2.0 0.0 0.0]
                    :rotate [(Math/cos tau-1:8)
                              0.0 0.0 (- (Math/sin tau-1:8))]}}
          link)
  (expect '{:type :line :e [2.0 0.0 0.0] :d [1.0 0.0 0.0]} a-line)
  (expect '[2.0 0.0 0.0] pivot)
  (expect '[0.0 4.0 0.0] to-vec)
  (expect '[0.0 0.0 4.0] from-vec)
  (expect '[-1.0 0.0] angle)
  (= '{:versor {:xlate [2.0 0.0 0.0]
                :rotate [0.5000000000000001 ;; cos(120/2)
                         -0.5
                         -0.4999999999999999
                         -0.5000000000000001]}}
     new-link))
