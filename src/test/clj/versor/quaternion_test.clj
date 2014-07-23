(ns versor.quaternion-test
  "Sample assembly for messing with quaternions."
  (:require [expectations :refer :all]
            [isis.geom.machine.geobj :as ga]))


(let [quat [0.7071067811865475
            0.0 0.0 -0.7071067811865475]
      v0 [1 2 3]
      v1 (ga/quat-sandwich quat v0)
      va (ga/quat-sandwich (ga/quat-conj quat) v1)
      v2 (ga/quat-sandwich quat v1)
      v3 (ga/quat-sandwich quat v2)
      v4 (ga/quat-sandwich quat v3)
      v5 (ga/quat-sandwich quat v4)]
  (expect  '[1.9999999999999996 -0.9999999999999998 2.9999999999999996]  v1)
  (expect  '[0.9999999999999997 1.9999999999999993 2.9999999999999987]  va)
  (expect  '[-0.9999999999999997 -1.9999999999999993 2.9999999999999987] v2)
  (expect  '[-1.9999999999999993 0.9999999999999996 2.9999999999999982]  v3)
  (expect  '[0.9999999999999993 1.9999999999999991 2.999999999999998]  v4)
  (expect  '[1.9999999999999987 -0.9999999999999992 2.999999999999997] v5) )
