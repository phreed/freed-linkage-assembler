(ns versor.rotate-test
  "Sample assembly for rotate (and translate)."
  (:require [expectations :refer :all]
            [isis.geom.machine
             [geobj :as ga]
             [tolerance :as tol]]))

;; imitation of dof-1r:p->p

(let [tau (* 2.0 (. Math PI))
      tau-1:8 (/ tau 8.0)
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

  (expect {:versor {:xlate [2.0 0.0 0.0]
                    :rotate [(Math/cos tau-1:8)
                              0.0 0.0 (- (Math/sin tau-1:8))]}}
          link)
  (expect '{:type :line :e [2.0 0.0 0.0] :d [1.0 0.0 0.0]} a-line)
  (expect '[2.0 0.0 0.0] pivot)
  (expect '[0.0 4.0 0.0] to-dir)
  (expect '[0.0 0.0 4.0] from-dir)
  (expect '[-1.0 0.0] angle)
  (expect '{:versor {:xlate [2.0 0.0 0.0]
                :rotate [0.5000000000000001 ;; cos(120/2)
                         -0.5
                         -0.4999999999999999
                         -0.5]}}
     new-link))



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

  (expect '{:type :line :e [5.0 0.0 0.0]
            :d [-0.7295372041400852
                -0.5471529031050638
                -0.4103646773287979]} a-line)
  (expect '[5.0 0.0 0.0] pivot)
  (expect '[-3.0 4.0 0.0] to-dir)
  (expect '[0.0 -3.0 4.0] from-dir)
  (expect '[0.8772684879784524 -0.48] angle)
  (expect '{:versor
            {:xlate [2.1476923076923073
                     0.11076923076923095
                     0.9230769230769234]
             :rotate [0.5099019513592785
                      -0.6275716324421889
                      -0.47067872433164165
                      -0.3530090432487313]}}
     new-link))

;; mimic dof-1r:p->p
(let [link {:versor {:xlate [2.0 0.0 0.0]
                     :rotate
                     [0.7071067811865475
                      0.0 0.0 -0.7071067811865475]}}
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

  (expect '{:type :line :e [5.0 0.0 0.0]
            :d [-1.0 0.0 0.0]} a-line)
  (expect '[2.0 0.0 0.0] pivot)
  (expect '[0.0 4.0 0.0] to-dir)
  (expect '[0.0 0.0 4.0] from-dir)
  (expect '[1.0 0.0] angle)
  (expect '{:versor
            {:xlate [2.0 0.0 0.0]
             :rotate [0.5
                      -0.4999999999999999
                      -0.4999999999999999
                      -0.5]}}
     new-link))

;; mimic dof-1r:p->p (180 degree rotation)
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

  (expect
   '{:type :line
     :e [3467.85 43.0687 302.5]
     :d [-0.24559880542553506
         -0.7613702967510761
         0.5999970816585097]} axis-line)

  (expect #(tol/near-same? :default '[3459.991 18.7046 321.700] %) pivot)
  (expect #(tol/near-same? :default '[-4.420757 -13.7046 -19.200105] %) to-dir)
  (expect #(tol/near-same? :default '[4.43826 13.70483 19.19386] %) from-dir)
  (expect #(tol/near-same? :default '[-0.00077 -1.0] %) angle)
  (expect
   '{:versor
     {:xlate [12315.409884054448 -4259.980462766835 903.0008418663099]
      :rotate [4.996795758810402E-5
               -0.8894325067016122
               0.45706653009419507
               -2.4358612537453417E-5]}}
   new-link) )
