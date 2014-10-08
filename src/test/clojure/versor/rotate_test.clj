(ns versor.rotate-test
  "Sample assembly for rotate (and translate)."
  (:require [midje.sweet :as tt]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.machine [tolerance :as tol]]))

(def ^:private tau (* 2.0 Math/PI))

(let [tau-1:8 (/ tau 8.0)
      link {:versor {:xlate [2.0 0.0 0.0]
                     :rotate (ga/axis-angle->quaternion
                              [0 0 -1] [1 0])}}
      center [2.0 0.0 0.0]
      from-point [2.0 0.0 4.0]
      to-point [2.0 4.0 0.0]
      axis [1.0 0.0 0.0]

      a-line (ga/line center axis)
      pivot (ga/projection to-point a-line)
      to-dir (ga/vec-diff to-point pivot)
      from-dir (ga/vec-diff from-point pivot)
      angle (ga/vec-angle from-dir to-dir axis)
      new-link (ga/rotate link center axis angle)]

  (tt/facts "imitation of dof/r1:p->p"
         (tt/fact "link check"
               link =>
               {:versor {:xlate [2.0 0.0 0.0]
                          :rotate [(Math/cos tau-1:8)
                                   0.0 0.0 (- (Math/sin tau-1:8))]}})
         (tt/fact "line check "
               a-line => '{:e [2.0 0.0 0.0] :d [1.0 0.0 0.0]} )
         (tt/fact "pivot check" pivot => '[2.0 0.0 0.0])
         (tt/fact "direction check" to-dir => '[0.0 4.0 0.0])
         (tt/fact "from direction check" from-dir => '[0.0 0.0 4.0])
         (tt/fact "angle check" angle => '[-1.0 0.0])
         (tt/fact "versor check"
               new-link =>
                  '{:versor {:rotate [0.5000000000000001 -0.5
                                      -0.4999999999999999 -0.5]
                             :xlate [2.0 0.0 0.0]}}  )))



(let [link {:versor {:xlate [5.0 -3.0 0.0]
                     :rotate [1.0 0.0 0.0 0.0]}}
      center [5.0 0.0 0.0]
      from-point [5.0 -3.0 4.0]
      to-point [2.0 4.0 0.0]
      axis [-16.0 -12.0 -9.0]

      a-line (ga/line center axis)
      pivot (ga/projection to-point a-line)
      to-dir (ga/vec-diff to-point pivot)
      from-dir (ga/vec-diff from-point pivot)
      angle (ga/vec-angle from-dir to-dir axis)
      new-link (ga/rotate link center axis angle)]

  (tt/facts "various geomtry tests")
  (tt/fact "line test" a-line =>
         {:d [-0.7295372041400852
              -0.5471529031050638
              -0.4103646773287979]
          :e [5.0 0.0 0.0]} )
  (tt/fact "pivot test" pivot => '[5.0 0.0 0.0])
  (tt/fact "to direction test" to-dir => '[-3.0 4.0 0.0])
  (tt/fact "from direction test" from-dir => '[0.0 -3.0 4.0])
  (tt/fact "angle test" angle =>  [0.8772684879784524 -0.48])
  (tt/fact "link position test" new-link =>
        {:versor
          {:xlate [2.1476923076923073
                   0.11076923076923095
                   0.9230769230769234]
           :rotate [0.5099019513592785
                    -0.6275716324421889
                    -0.47067872433164165
                    -0.3530090432487313]}} ))

(let [link {:versor {:xlate [2.0 0.0 0.0]
                     :rotate
                     [0.70710678
                      0.0 0.0 -0.70710678]}}
      center [5.0 0.0 0.0]
      from-point [2.0 0.0 4.0]
      to-point [2.0 4.0 0.0]
      axis [-1.0 0.0 0.0]

      a-line (ga/line center axis)
      pivot (ga/projection to-point a-line)
      to-dir (ga/vec-diff to-point pivot)
      from-dir (ga/vec-diff from-point pivot)
      angle (ga/vec-angle from-dir to-dir axis)
      new-link (ga/rotate link pivot axis angle)]

  (tt/facts "mimic dof/r1:p->p"
         (tt/fact "" a-line =>
               '{:e [5.0 0.0 0.0]
                 :d [-1.0 0.0 0.0]})
         (tt/fact "" pivot => '[2.0 0.0 0.0])
         (tt/fact "" to-dir => '[0.0 4.0 0.0])
         (tt/fact "" from-dir => '[0.0 0.0 4.0])
         (tt/fact "" angle => '[1.0 0.0])
         (tt/fact "" new-link =>
                  {:versor {:rotate [0.49999999916098425
                                     -0.4999999991609842
                                     -0.4999999991609842
                                     -0.49999999916098425]
                            :xlate [2.0 0.0 0.0]}}) ))

(let [link {:versor
            {:xlate [-6096.966798571706
                     2121.699201037429
                     1464.2762736936115]
             :rotate [0.12956794323176976
                      -0.27457530013159515
                      -0.5335246272397006
                      -0.7894124554205311]}}
      inv-pnt [3467.85 43.0687 302.5]
      inv-axis [-0.24559880542553506 -0.7613702967510761 0.5999970816585097]

      from-point [-8625.71 4720.65 600.0]
      to-point [3455.57 5.0 302.5]

      axis-line (ga/line inv-pnt inv-axis)
      pivot (ga/projection to-point axis-line)
      to-dir (ga/vec-diff to-point pivot)

      from-dir (ga/vec-diff
                (ga/vec-sum
                 (get-in link [:versor :xlate])
                 (ga/quat-sandwich
                  (get-in link [:versor :rotate])
                  from-point))
                pivot)
      angle (ga/vec-angle from-dir to-dir inv-axis)
      new-link (ga/rotate link pivot inv-axis angle)]

  (tt/facts "mimic dof/r1:p->p (180 degree rotation)"
         (tt/fact "" axis-line =>
               '{:e [3467.85 43.0687 302.5]
                 :d [-0.24559880542553506
                     -0.7613702967510761
                     0.5999970816585097]})

         (tt/fact "" pivot =>  #(tol/near-same? :default '[3459.991 18.7046 321.700] %) )
         (tt/fact "" to-dir => #(tol/near-same? :default '[-4.420757 -13.7046 -19.200105] %))
         (tt/fact "" from-dir => #(tol/near-same? :default '[4.43826 13.70483 19.19386] %))
         (tt/fact "" angle => #(tol/near-same? :default '[-0.00077 -1.0] %))
         (tt/fact "" new-link =>
               '{:versor
                 {:xlate [12315.409884054448 -4259.980462766835 903.0008418663099]
                  :rotate [4.996795758810402E-5
                           -0.8894325067016122
                           0.45706653009419507
                           -2.4358612537453417E-5]}}) ))
