(ns isis.geom.machine.geobj
  (:require [isis.geom.machine
             [tolerance :as tolerance]]) )

(defn mag
  "If quantity is a vector, returns the magnitude of quantity.
  If quantity is a scalar, returns the absolute value of quantity."
  [quantity]
  (cond (number? quantity) (Math/abs quantity)
        (empty? quantity) 0.0
        (vector? quantity) (Math/sqrt (reduce #(+ %1 (* %2 %2)) 0.0 quantity))
        :else 0.0))

(defn unit-ize
  "make the object have size 1."
  [vect]
  (into [] (map #(/ % (mag vect)) vect)))

(defn point
  "No special typing on a point.
  It is simply a 3-tuple."
  [triple] triple)


(defn point?
  "Returns 'true' if object is a 'point'."
  [object]
  (cond (not (vector? object)) false
        (not (= 3 (count object))) false
        :else true))

(defn line
  "Returns a line object with direction axis passing through point."
  [point axis]
  {:type :line :e point, :d (unit-ize axis)})

(defn line?
  "Returns 'true' if object is a line."
  [object]
  (= :line (:type object)))



(defn inner-prod
  "Returns the dot product of vect-1 and vect-2."
   [vect-1 vect-2]
  (reduce + (map * vect-1 vect-2)))

(defn outer-prod
  "Returns the outer product of vect-1 and vect-2.
  This can be considered either a bivector or
  the perpendicular dual vector."
   [v1 v2]
  (let [[v1-1 v1-2 v1-3] v1
        [v2-1 v2-2 v2-3] v2 ]
    [(- (* v1-2 v2-3) (* v1-3 v2-2))
     (- (* v1-3 v2-1) (* v1-1 v2-3))
     (- (* v1-1 v2-2) (* v1-2 v2-1))]))

(defn outer-prod-3
  "Returns the outer product of vect-1 and vect-2.
  This can be considered either a bivector or
  the perpendicular dual vector."
   [v1 v2 v3]
  (inner-prod (outer-prod v1 v2) v3))



(defn plane
  "Create a plane object with normal vector passing through point."
  [point normal]
  {:type :plane :e point :n (unit-ize normal)})


(defn plane?
  "Returns 'true' if object is a 'plane'."
  [object]
  (= :plane (:type object)))


(defn a-point
  "Returns an 'arbitrary' point that lies on curve"
  [curve] )

(defn axis
  "Returns the vector axis of an object of type circle, line, cylinder, or helix"
  [object]
  (let [otype (:type object)]
    (cond (= otype :line) (:d object)
          (= otype :circle) (:a object)
          (= otype :cylinder) (:a object)
          (= otype :helix) (:a object)
          :else object)))

(defn centerline
  "Returns the centerline of rotation of a curve that is constrained by a
  rotational DOF, where curve in {screw, ellipse+r}.
  See the definition of screw and ellipse+r."
  [object]
  )

(defn circle
  "Returns a circle object with its center at point, and with
  specified axis vector and radius."
   [point axis radius]
  {:type :circle :e point :a axis :r radius})

(defn circle?
  "Returns 'true' if object is a circle."
   [object]
  (= :circle (:type object)))

(defn copy
  "Returns a copy of a geometric object, fixed in the
  global cooridinate frame."
   [object]
  (println "copy unimplemented")
  )


(defn cylinder
  "Return a cylinder object with axial line, whose circular
  profile is defined by axis and radius."
   [line axis radius]
  (println "cylinder unimplemented")
  )


(defn ellipse?
  "Returns 'true' if object is an ellipse."
   [object]
  (println "ellipse? unimplemented")
  )


(defn ellipse+r
  "Create an ellipse+r object that records an ellipse, a rotational axis and
  a length, to make an objec twith a single DOF (combined translational
  and rotational )."
  [ellipse axis length]
  (println "ellipse+r unimplemented")
)


(defn error
  "Signals a run-time error, caused by degeneracies or over-constratin.
  measure is an expression which is zero if there is no error, an non-zero oterwise.
  The magnitude of measuer increase iwht ht severity of the error.
  If ERROR-MODE is 'fatal' and measuer is greater tahn TOLERANCE, then
  string is returned with the error signal.
  If ERROR-MODE is 'accumlate' and measuer is greater than TOLERANCE,
  then ERROR-ACC is set to ERROR-ACC plus measure."
  [measure string]
  (println "error : " measure string) )


(defn equal?
  "Returns 'true' if quantity-1 equals quantity-2 within TOLERANCE.
  Quantities may be scalars, points, or vectors."
  [quantity-1 quantity-2]
  (println "equal? unimplemented")
  )

(defn gmp
  "marker position (in global coordinate frame)."
  [marker ikb]
  (let [[[link-name _] mp] marker
        me (get mp :e [0.0 0.0 0.0])
        link @(get-in ikb [:link link-name])
        lp (get-in link [:versor :e] [0.0 0.0 0.0])]
    (into [] (map + lp me))))

(defn gmx
  "marker x-axis vector (in global coordinate frame)."
  [marker ikb]
  (println "gmx unimplemented")
  )

(defn gmz
  "marker z-axis vector (in global coordinate frame)."
  [marker ikb]
  (println "gmz unimplemented")
  )

(defn helix
 "Returns a helix object with axial line defined by point
 and axis, and with specified radius and pitch."
 [point axis radius pitch]
  (println "helix unimplemented")
 )

(defn intersect-3-planes
 "Calculates the intersection of three planes."
  [a b c]
  (let [{a- :e, an :n} a
        {b- :e, bn :n} b
        {c- :e, cn :n} c
        anbncn (mag (outer-prod-3 an bn cn))
        a-bncn (mag (outer-prod-3 a- bn cn))
        anb-cn (mag (outer-prod-3 an b- cn))
        anbnc- (mag (outer-prod-3 an bn c-))]
    (if (zero? anbncn) nil
      [(/ a-bncn anbncn) (/ anb-cn anbncn) (/ anbnc- anbncn)])))


(defn intersect
 "Calculates the intersection of surface-1 and surface-2.
 Either surface may be zero-, one-, or two-dimensional.
 The branch argument determines which solution branch is used.
 Returns the null value (nil) if the surfaces do not intersect.

 For a closed form solution to two planes intesecting.
 Compute the intersection between 3 planes.
 The third plane uses either point from surface-1 or vector-2
 as its locating point and the cross product as the normal."
  [s1 s2 branch]
  (cond (and (plane? s1) (plane? s2))
        (let [axis (unit-ize (outer-prod (:n s1) (:n s2)))
              p1 (:e s1) p2 (:e s2)
              s3 (plane p1 axis)
              point (intersect-3-planes s1 s2 s3)]
          (line point axis))))


(defn inverse
  "Returns the inverse of a transform."
  [transform]
  (println "inverse unimplemented")
  )

(defn lcf
  "Returns the transform matrix for the local coordinate frame of the marker"
  [marker]
  (println "lcf unimplemented")
  )

(defn lmp
  "marker position in link coordinate frame."
  [marker]
  (println "lmp unimplemented")
  )

(defn lmx
  "marker x-axis position in link coordinate frame."
  [marker]
  (println "lmx unimplemented")
  )

(defn lmz
  "marker z-axis position in link coordinate frame."
  [marker]
  (println "lmz unimplemented")
  )


(defn modulo
  "Returs quantity modulo modulus"
  [quantity modulus]
  (println "modulo unimplemented")
  )

(defn normal
  "Returns the normal of a plane."
  [plane]
  (println "normal unimplemented")
  )

(defn null?
  "Returns 'true' if quantity has the nil value."
  [quantity]
  (println "null? unimplemented")
  )

(defn perp-dist
  "Distance between surface-1 and serface-2.
  The surfaces may be zero-, one-, or two-dimensional."
  [surface-1 surface-2]
  (println "perp-dist unimplemented")
  )

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
  (tolerance/small-scalar? :default (mag (outer-prod (unit-ize ?axis-1) (unit-ize ?axis-2)))))


(defn pc-check
  "For use with curve in {screw ellipse+r}.
  If translation and rotation are both non-null,
  return 'true' if the stranslation and rotation are
  consistent with curve parameters, and 'false' otherwise.
  If one of translation or rotation is nil, then
  return a value that is consistent with curve parameters.
  If multiple choices are possible, disambiguate with branch."
  [curve translation rotation branch]
  (println "pc-check unimplemented")
  )

(defn pc-error
  "Returns an error term for curve with inconsistent
  translation and rotation."
  [curve translation rotation]
  (println "pc-error unimplemented")
  )

(defn pc-locus
  "Returns the locus of locus-point, which is on the
  same rigid body as constrained-point consstrained to
  lie on curve."
  [curve constrained-point locus-point]
  (println "pc-locus unimplemented")
  )

(defmulti perp-base
  "Returns the point on the surface closest to the specified point.
  The surface can be 0d 1d 2d."
  (fn [point surface] (:type surface)))

(defmethod perp-base
  :line
  [point line]
  (into [] (map #(- %2 (* %3 (- %1 %2))) point (:e line) (:d line)) ))

(defmethod perp-base
  :plane
  [point plane]
  (let [[p1 p2 p3] point
        {[s1 s2 s3] :e, [sn1 sn2 sn3] :n} plane
        q [(- p1 s1) (- p2 s2) (- p3 s3)]
        s-m (mag [sn1 sn2 sn3])
        s-u [(/ sn1 s-m) (/ sn2 s-m) (/ sn3 s-m)]
        sq-in-prod (reduce + (map * s-u q))
        sn (map #(* % sq-in-prod) s-u)
        pu ()
        ]
    )
  )


(defn screw
  "Create a screw object with translational constraint
  defined by line, and with screw motion defined by
  point an pitch."
  [line point pitch]
  (println "screw unimplemented")
  )


(defn sphere
  "Returns a sphere object centered at point with specified radius."
  [point radius]
  (println "sphere unimplemented")
  )

(defn transform
  "If argument is a link, returns the coordinate transform of the link.
  If argument is a marker, returns the coordinate transform of the link
  to which the marker is attached."
  [argument]
  (println "transform unimplemented")
  )

(defn sin
  "If quantity-or-pair is a pair, returns the sine element of the pair.
  If quantity-or-pair is a scalar, returns the sine of the quantity."
  [quantity-or-pair]
  [quantity-or-pair]
  (cond (vector? quantity-or-pair) (nth quantity-or-pair 0)
        :else (Math/sin quantity-or-pair)))

(defn cos
  "If quantity-or-pair is a pair, returns the cosine element of the pair.
  If quantity-or-pair is a scalar (an angle measured in radians), returns the cosine."
  [quantity-or-pair]
  (cond (vector? quantity-or-pair) (nth quantity-or-pair 1)
        :else (Math/cos quantity-or-pair)))

(defn vec-angle
  "The angle between vector-1 and vector-2,
  viewed from the positive direction of axis,
  measured counter-clockwise from vector-1 to vector-2.
  The angle is represented as a (sine cosine) pair.

  |a.b| = |a| |b| cos 0 & |a^b| = |a| |b| sin 0
  or [sine cosine] = [|a^b| a.b]/(|a|*|b|).
  The sign for sine is determined by comparing the
  direction of the axis to a^b."
  [vector-1 vector-2 axis]
  (let [ab-outer (outer-prod vector-1 vector-2)
        ab-inner (inner-prod vector-2 axis)
        ab-mag (* (mag vector-1) (mag vector-2))
        aligned? (pos? (inner-prod ab-outer axis))
        sine (/ (mag ab-outer) ab-mag)
        cosine (/ ab-inner ab-mag)]
    (cond aligned? [sine cosine]
          :else [(- sine) cosine] )))

(defn half-angle
  "The angle specified in [sine cosine] form is in halved. "
  [[sine cosine]]
  [(Math/sin (* 0.5 (Math/asin sine))) (Math/cos (* 0.5 (Math/acos cosine)))])

(defn double-angle
  "The angle specified in [sine cosine] form is doubled. "
  [[sine cosine]]
  (println "sine: " sine "  cosine: " cosine)
  (let [double-sine (Math/sin (* 2.0 (Math/asin sine)))
        double-cosine (Math/cos (* 2.0 (Math/acos cosine)))
        double-cosine (cond (< -0.0001 double-cosine 0.0001) 0.0
                            :else double-cosine)]
    [double-sine double-cosine]))

(defn vec-diff
  "Vector difference of vector-1 and vector-2.
  Produces a vector from vector-subtrahend to vector-minuend."
  [vector-minuend vector-subtrahend]
  (into [] (map - vector-minuend vector-subtrahend)))

(defn vec-scale
  "Returns a vector which is original vector times scalar."
  [vector-0, scale]
  (println "vect-scale unimplemented")
  )

(defn vec-sum
  "Vector sum of vector-1 and vector-2."
  [vector-1 vector-2]
  (into [] (map + vector-1 vector-2)))

(defn x-mul
  "Multiply transform times vector-or transform."
  [transform vector-or-transform]
  (println "x-mul unimplemented")
  )

(defn- quat-exp
  "Produce a quaternion from an axis and and angle.
  The axis need not be unit and the angle is [sine cosine] form.
  "
  [axis angle]
  (let [uaxis (unit-ize axis)
        [sine cosine] angle]
    (into [] (cons cosine (map #(* % sine) uaxis) ))))

(defn- quat-prod
  "Multiply two quaternion."
  [q1 q2]
  (let [[a1 b1 c1 d1] q1
        [a2 b2 c2 d2] q2]
    [(+ (+ (* a1 a2)) (- (* b1 b2)) (- (* c1 c2)) (- (* d1 d2)))
     (+ (+ (* a1 b2)) (+ (* b1 a2)) (+ (* c1 d2)) (- (* d1 c2)))
     (+ (+ (* a1 c2)) (- (* b1 d2)) (+ (* c1 a2)) (+ (* d1 b2)))
     (+ (+ (* a1 d2)) (+ (* b1 c2)) (- (* c1 b2)) (+ (* d1 a2)))]))

(defn- quat-log
  "Convert quaternion into axis angle representation."
  [q]
  (let [[q0 q1 q2 q3] q
        qi [q1 q2 q3]
        i (unit-ize qi) ]
    {:i i
     :a (double-angle [q0 (/ (mag qi) (mag i))]) } ))

(defn rotate
  "rotate a link about the point and axis by an angle.
  Performed by composition of rotations.
  e(-i*theta/2) = e(-i2*theta2/2) * (-i1*thata1/2)
  Where
  e(i*theta/2) = cos(theta/2) + i*sin(theta/2)
  "
  [link point axis angle]
  (let [quat (:versor link)
        q1 (quat-exp (:i quat) (half-angle (:a quat)))
        q2 (quat-exp axis (half-angle angle))
        r (quat-log (quat-prod q1 q2)) ]
    (merge link {:versor (merge (:versor link) {:e point} r)})))


(defn translate
  "translate a link by the specified vector.
  This receives a full placement and returns a full placement.
  The vector points in the direction of the translation."
  [link vect]
  (merge link {:versor (merge-with vec-sum (:versor link) {:e vect})}))
