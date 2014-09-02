(ns versor.quaternion-test
  "Sample assembly for messing with quaternions."
  (:require [midje.sweet :refer [facts fact roughly]]

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
      v5 (ga/quat-sandwich quat v4)

      is-approximately #(partial tol/near-same? :default %)]
  (facts "concerning quaternion sandwich"
         (fact "simple rotate" v1 => (is-approximately '[2.0 -1.0 3.0]))
         (fact "return to original" va => (is-approximately v0))
         (fact "rotate 1/4 turn" v2 => (is-approximately '[-1.0 -2.0 3.0]))
         (fact "rotate 1/2 turn" v3 => (is-approximately '[-2.0 1.0 3.0]))
         (fact "rotate full turn" v4 => (is-approximately v0))
         (fact "rotate 1-1/4 turn" v5 => (is-approximately v1)) ))

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

      is-approximately #(partial tol/near-same? :default %)
      ]
  (facts "in imitation of the 0-3-coincident"
         (fact "quat sandwich a0"
               (ga/quat-sandwich q1 ao) => (is-approximately ao))

         (fact "quat sandwich xp1"
               (ga/vec-sum xp1 (ga/quat-sandwich q1 ao)) => (is-approximately af))

         (fact "quat sandwich diff"
               (ga/quat-sandwich (ga/quat-conj q1) (ga/vec-diff af xp1)) => (is-approximately ao))
         (fact ""
               (ga/vec-sum xp2 (ga/quat-sandwich q2 ao)) => (is-approximately af))
         (fact ""
               (ga/vec-sum xp2 (ga/quat-sandwich q2 bo)) => (is-approximately bf))
         (fact ""
               (ga/vec-diff bf af) => #(ga/parallel? axis % false))
         (fact ""
               (ga/quat-normalize q2) => (partial tol/near-same? :tiny q2)) ))

(let [ao [-8625.71 4720.65 600.0]
      aq [0.12956794323176976
          -0.27457530013159515
          -0.5335246272397006
          -0.7894124554205311]
      ax [-6096.966798571706 2121.699201037429 1464.2762736936115]
      af [3464.429 32.4094 340.8940]
      inv-pnt [3467.85 43.0687 302.5]
      inv-axis [-0.24559880542553506 -0.7613702967510761 0.5999970816585097]
      at [3455.57 5.0 302.5]
      pivot [3459.991 18.7046 321.700]
      af-dir (ga/vec-diff af pivot)
      at-dir (ga/vec-diff at pivot)

      is-approximately #(partial tol/near-same? :default %)
      ]
  (facts "in imitation of the 0-1-coincident"
         (fact ""
               (ga/vec-sum ax (ga/quat-sandwich aq ao)) => (is-approximately af))
         (fact ""
               (ga/projection at (ga/line inv-pnt inv-axis)) => (is-approximately pivot))
         (fact ""
               (ga/projection at (ga/line pivot inv-axis)) => (is-approximately pivot))
         (fact ""
               (ga/projection af (ga/line pivot inv-axis)) => (is-approximately pivot))
         (fact ""
               (ga/outer-prod (ga/normalize at-dir) (ga/normalize af-dir)) =>
               #(tol/near-zero? :default %))
         (fact ""
               (ga/vec-angle af-dir at-dir inv-axis) =>
               #(tol/near-same? :default [0.0 -1.0] %))
         ))
