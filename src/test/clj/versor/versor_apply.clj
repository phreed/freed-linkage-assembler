(ns versor.versor-apply
  "Sample assembly for rotate (and translate)."
  (:require [expectations :refer :all]
            [isis.geom.machine.geobj :as ga]))

(let [brick-versor {:xlate [2.0 0.0 0.0] :rotate [0.5 -0.5 -0.5 -0.5]}
      brick-local [0.0 3.0 0.0]]
  (expect
   '[5.0 0.0 0.0]
   (ga/versor-apply brick-versor brick-local)))

