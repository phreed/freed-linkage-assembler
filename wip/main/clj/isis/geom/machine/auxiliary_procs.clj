(ns isis.geom.machine.auxiliary-procs
  (:require [isis.geom.machine
             [functions :refer [vec-scale]]]))

(defn unit-vec
  "Returns a unit vector with the same direction as ?vector."
  [?vector]
  (vec-scale ?vector (/ 1.0 (mag ?vector))) )

(defn zero?
  "Returns 'true' if ?quantity is zero."
  [?quantity]
  (equal? 0.0 ?quantity) )

(defn on-surface?
  "Returns 'true' if ?point lies on ?surface."
  [?point ?surface]
  (zero? (perp-dist ?point ?surface)) )

(defn parallel?
  "Returns 'true' if ?axis-1 and ?axis-2 are parallel.
  If ?direction-matters is 'true', then the axis
  must also be pointed in the same direction.
  If ?direction-maters is 'false', then the axis
  may be either parallel or anti-parallel."
  [?axis-1 ?axis-2 ?direction-matters]
  (let [r0 (unit-vec ?axis-1)
        r1 (unit-vec ?axis-2)
        r2 (inner-prod r0 r1)]
    (if ?direction-matters
      (equal? r2 1)
      (equal? (mag r1) 1))) )



(defn r2:a
  "Procedure to rotate body ?link about axes ?axis_1 and ?axis-2,
  keeping the position of point ?center invariant, so as to
  move the ?link about axis by ?angle."
  [?link ?center ?angle ?axis ?axis-1 ?axis-2]
  (let [r0 (circle ?center ?axis 10)
        r1 (a-point r0)
        _  (rotate ?link ?center ?axis ?angle)
        r2 (copy r1)
        _ (rotate ?link ?center ?axis (- ?angle))]
    (2r:p->p ?link ?center r1 r2 ?axis-1 ?axis-2)))






(defn t1-r1:p->p:point-lf
  "This case is for when ?point is on ?link, and ?line is invariant."
  [?link ?point ?line ?axis ?from-point ?to-point ?branch]
  (let [r0 (vec-diff ?to-point ?from-point)
        _ (translate ?link r0)
        r1 (line ?to-point ?axis)
        r2 (perp-base ?point r1)
        r3 (circle r2 ?axis (perp-dist ?point r2))
        r4 (intersect ?line r3 ?branch)]
    (if-not (point? r4)
      (error (perp-dist ?line r3) estring-1)
      (1r:p->p ?link ?from-point ?point r4 ?axis ?axis-1 ?axis-2))) )


(defn t1-r1:p->p:line-lf
  "This case is for when ?line is on ?link, and ?point is invariant."
  [?link ?point ?line ?axis ?from-point ?to-point ?branch]
  (let [r0 (vec-diff ?from-point ?to-point)
        r1 (copy ?line)
        _ (translate ?link r0)
        r2 (line ?from-point ?axis)
        r3 (perp-base ?point r2)
        r4 (mag (vec-diff ?point r3))
        r5 (circle r3 ?axis r4)
        r6 (intersect r1 r5 ?branch)]
    (if-not (point? r6)
      (error (perp-dist r1 r5) estring-7)
      ;;; ERRATA the ?point below was point?
      (r1:p->p ?link ?to-point r6 ?point ?axis ?axis-1 ?axis-2))) )

(defn t1-r1:p->p
  "Procedure to rotate body ?link about ?axis and
  translate body ?link along ?line,
  thus moving ?freom-point to ?link to globally-fixed ?to-point.
  Rotation is done so as to not violate restrictions imposed
  by ?axis-1 and ?axis-2, if they exist."
  [?link ?point ?line ?axis ?from-point ?to-point ?lf ?branch]

  ((if (equal? ?point ?lf) t1-r1:p->p:point-lf  t1-r1:p->p:line-lf )
   ?link ?point ?line ?axis ?axis-1 ?axis-2) )


