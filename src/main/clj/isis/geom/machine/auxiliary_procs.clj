(ns isis.geom.machine.auxiliary-procs
  (:require (isis.geom.machine functions [:refer vec-scale])))

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
        r2 (dot-prod r0 r1)]
    (if ?direction-matters
      (equal? r2 1)
      (equal? (mag r1) 1))) )

(defn dof-2r_p-p
  "Procedure to rotate body ?geom, about axes ?axis-1 and ?axis-2,
  leaving the position of point ?center invariant, and
  moving ?from-point on ?geom to globally fixed ?to-point."
  [?geom ?center ?from-point ?to-point ?axis_1 ?axis_2 ?q]
  (let [r0 (line ?center ?axis-2)
        r1 (line ?center ?axis-1)
        r2 (perp-base ?from-point r0)
        r3 (perp-base ?to-point r1)
        r4 (intersect (plane r2 ?axis-2) (plane r3 ?axis-1) 0)
        r5 (sphere ?center (perp-dist ?to-point ?center))
        r6 (intersect r5 r4 ?q)]
    (if-not (point? r6)
      (error (perp-dist r5 r4) estring-7)
      (let [r7 (vec-diff ?from-point r3)
            r8 (vec-diff r6 r2)
            r9 (vec-angle r7 r8 (cross-prod r7 r8))
            r10 (vec-diff r6 r3)
            r11 (vec-diff ?to-point r3)
            r12 (vec-angle r10 r11 (cross-prod r10 r11)) ]
        (rotate ?geom ?center ?axis-1 r12)))) )

(defn dof-2r_a
  "Procedure to rotate body ?geom about axes ?axis_1 and ?axis-2,
  keeping the position of point ?center invariant, so as to
  move the ?geom about axis by ?angle."
  [?geom ?center ?angle ?axis ?axis-1 ?axis-2]
  (let [r0 (circle ?center ?axis 10)
        r1 (a-point r0)
        _  (rotate ?geom ?center ?axis ?angle)
        r2 (copy r1)
        _ (rotate ?geom ?center ?axis (- ?angle))]
    (2r_p-p ?geom ?center r1 r2 ?axis-1 ?axis-2)))


(defn dof-1r_p-p
  "Procedure to rotate body ?geom about ?axis in such a way
  as to not violate restrictions imposed by ?axis-1 and
  ?axis-2, if they exist.
  The procedure keeps the position of point ?center invariant,
  and moves ?from-point on ?geom to globally-fixed ?to-point."
  [?geom ?center ?from-point ?to-point ?axis ?axis-1 ?axis-2]
  (let [r0 (perp-base to-point (line center axis))
        r1 (vec-diff to-point r0)
        r2 (vec-diff from-point r0)
        r3 (- (mag r1) (mag r2))]
    (if-not (zero? r3)
      (error r3 estring-4)
      (let [r4 (cross-prod r1 r2)]
        (if-not (parallel? r4 ?axis false)
          (error (vec-angle r4 ?axis
                            (cross-prod r4 ?axis)) estring-3)
          (let [r5 (vec-angle r2 r1 ?axis)]
            (if (and (null? ?axis_1) (null? ?axis-2))
              (rotate ?geom ?center ?axis r5)
              (2r_p-p ?geom ?center ?from-point ?to-point ?axis-1 ?axis-2))))))) )


(defn dof-3r_p-p
  "Procedure to rotate body ?geom about ?center,
  moving ?from-point on ?geom to globally-fixed ?to-point.
  Done by constructing a rotational axis and calling 1r_p-p."
  [?geom ?center ?from-point ?to-point]
  (let [r0 (vec-diff ?from-point ?center)
        r1 (vec-diff ?to-point ?center)
        r2 (mag r0)
        r3 (mag r1)]
    (if-not (equal? r2 r3)
      (error (- r2 r3) estring-4)
      (let [r4 (cross-prod r0 r1)]
        (1r_p-p ?geom ?center ?from-point ?to-point r4 nil nil)))) )

(defn dof-1t-1r_p-p_point-lf
  "This case is for when ?point is on ?geom, and ?line is invariant."
  [?geom ?point ?line ?axis ?from-point ?to-point ?q]
  (let [r0 (vec-diff ?to-point ?from-point)
        _ (translate ?geom r0)
        r1 (line ?to-point ?axis)
        r2 (perp-base ?point r1)
        r3 (circle r2 ?axis (perp-dist ?point r2))
        r4 (intersect ?line r3 ?q)]
    (if-not (point? r4)
      (error (perp-dist ?line r3) estring-1)
      (1r_p-p ?geom ?from-point ?point r4 ?axis ?axis-1 ?axis-2))) )


