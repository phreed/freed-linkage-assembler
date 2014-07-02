(ns isis.geom.machine.auxiliary
  "The geometric movement functions."
  (:require [isis.geom.machine
              [error-string :as emsg]
              [functions  :refer [vec-scale
                                  vec-diff
                                  mag
                                  error
                                  cross-prod]]]))

(defn dof-1r_p->p
  "Procedure to rotate body ?geom about ?axis in such a way
  as to not violate restrictions imposed by ?axis-1 and
  ?axis-2, if they exist.
  The procedure keeps the position of point ?center invariant,
  and moves ?from-point on ?geom to globally-fixed ?to-point."
  [?geom ?center ?from-point ?to-point ?axis ?axis-1 ?axis-2])

(defn dof-3r_p->p
  "Procedure to rotate body ?geom about ?center,
  moving ?from-point on ?geom to globally-fixed ?to-point.
  Done by constructing a rotational axis and calling dof-1r_p->p."
  [?geom ?center ?from-point ?to-point]
    (let [r0 (vec-diff ?from-point ?center)
        r1 (vec-diff ?to-point ?center)
        r2 (mag r0)
        r3 (mag r1)]
    (if-not (= r2 r3)
      (error (- r2 r3) emsg/emsg-4)
      (let [r4 (cross-prod r0 r1)]
        (dof-1r_p->p ?geom ?center ?from-point ?to-point r4 nil nil)))) )