(defn t2-r1:p->p:point-lf
  "This case is for when ?point is on ?link, and ?plane is invariant."
  [?link ?point ?plane ?axis ?axis-1 ?axis-2 ?from-point ?to-point ?branch]

  (let [r0 (vec-diff ?to-point ?from-point)
        _ (translate ?link r0)
        r1 (line ?to-point ?axis)
        r2 (perp-base ?point r1)
        r3 (circle r2 ?axis (perp-dist ?point r2))
        r4 (intersect ?plane r3 ?branch)]
    (if-not (point? r4)
      (error (perp-dist ?plane r3) estring-1)
      (r1:p->p ?link ?from-point ?point r4 ?axis ?axis-1 ?axis-2))))


(defn t2-r1:p->p:plane-lf
  "This case is for when ?plane is on ?link, and ?point is invariant."
  [?link ?point ?plane ?axis ?axis-1 ?axis-2 ?from-point ?to-point ?branch]

  (let [r0 (vec-diff ?to-point ?from-point)
        _ (translate ?link r0)
        r1 (normal ?plane)
        r2 (plane ?point r1)
        r3 (perp-dist ?from-point ?point)
        r4 (circle ?from-point ?axis r3)
        r5 (intersect r2 r4 ?branch)]
    (if-not (point? r5)
      (error (perp-dist r2 r4) estring-7)
      ;;; ERRATA the ?point below was point?
      (r1:p->p ?link ?from-point r5 ?point ?axis ?axis-1 ?axis-2))))


(defn t2-r1:p->p
  "Procedure to rotate body ?link about ?axis and
  translate body ?link along ?plane,
  thus moving ?freom-point to ?link to globally-fixed ?to-point.
  Rotation is done so as to not violate restrictions imposed
  by ?axis-1 and ?axis-2, if they exist."
  [?link ?point ?plane ?axis ?axis-1 ?axis-2 ?from-point ?to-point ?lf ?branch]

  ((if (equal? ?point ?lf) t2-r1:p->p:point-lf  t2-r1:p->p:plane-lf )
   ?link ?point ?plane ?axis ?axis-1 ?axis-2 ?from-point ?to-point ?branch) )

(defn t1-r2:p->p:point-lf
  "This case is for when ?point is on ?link, and ?line is invariant."
  [?link ?point ?line ?axis-1 ?axis-2 ?from-point ?to-point ?branch]
  (let [_ (translate ?link (vec-diff ?to-point ?from-point))
        r0 (sphere ?from-point (perp-dist ?freom-point ?point))
        r1 (intersect r0 ?line ?branch)]
    (if-not (point? r1)
      (error (perp-dist r0 ?line) estring-1)
      (r2:p->p ?link ?from-point ?point r1 ?axis-1 ?axis-2))))


(defn t1-r2:p->p:line-lf
  "This case is for when ?line is on ?link, and ?point is invariant."
  [?link ?point ?line ?axis-1 ?axis-2 ?from-point ?to-point ?branch]
  (let [r0 (vec-diff ?to-point ?from-point)
        r2 (copy ?line)
        _ (translate ?link r0)
        r3 (perp-dist ?from-point ?point)
        r4 (sphere ?from-point r3)
        r5 (intersect r2 r4 ?branch)]
    (if-not (point? r5)
      (error (perp-dist r2 r4) estring-7)
      (r2:p->p ?link ?from-point r5 ?point ?axis-1 ?axis-2))))


(defn t1-r2:p->p
  "Procedure to translate ?link to bring ?M2 into coincidence with ?M1,
  followed by rotating ?link about ?axis-2 and ?axis-1 to bring ?point back onto ?line.
  In general there are two distinct solutions to this problem,
  so a branch variable ?branch is used to select the desired solution."
  [?link ?point ?line ?axis-1 ?axis-2 ?from-point ?to-point ?lf ?branch]

  ((if (equal? ?point ?lf) t1-r2:p->p:point-lf  t1-r2:p->p:line-lf )
   ?link ?point ?line ?axis-1 ?axis-2 ?from-point ?to-point ?branch) )