(defn dof-1t-1r_p-p_line-lf
  "This case is for when ?line is on ?geom, and ?point is invariant."
  [?geom ?point ?line ?axis ?from-point ?to-point ?q]
  (let [r0 (vec-diff ?from-point ?to-point)
        r1 (copy ?line)
        _ (translate ?geom r0)
        r2 (line ?from-point ?axis)
        r3 (perp-base ?point r2)
        r4 (mag (vec-diff ?point r3))
        r5 (circle r3 ?axis r4)
        r6 (intersect r1 r5 ?q)]
    (if-not (point? r6)
      (error (perp-dist r1 r5) estring-7)
      ;;; ERRATA the ?point below was point?
      (1r_p-p ?geom ?to-point r6 ?point ?axis ?axis-1 ?axis-2))) )

(defn dof-1t-1r_p-p
  "Procedure to rotate body ?geom about ?axis and
  translate body ?geom along ?line,
  thus moving ?freom-point to ?geom to globally-fixed ?to-point.
  Rotation is done so as to not violate restrictions imposed
  by ?axis-1 and ?axis-2, if they exist."
  [?geom ?point ?line ?axis ?from-point ?to-point ?lf ?q]

  ((if (equal? ?point ?lf) dof-lt-1r_p-p_point-lf  dof-lt-1r_p-p_line-lf )
   ?geom ?point ?line ?axis ?axis-1 ?axis-2) )


(defn dof-2t-1r_p-p_point-lf
  "This case is for when ?point is on ?geom, and ?plane is invariant."
  [?geom ?point ?plane ?axis ?axis-1 ?axis-2 ?from-point ?to-point ?q]

  (let [r0 (vec-diff ?to-point ?from-point)
        _ (translate ?geom r0)
        r1 (line ?to-point ?axis)
        r2 (perp-base ?point r1)
        r3 (circle r2 ?axis (perp-dist ?point r2))
        r4 (intersect ?plane r3 ?q)]
    (if-not (point? r4)
      (error (perp-dist ?plane r3) estring-1)
      (1r_p-p ?geom ?from-point ?point r4 ?axis ?axis-1 ?axis-2))))


(defn dof-2t-1r_p-p_plane-lf
  "This case is for when ?plane is on ?geom, and ?point is invariant."
  [?geom ?point ?plane ?axis ?axis-1 ?axis-2 ?from-point ?to-point ?q]

  (let [r0 (vec-diff ?to-point ?from-point)
        _ (translate ?geom r0)
        r1 (normal ?plane)
        r2 (plane ?point r1)
        r3 (perp-dist ?from-point ?point)
        r4 (circle ?from-point ?axis r3)
        r5 (intersect r2 r4 ?q)]
    (if-not (point? r5)
      (error (perp-dist r2 r4) estring-7)
      ;;; ERRATA the ?point below was point?
      (1r_p-p ?geom ?from-point r5 ?point ?axis ?axis-1 ?axis-2))))


(defn dof-2t-1r_p-p
  "Procedure to rotate body ?geom about ?axis and
  translate body ?geom along ?plane,
  thus moving ?freom-point to ?geom to globally-fixed ?to-point.
  Rotation is done so as to not violate restrictions imposed
  by ?axis-1 and ?axis-2, if they exist."
  [?geom ?point ?plane ?axis ?axis-1 ?axis-2 ?from-point ?to-point ?lf ?q]

  ((if (equal? ?point ?lf) dof-2t-1r_p-p_point-lf  dof-2t-1r_p-p_plane-lf )
   ?geom ?point ?plane ?axis ?axis-1 ?axis-2 ?from-point ?to-point ?q) )

(defn dof-1t-2r_p-p_point-lf
  "This case is for when ?point is on ?geom, and ?line is invariant."
  [?geom ?point ?line ?axis-1 ?axis-2 ?from-point ?to-point ?q]
  (let [_ (translate ?geom (vec-diff ?to-point ?from-point))
        r0 (sphere ?from-point (perp-dist ?freom-point ?point))
        r1 (intersect r0 ?line ?q)]
    (if-not (point? r1)
      (error (perp-dist r0 ?line) estring-1)
      (dof-2r_p-p ?geom ?from-point ?point r1 ?axis-1 ?axis-2))))


(defn dof-1t-2r_p-p_line-lf
  "This case is for when ?line is on ?geom, and ?point is invariant."
  [?geom ?point ?line ?axis-1 ?axis-2 ?from-point ?to-point ?q]
  (let [r0 (vec-diff ?to-point ?from-point)
        r2 (copy ?line)
        _ (translate ?geom r0)
        r3 (perp-dist ?from-point ?point)
        r4 (sphere ?from-point r3)
        r5 (intersect r2 r4 ?q)]
    (if-not (point? r5)
      (error (perp-dist r2 r4) estring-7)
      (dof-2r_p-p ?geom ?from-point r5 ?point ?axis-1 ?axis-2))))


