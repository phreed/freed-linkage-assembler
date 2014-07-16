(ns isis.geom.action.auxiliary
  "The geometric movement functions."
  (:require [isis.geom.machine
             [error-msg :as emsg]
             [tolerance :as tol]
             [geobj
              :refer [vec-scale vec-diff mag
                      outer-prod perp-base
                      vec-angle parallel? point?
                      null? rotate perp-dist
                      line intersect plane sphere]]]))




(defn dof-2r:p->p
  "Procedure to rotate body ?link, about axes ?axis-1 and ?axis-2,
  leaving the position of point ?center invariant, and
  moving ?from-point on ?link to globally fixed ?to-point.
  The ?branch indicates which of the possible solutions to choose."
  [?link ?center ?from-point ?to-point ?axis-1 ?axis-2 ?branch]
  (let [r0 (line ?center ?axis-2)
        r1 (line ?center ?axis-1)
        r2 (perp-base ?from-point r0)
        r3 (perp-base ?to-point r1)
        r4 (intersect (plane r2 ?axis-2) (plane r3 ?axis-1) 0)
        r5 (sphere ?center (perp-dist ?to-point ?center))
        r6 (intersect r5 r4 ?branch)]
    (if-not (point? r6)
      (emsg/e-mark-place-ic perp-dist r5 r4)
      (let [r7 (vec-diff ?from-point r3)
            r8 (vec-diff r6 r2)
            r9 (vec-angle r7 r8 (outer-prod r7 r8))
            r10 (vec-diff r6 r3)
            r11 (vec-diff ?to-point r3)
            r12 (vec-angle r10 r11 (outer-prod r10 r11)) ]
        (rotate ?link ?center ?axis-1 r12)))) )

(defn dof-1r:p->p
  "Procedure to rotate body ?link about ?axis.
  If restrictions are imposed by ?axis-1 and ?axis-2, they are honored.
  The procedure keeps the position of point ?center invariant,
  and moves ?from-point on ?link to globally-fixed ?to-point."
  [?link ?center ?from-point ?to-point ?axis ?axis-1 ?axis-2]
  (let [pivot (perp-base ?to-point (line ?center ?axis))
        to-diff (vec-diff ?to-point pivot)
        from-diff (vec-diff ?from-point pivot)]
    (if-not (tol/near-equal? :default (mag to-diff) (mag from-diff))
      (emsg/e-dim-oc ?from-point ?to-point ?center ?axis)
      (let [possible-axis (outer-prod to-diff from-diff)]
        (cond (tol/near-zero? :default possible-axis)
              ?link ;; no change

              (not (parallel? possible-axis ?axis false))
              (emsg/emsg-3 possible-axis ?axis)

              :else
              (if (and (nil? ?axis-1) (nil? ?axis-2))
                (rotate ?link pivot ?axis
                        (vec-angle from-diff to-diff ?axis))
                (dof-2r:p->p ?link ?center
                             ?from-point ?to-point ?axis-1 ?axis-2 1)))))))

(defn dof-3r:p->p
  "Procedure to rotate body ?link about ?center,
  moving ?from-point on ?link to globally-fixed ?to-point.
  Done by constructing an arbitrary rotational axis and calling dof-1r:p->p."
  [?link ?center ?from-point ?to-point]
  (let [from-diff (vec-diff ?from-point ?center)
        to-diff (vec-diff ?to-point ?center)]
    (cond (tol/near-same? :default from-diff to-diff)
          ?link

          (not (tol/near-equal? :default (mag from-diff) (mag to-diff)))
          (emsg/e-dim-oc ?from-point ?to-point ?center nil)

          :else
          (dof-1r:p->p ?link ?center ?from-point ?to-point
                       (outer-prod from-diff to-diff) nil nil))))

