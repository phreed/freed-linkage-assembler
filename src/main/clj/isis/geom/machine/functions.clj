(ns isis.geom.machine.functions
  (:require [isis.geom.machine.c3ga :as c3ga]
            [clojure.math.numeric-tower :as math] ) )


(defn is-c3ga-working?
  "A simple check to make sure c3ga is working."
  []
  (throw (UnsupportedOperationException. "is-c3ga-working"))
  #_(let [ mv (c3ga/mv.)]
    (.set mv 25.0)
    (println "multi-vector =" (.toString mv)))

  (let [ e1 (c3ga/vectorE1)
         e2 (c3ga/vectorE2)
         e3 (c3ga/vectorE3)
         e1+e2 (c3ga/add e1 e2)]
    (println "e1 + e2 = " (.toString e1+e2))) )

(defn- merge-position
  "A function which merges the matching fields. (e1 e2 e3).
  The remaining fields are preserved from v1,
  this function is asymetric."
  [f v1 v2]
  (merge-with f v1 (select-keys v2 [:e1 :e2 :e3])))


(defn a-point
  "Returns an 'arbitrary' point that lies on curve"
  [curve] )

(defn axis
  "Returns the vector axis of an object of type circle, line, cylinder, or helix"
  [object]
  )

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
  )

(defn circle?
  "Returns 'true' if object is a circle."
   [object]
  (println "circle? unimplemented")
  )

(defn copy
  "Returns a copy of a geometric object, fixed in the
  global cooridinate frame."
   [object]
  (println "copy unimplemented")
  )

(defn cos
  "If quantity-or-pair is a pair, returns the cosine element of the pair.
  If quantity-or-pair is a scalar, returns the cosine."
   [quantity-or-pair]
  (println "cos unimplemented")
  )

(defn cross-prod
  "Returns the cross product of vect-1 and vect-2."
   [vect-1 vect-2]
  (let [{v1-1 :e1, v1-2 :e2, v1-3 :e3} vect-1
        {v2-1 :e1, v2-2 :e2, v2-3 :e3} vect-2 ]
    {:e1 (- (* v1-2 v2-3) (* v1-3 v2-2))
     :e2 (- (* v1-3 v2-1) (* v1-1 v2-3))
     :e3 (- (* v1-1 v2-2) (* v1-2 v2-1))}))

(defn cylinder
  "Return a cylinder object with axial line, whose circular
  profile is defined by axis and radius."
   [line axis radius]
  (println "cylinder unimplemented")
  )

(defn dot-prod
  "Returns the dot product of vect-1 and vect-2."
   [vect-1 vect-2]
  (println "dot-prod unimplemented")
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
  (println "error unimplemented")
  )


(defn equal?
  "Returns 'true' if quantity-1 equals quantity-2 withing TOLERANCE.
  Quantities may be scalars, points, or vectors."
  [quantity-1 quantity-2]
  (println "equal? unimplemented")
  )

(defn gmp
  "marker position (in global coordinate frame)."
  [marker ikb]
  (let [[[link-name _] mp] marker
        lkb (:l ikb)
        link @(link-name lkb)
        lp (:p link)]
    (merge-position + lp mp)))

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

(defn intersect
 "Calculates the intersection of surface-1 and surface-2.
 Either surface may be zero-, one-, or two-dimensional.
 The branch argument determines which solution branch is used.
 Returns the null value (nil) if the surfaces do not intersect."
[surface-1 surface-2]
  (println "intersect unimplemented")
 )

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

(defn line
  "Returns a line object with direction vector passing through point."
  [point vector]
  (println "line unimplemented")
  )

(defn line?
  "Returns 'true' if object is a line."
  [object]
  (println "line? unimplemented")
  )

(defn lmp
  "marker position in geom coordinate frame."
  [marker]
  (println "lmp unimplemented")
  )

(defn lmx
  "marker x-axis position in geom coordinate frame."
  [marker]
  (println "lmx unimplemented")
  )

(defn lmz
  "marker z-axis position in geom coordinate frame."
  [marker]
  (println "lmz unimplemented")
  )

(defn mag
  "If quantity is a vector, returns the magnitude of quantity.
  If quantity is a scalar, returns the absolute value of quantity."
  [quantity]
  (cond (number? quantity) (math/abs quantity)
        (associative? quantity)
        (let [{e1 :e1, e2 :e2, e3 :e3} quantity]
          (math/sqrt (+ (* e1 e1) (* e2 e2) (* e3 e3))))))

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
  (let [{n1-1 :e23, n1-2 :e31, n1-3 :e12} ?axis-1
        {n2-1 :e23, n2-2 :e31, n2-3 :e12} ?axis-2
        pairs [[n1-1 n2-1] [n1-2 n2-2] [n1-3 n2-3]]]
    (if (every? (fn [[a b]]  (not= (= 0.0 a) (= 0.0 b))) pairs)
      ;; found a coordinate with value zero, but only for one side
      false
      ;; filter out the degenerate coordinates, those with both zero
      (let [good (filter (fn [[a b]] (not= 0.0 a b)) pairs)]
        (if (empty? good)
          ;; if everything is filtered out then not parallel
          false
          ;; parallel if the ratios of the remaining pairs match
          (if ?direction-matters
            (= (map (fn [[a b]] (/ a b)) good))
            (= (map (fn [[a b]] (math/abs (/ a b))) good))))))))



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

(defn perp-base
  "Returns the point on the surface closest to the specified point."
  [point surface]
  (println "perp-base unimplemented")
  )


(defn plane
  "Create a plane object with normal vector passing through point."
  [point vect]
  (println "plane unimplemented")
  )

(defn point?
  "Returns 'true' if object is a 'point'."
  [object]
  (println "point? unimplemented")
  )

(defn rotate
  "Rotate a geom about the point and axis by an angle."
  [geom point axis angle]
  (println "rotate unimplemented")
  )

(defn screw
  "Create a screw object with translational constraint
  defined by line, and with screw motion defined by
  point an pitch."
  [line point pitch]
  (println "screw unimplemented")
  )

(defn sin
  "If quantity-or-pair is a pair, returns the sine element of the pair.
  If quantity-or-pair is a scalar, returns the sine of the quantity."
  [quantity-or-pair]
  (println "sin unimplemented")
  )

(defn sphere
  "Returns a sphere object centered at point with specified radius."
  [point radius]
  (println "sphere unimplemented")
  )

(defn transform
  "If argument is a geom, returns the coordinate transform of the geom.
  If argument is a marker, returns the coordinate transform of the geom
  to which the marker is attached."
  [argument]
  (println "transform unimplemented")
  )

(defn translate
  "Translate a geom by the specified vector."
  [geom vect]
  (merge-position + (:p geom) vect))

(defn vec-angle
  "The angle between vector-1 and vector-2,
  viewed from the positive direction of axis,
  measured counter-clockwise from vector-1 to vector-2.
  The angle is represented as a (sine cosine) pair."
  [vector-1 vector-2 axis]
  (println "vec-angle unimplemented")
  )

(defn vec-diff
  "Vector difference of vector-1 and vector-2."
  [vector-1 vector-2]
  (merge-position - vector-1 vector-2))

(defn vec-scale
  "Returns a vector which is original vector times scalar."
  [vector-0, scale]
  (println "vect-scale unimplemented")
  )

(defn vec-sum
  "Vector sum of vector-1 and vector-2."
  [vector-1 vector-2]
  (merge-position + vector-1 vector-2))

(defn x-mul
  "Multiply transform times vector-or transform."
  [transform vector-or-transform]
  (println "x-mul unimplemented")
  )

