(ns isis.geom.machine.auxiliary
  "The geometric movement functions."
  (:require [isis.geom.machine
             [error-string :as emsg]
             [tolerance :as tol]
             [geobj
              :refer [vec-scale vec-diff mag
                      error outer-prod perp-base
                      vec-angle parallel? point?
                      null? rotate perp-dist
                      line intersect plane sphere]]]))

(defn contextual-eval [ctx expr]
    (eval
        `(let [~@(mapcat (fn [[k v]] [k `'~v]) ctx)]
             ~expr)))

(defmacro local-context []
    (let [symbols (keys &env)]
        (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))

(defn readr [prompt exit-code]
    (let [input (clojure.main/repl-read prompt exit-code)]
        (if (= input ::tl)
            exit-code
             input)))

;;make a break point
(defmacro break []
  `(clojure.main/repl
    :prompt #(print "debug=> ")
    :read readr
    :eval (partial contextual-eval (local-context))))


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
      (error (perp-dist r5 r4) emsg/emsg-7)
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
  (let [r0 (perp-base ?to-point (line ?center ?axis))
        r1 (vec-diff ?to-point r0)
        r2 (vec-diff ?from-point r0)]
    (if-not (tol/near-equal? :default (mag r1) (mag r2))
      (error [" to-point: " ?to-point " from-point: " ?from-point
              " about-axis: " ?axis " about-center: " ?center] emsg/emsg-4)
      (let [r4 (outer-prod r1 r2)]
        (if-not (parallel? r4 ?axis false)
          (do (error (vec-angle r4 ?axis
                            (outer-prod r4 ?axis)) emsg/emsg-3))
          (let [r5 (vec-angle r2 r1 ?axis)]
            (if (and (nil? ?axis-1) (nil? ?axis-2))
              (rotate ?link ?center ?axis r5)
              (dof-2r:p->p ?link ?center ?from-point ?to-point ?axis-1 ?axis-2 1))))))))

(defn dof-3r:p->p
  "Procedure to rotate body ?link about ?center,
  moving ?from-point on ?link to globally-fixed ?to-point.
  Done by constructing an arbitrary rotational axis and calling dof-1r:p->p."
  [?link ?center ?from-point ?to-point]
  (let [r0 (vec-diff ?from-point ?center)
        r1 (vec-diff ?to-point ?center)]
    (cond (tol/near-same? :default r0 r1)
          {:e [0.0 0.0 0.0]}

          (not (tol/near-equal? :default (mag r0) (mag r1)))
          (error [" from: " ?from-point " to:" ?to-point " about: " ?center] emsg/emsg-4)

          :else
          (dof-1r:p->p ?link ?center ?from-point ?to-point
                       (outer-prod r0 r1) nil nil))))

