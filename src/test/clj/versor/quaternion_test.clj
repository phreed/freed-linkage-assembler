(ns versor.quaternion-test
  "Sample assembly for messing with quaternions."
  (:require [expectations :refer :all]
            [isis.geom.machine
             [geobj :as ga]
             [tolerance :as tol]]))


(let [quat [0.7071067811865475
            0.0 0.0 -0.7071067811865475]
      v0 [1 2 3]
      v1 (ga/quat-sandwich quat v0)
      va (ga/quat-sandwich (ga/quat-conj quat) v1)
      v2 (ga/quat-sandwich quat v1)
      v3 (ga/quat-sandwich quat v2)
      v4 (ga/quat-sandwich quat v3)
      v5 (ga/quat-sandwich quat v4)]
  (expect (partial tol/near-same? :default '[2.0 -1.0 3.0]) v1)
  (expect (partial tol/near-same? :default v0) va)
  (expect (partial tol/near-same? :default '[-1.0 -2.0 3.0]) v2)
  (expect (partial tol/near-same? :default '[-2.0 1.0 3.0]) v3)
  (expect (partial tol/near-same? :default v0) v4)
  (expect (partial tol/near-same? :default v1) v5) )

;; in imitation of the 0-3-coincident
(let [ao [-8649.51 4688.51 600.0]
      q1 [1.0 0.0 0.0 0.0]
      xp1 [12117.36 -4645.4413 -297.5]
      af [3467.85 43.0687 302.5]

      bo [-8625.71 4720.65 570.0]
      q2 [0.12956794323176976
          -0.27457530013159515
          -0.5335246272397006
          -0.7894124554205311]
      xp2 [-6096.966798571706 2121.699201037429 1464.2762736936115]
      bf [3455.57 5.0 332.5]
      axis [-0.24559880542553506 -0.7613702967510761 0.5999970816585097]
      ]
  (expect (partial tol/near-same? :default ao)
          (ga/quat-sandwich q1 ao))
  (expect (partial tol/near-same? :default af)
          (ga/vec-sum xp1 (ga/quat-sandwich q1 ao)))
  (expect (partial tol/near-same? :default ao)
          (ga/quat-sandwich (ga/quat-conj q1) (ga/vec-diff af xp1)))
  (expect (partial tol/near-same? :default af)
          (ga/vec-sum xp2 (ga/quat-sandwich q2 ao)))
  (expect (partial tol/near-same? :default bf)
          (ga/vec-sum xp2 (ga/quat-sandwich q2 bo)))
  (expect #(ga/parallel? axis % false)
          (ga/vec-diff bf af))
  (expect (partial tol/near-same? :tiny q2)
          (ga/quat-normalize q2))
  )
