(ns isis.geom.machine.functions
  (:import (c3ga_pkg c3ga) ) )


(defn a-point
  "Returns an 'arbitrary' point that lies on curve"
  [curve]
  )

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
  "Returns the cross product of vect_1 and vect_2."
   [vect_1 vect_2]
  (println "cross-prod unimplemented")
  )

(defn cylinder
  "Return a cylinder object with axial line, whose circular
  profile is defined by axis and radius."
   [line axis radius]
  (println "cylinder unimplemented")
  )

(defn dot-prod
  "Returns the dot product of vect_1 and vect_2."
   [vect_1 vect_2]
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
  [marker]
  (println "gmp unimplemented")
  )

(defn gmx
  "marker x-axis vector (in global coordinate frame)."
  [marker]
  (println "gmx unimplemented")
  )
(defn gmz
  "marker z-axis vector (in global coordinate frame)."
  [marker]
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
  (println "mag unimplemented")
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

(defn perp-dist
  "Distance between surface-1 and serface-2.
  The surfaces may be zero-, one-, or two-dimensional."
  [surface-1 surface-2]
  (println "perp-dist unimplemented")
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
  (println "translate unimplemented")
  )

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
  (println "vec-diff unimplemented")
  )

(defn vec-scale
  "Returns a vector which is original vector times scalar."
  [vector-0, scale]
  (println "vect-scale unimplemented")
  )

(defn vec-sum
  "Vector sum of vector-1 and vector-2."
  [vector-1 vector-2]
  (println "vec-sum unimplemented")
  )

(defn x-mul
  "Multiply transform times vector-or transform."
  [transform vector-or-transform]
  (println "x-mul unimplemented")
  )

