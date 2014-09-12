(ns isis.geom.action.auxiliary
  "The geometric movement functions."
  (:require [isis.geom.machine
             [error-msg :as emsg]
             [tolerance :as tol]
             [geobj
              :refer [vec-scale vec-diff
                      norm normalize
                      outer-prod projection
                      vec-angle parallel? point?
                      null? rotate rejection
                      line intersect plane sphere]]]))


(defn r2:p->p
  "Procedure to rotate body ?link, about axes ?axis-1 and ?axis-2,
  leaving the position of point ?center invariant, and
  moving ?from-point on ?link to globally fixed ?to-point.
  The ?branch indicates which of the possible solutions to choose."
  [?link ?center ?from-point ?to-point ?axis-1 ?axis-2 ?branch]
  (let [r0 (line ?center ?axis-2)
        r1 (line ?center ?axis-1)
        r2 (projection ?from-point r0)
        r3 (projection ?to-point r1)
        r4 (intersect (plane r2 ?axis-2) (plane r3 ?axis-1) 0)
        r5 (sphere ?center (rejection ?to-point ?center))
        r6 (intersect r5 r4 ?branch)]
    (if-not (point? r6)
      (emsg/mark-place-ic rejection r5 r4)
      (let [r7 (vec-diff ?from-point r3)
            r8 (vec-diff r6 r2)
            r9 (vec-angle r7 r8 (outer-prod r7 r8))
            r10 (vec-diff r6 r3)
            r11 (vec-diff ?to-point r3)
            r12 (vec-angle r10 r11 (outer-prod r10 r11)) ]
        (rotate ?link ?center ?axis-1 r12)))) )

(defn r1:p->p
  "Procedure to rotate body ?link about ?axis.
  If restrictions are imposed by ?axis-1 and ?axis-2, they are honored.
  The procedure keeps the position of point ?center invariant,
  and moves ?from-point on ?link to globally-fixed ?to-point."
  [?link ?center ?from-point ?to-point ?axis ?axis-1 ?axis-2]
  (let [pivot (projection ?to-point (line ?center ?axis))
        from-dir (vec-diff ?from-point pivot)
        to-dir (vec-diff ?to-point pivot)]
    (cond (tol/near-same? :default from-dir to-dir)
          ?link

          (not (tol/near-equal? :default (norm to-dir) (norm from-dir)))
          (emsg/dim-oc ?from-point ?to-point ?center ?axis)

          :else
          (let [possible-axis
                (outer-prod (normalize to-dir)
                            (normalize from-dir))]
            (cond
             (and (not (tol/near-zero? :default possible-axis))
                  (not (parallel? possible-axis ?axis false)))
             (emsg/inconst-rot possible-axis ?axis)

             :else
             (if (and (nil? ?axis-1) (nil? ?axis-2))
               (rotate ?link pivot ?axis
                       (vec-angle from-dir to-dir ?axis))
               (r2:p->p ?link ?center
                            ?from-point ?to-point ?axis-1 ?axis-2 1)))))))

(defn r3:p->p
  "Procedure to rotate body ?link about ?center,
  moving ?from-point on ?link to globally-fixed ?to-point.
  Done by constructing an arbitrary rotational axis and calling r1:p->p."
  [?link ?center ?from-point ?to-point]
  (let [from-dir (vec-diff ?from-point ?center)
        to-dir (vec-diff ?to-point ?center)]
    (cond (tol/near-same? :default from-dir to-dir)
          ?link

          (not (tol/near-equal? :default (norm from-dir) (norm to-dir)))
          (emsg/dim-oc ?from-point ?to-point ?center nil)

          :else
          (r1:p->p ?link ?center ?from-point ?to-point
                       (outer-prod from-dir to-dir) nil nil))))

