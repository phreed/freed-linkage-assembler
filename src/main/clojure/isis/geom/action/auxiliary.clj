(ns isis.geom.action.auxiliary
  "The geometric movement functions."
  (:require [isis.geom.machine
             [error-msg :as emsg]
             [tolerance :as tol]
             [geobj :as ga]]))

(defn r2:p->p
  "Procedure to rotate body ?link, about axes ?axis-1 and ?axis-2,
  leaving the position of point ?center invariant, and
  moving ?from-point on ?link to globally fixed ?to-point.
  The ?branch indicates which of the possible solutions to choose."
  [?link ?center ?from-point ?to-point ?axis-1 ?axis-2 ?branch]
  (let [r0 (ga/line ?center ?axis-2)
        r1 (ga/line ?center ?axis-1)
        r2 (ga/projection ?from-point r0)
        r3 (ga/projection ?to-point r1)
        r4 (ga/meet (ga/plane r2 ?axis-2) (ga/plane r3 ?axis-1) 0)
        r5 (ga/sphere ?center (ga/rejection ?to-point ?center))
        r6 (ga/meet r5 r4 ?branch)]
    (if-not (ga/point? r6)
      (emsg/mark-place-ic ga/rejection r5 r4)
      (let [r7 (ga/vec-diff ?from-point r3)
            r8 (ga/vec-diff r6 r2)
            r9 (ga/vec-angle r7 r8 (ga/outer-prod r7 r8))
            r10 (ga/vec-diff r6 r3)
            r11 (ga/vec-diff ?to-point r3)
            r12 (ga/vec-angle r10 r11 (ga/outer-prod r10 r11)) ]
        (ga/rotate ?link ?center ?axis-1 r12)))) )

(defn r2:a
  "Procedure to rotate body ?link about axes ?axis_1 and ?axis-2,
  keeping the position of point ?center invariant, so as to
  move the ?link about axis by ?angle."
  [?link ?center ?angle ?axis ?axis-1 ?axis-2]
  (let [arbitrary-circle (ga/circle ?center ?axis 10)
        pnt-on-circle (ga/a-point arbitrary-circle) ]
    (dosync
     (alter ?link merge
            (ga/rotate ?link ?center ?axis ?angle))
     ;; r2 (ga/copy pnt-on-circle)

     (alter ?link merge
            (ga/rotate ?link ?center ?axis (- ?angle)))

     (r2:p->p ?link ?center pnt-on-circle pnt-on-circle
              ?axis-1 ?axis-2) )))


(defn r1:p->p
  "Procedure to rotate body ?link about ?axis.
  If restrictions are imposed by ?axis-1 and ?axis-2, they are honored.
  The procedure keeps the position of point ?center invariant,
  and moves ?from-point on ?link to globally-fixed ?to-point."
  [?link ?center ?from-point ?to-point ?axis ?axis-1 ?axis-2]
  (let [pivot (ga/projection ?to-point (ga/line ?center ?axis))
        from-dir (ga/vec-diff ?from-point pivot)
        to-dir (ga/vec-diff ?to-point pivot)]
    (cond (tol/near-same? :default from-dir to-dir)
          ?link

          (not (tol/near-equal? :default (ga/norm to-dir) (ga/norm from-dir)))
          (emsg/dim-oc ?from-point ?to-point ?center ?axis)

          :else
          (let [possible-axis
                (ga/outer-prod (ga/normalize to-dir)
                               (ga/normalize from-dir))]
            (cond
             (and (not (tol/near-zero? :default possible-axis))
                  (not (ga/parallel? possible-axis ?axis false)))
             (emsg/inconst-rot possible-axis ?axis)

             :else
             (if (and (nil? ?axis-1) (nil? ?axis-2))
               (ga/rotate ?link pivot ?axis
                          (ga/vec-angle from-dir to-dir ?axis))
               (r2:p->p ?link ?center
                        ?from-point ?to-point ?axis-1 ?axis-2 1)))))))

(defn r3:p->p
  "Procedure to rotate body ?link about ?center,
  moving ?from-point on ?link to globally-fixed ?to-point.
  Done by constructing an arbitrary rotational axis and calling r1:p->p."
  [?link ?center ?from-point ?to-point]
  (let [from-dir (ga/vec-diff ?from-point ?center)
        to-dir (ga/vec-diff ?to-point ?center)]
    (cond (tol/near-same? :default from-dir to-dir)
          ?link

          (not (tol/near-equal? :default (ga/norm from-dir) (ga/norm to-dir)))
          (emsg/dim-oc ?from-point ?to-point ?center nil)

          :else
          (r1:p->p ?link ?center ?from-point ?to-point
                   (ga/outer-prod from-dir to-dir) nil nil))))