(defn t1-r3:p->p:point-lf
  "This case is for when ?point is on ?link, and ?line is invariant"
  [?link ?point ?line ?from-point ?to-point ?branch]

  (let [_ (translate ?link (vec-diff ?to-point ?from-point))
        r0 (sphere ?from-point (perp-dist ?from-point ?point))
        r1 (intersect r0 ?line ?branch)]
    (if-not (point? r1)
      (error (perp-dist r0 ?line) estring-7)
      (r3:p->p ?link ?from-point ?point r1))))
;; ERRATA r2 (vec-diff r1 ?from-point)

(defn t1-r3:p->p:line-lf
  "This case is for when ?line is on ?link, and ?point is invariant"
  [?link ?point ?line ?from-point ?to-point ?branch]

  (let [r0 (vec-diff ?to-point ?from-point)
        r2 (copy ?line)
        _ (translate ?link r0)
        r3 (perp-dist ?from-point ?point)
        r4 (sphere ?from-point r3)
        r5 (intersect r2 r4 ?branch)]
    (if-not (point? r5)
      (error (perp-dist r2 r4) estring-7)
      (r3:p->p ?link ?from-point r5 ?point ))))
;; ERRATA r6 (vec-diff r5 ?from-point)

(defn t1-r3:p->p
  "Procedure to translate ?link to bring ?M-2 into coincidence with ?M-1,
  followed by rotating ?link to brint ?point back onto ?line.
  In general, there are two distinct solutions to this problem,
  so a branch variable ?branch is used to select the desired solution."
  [?link ?point ?line ?from-point ?to-point ?lf ?branch]


  ((if (equal? ?point ?lf) t1-r3:p->p:point-lf  t1-r3:p->p:line-lf )
   ?link ?point ?line ?from-point ?to-point ?branch) )


(def t2-r2:p->p:point-lf
  "This case is for when ?point is on ?link and ?plane is invariant."
  [?link ?point ?plane ?axis-1 ?axis-2 ?from-point ?to-point]

  (let [_ (translate ?link (vec-diff ?to-point ?from-point))
        r0 (sphere ?from-point ?point)
        r1 (intersect  r0 ?plane 0)]
    (if-not (circle? r1)
      (error (purp-dist r0 ?plane) estring-7)
      (r2:p->p ?link ?from-point ?point r2 ?axis-1 ?axis-2))))
;; ERRATA r3 (perp-base ?from-point ?plane) r4 (vec-diff r3 ?from-point)

(defn t2-r2:p->p:plane-lf
  "This case is for when ?plane is on ?link, and ?point is invariant"
  [?link ?point ?plane ?axis-1 ?axis-2 ?from-point ?to-point]

  (let [r0 (vec-diff ?to-point ?from-point)
        _ (translate ?link r0)
        r1 (normal ?plane)
        r2 (plane ?point r1)
        r3 (perp-dist ?from-point ?point)
        r4 (sphere ?from-point r3)
        ;; ERRATA ?branch is not defined
        r5 (intersect r2 r4 ?branch)]
    (if-not (circle? r5)
      (error (perp-dist r2 r4) estring-7)
      (let [r6 (a-point r5)]
        (r2:p->p ?link ?from-point r5 ?point ?axis-1 ?axis-2)))))
;; ERRATA r7 (perp-base ?from-point ?plane) r8 (vec-diff r7 ?from-point)


(defn t2-r2:p->p
  "Procedure to translate ?link to bring ?M-2 into coincidence with ?M-1,
  followed by rotating ?link to brint ?point back onto ?line."
  [?link ?point ?plane ?axis-1 ?axis-2 ?from-point ?to-point ?lf]


  ((if (equal? ?point ?lf) t2-r2:p->p:point-lf  t2-r2:p->p:plane-lf )
   ?link ?point ?line ?from-point ?to-point ?branch) )