(defn dof-1t-2r_p-p
  "Procedure to translate ?geom to bring ?M2 into coincidence with ?M1,
  followed by rotating ?geom about ?axis_2 and ?axis_1 to bring ?point back onto ?line.
  In general there are two distinct solutions to this problem,
  so a branch variable ?q is used to select the desired solution."
  [?geom ?point ?line ?axis-1 ?axis-2 ?from-point ?to-point ?lf ?q]

  ((if (equal? ?point ?lf) dof-1t-2r_p-p_point-lf  dof-1t-2r_p-p_line-lf )
   ?geom ?point ?line ?axis-1 ?axis-2 ?from-point ?to-point ?q) )


(defn dof-1t-3r_p-p_point-lf
  "This case is for when ?point is on ?geom, and ?line is invariant"
  [?geom ?point ?line ?from-point ?to-point ?q]

  (let [_ (translate ?geom (vec-diff ?to-point ?from-point))
        r0 (sphere ?from-point (perp-dist ?from-point ?point))
        r1 (intersect r0 ?line ?q)]
    (if-not (point? r1)
      (error (perp-dist r0 ?line) estring-7)
      (dof-3r_p-p ?geom ?from-point ?point r1))))
;; ERRATA r2 (vec-diff r1 ?from-point)

(defn 1t-3r_p-p_line-lf
  "This case is for when ?line is on ?geom, and ?point is invariant"
  [?geom ?point ?line ?from-point ?to-point ?q]

  (let [r0 (vec-diff ?to-point ?from-point)
        r2 (copy ?line)
        _ (translate ?geom r0)
        r3 (perp-dist ?from-point ?point)
        r4 (sphere ?from-point r3)
        r5 (intersect r2 r4 ?q)]
    (if-not (point? r5)
      (error (perp-dist r2 r4) estring-7)
      (dof-3r_p-p ?geom ?from-point r5 ?point ))))
;; ERRATA r6 (vec-diff r5 ?from-point)

(defn dof-1t-3r_p-p
  "Procedure to translate ?geom to bring ?M-2 into coincidence with ?M-1,
  followed by rotating ?geom to brint ?point back onto ?line.
  In general, there are two distinct solutions to this problem,
  so a branch variable ?q is used to select the desired solution."
  [?geom ?point ?line ?from-point ?to-point ?lf ?q]


  ((if (equal? ?point ?lf) dof-1t-3r_p-p_point-lf  dof-1t-3r_p-p_line-lf )
   ?geom ?point ?line ?from-point ?to-point ?q) )


(def dof-2t-2r_p-p_point-lf
  "This case is for when ?point is on ?geom and ?plane is invariant."
  [?geom ?point ?plane ?axis-1 ?axis-2 ?from-point ?to-point]

  (let [_ (translate ?geom (vec-diff ?to-point ?from-point))
        r0 (sphere ?from-point ?point)
        r1 (intersect  r0 ?plane 0)]
    (if-not (circle? r1)
      (error (purp-dist r0 ?plane) estring-7)
      (2r_p-p ?geom ?from-point ?point r2 ?axis-1 ?axis-2))))
;; ERRATA r3 (perp-base ?from-point ?plane) r4 (vec-diff r3 ?from-point)

(defn dof-2t-2r_p-p_plane-lf
  "This case is for when ?plane is on ?geom, and ?point is invariant"
  [?geom ?point ?plane ?axis-1 ?axis-2 ?from-point ?to-point]

  (let [r0 (vec-diff ?to-point ?from-point)
        _ (translate ?geom r0)
        r1 (normal ?plane)
        r2 (plane ?point r1)
        r3 (perp-dist ?from-point ?point)
        r4 (sphere ?from-point r3)
        ;; ERRATA ?q is not defined
        r5 (intersect r2 r4 ?q)]
    (if-not (circle? r5)
      (error (perp-dist r2 r4) estring-7)
      (let [r6 (a-point r5)]
        (dof-2r_p-p ?geom ?from-point r5 ?point ?axis-1 ?axis-2)))))
;; ERRATA r7 (perp-base ?from-point ?plane) r8 (vec-diff r7 ?from-point)


(defn dof-2t-2r_p-p
  "Procedure to translate ?geom to bring ?M-2 into coincidence with ?M-1,
  followed by rotating ?geom to brint ?point back onto ?line."
  [?geom ?point ?plane ?axis-1 ?axis-2 ?from-point ?to-point ?lf]


  ((if (equal? ?point ?lf) dof-2t-2r_p-p_point-lf  dof-2t-2r_p-p_plane-lf )
   ?geom ?point ?line ?from-point ?to-point ?q) )

