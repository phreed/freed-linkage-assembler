(ns isis.geom.algebra.geobj
  (:require [isis.geom.machine
             [tolerance :as tol]]
            [clojure.pprint :as pp]) )

(def ^:private tau (* 2.0 Math/PI))


(defprotocol SubSpace
  "This protocol specified properties of objects that
  retain there metrics over transformation. "
  (dual [ss] "compute the dual subspace. ")
  (norm2 [ss] "compute the squared magnitude of the subspace. ")
  (norm [ss] "compute the magnitude of the subspace. "))



(defn- subspace-dispatch-n
  ""
  [ss1 ss2 & sss] (mapv class (into [ss1 ss2] sss)))

(defn- subspace-dispatch-2
  ""
  [ss1 ss2 & sss] (mapv class [ss1 ss2] ))


(defmulti outer-prod
  "compute the subspace that is the outer product
  of the two subspaces. This is a grade increasing
  operation.
  The surface can be 0d 1d 2d."
  subspace-dispatch-n)


(defmulti inner-prod
  "compute the subspace that is the inner product
  of the two subspaces. This is a grade decreasing operation.
  The surface can be 0d 1d 2d."
  subspace-dispatch-n)


(defmulti projection
  "compute the subspace that is the projection of mv2 onto mv1.
  Returns the point on the surface closest to the specified point.
  The surface can be 0d 1d 2d."
  subspace-dispatch-2)


(defmulti rejection
  "compute the subspace that is the rejection of mv2 from mv1.
  Distance between surface-1 and serface-2.
  The surfaces may be zero-, one-, or two-dimensional."
  subspace-dispatch-2)


(defmulti separation
  "Distance between object-1 and object-2.
  The objects may be zero-, one-, or two-dimensional.
  The separation is their closest approach.
  It is the magnitude (norm) of the rejection of
  the two objects. "
  subspace-dispatch-2)


(defmulti join
  "extract a subspace representing the join of ss1 and ss2.
  The join is the geometric analog of union.
  This is a grade increasing action and is the
  smallest subspace containing both ss1 and ss2. "
  subspace-dispatch-2)


(defmulti meet
  "compute the subspace that is common to mv1 and mv2.
  This is the geometric analog of intersection.
  This is a grade diminishing action and is equivalent to
  M = (dual (outer-prod (dual B) (dual A)))
  Keep in mind that this dual is relative to the
  The surfaces may be zero-, one-, or two-dimensional."
  subspace-dispatch-n)



(defprotocol Conformal
  "This protocol specified properties of objects that
  retain there metrics over transformation. "
  (locate [mv] "extract the location as a vector.")
  (direct [mv] "extract a direction vector.")
  (weight [mv] "extract a scalar representing the weight.")
  )

(defprotocol Round
  "This protocol specified properties of objects that
  do not have an :ni basis. "
  )

(defrecord Point [e]
  Conformal
  (locate [pt] (:e pt))
  (direct [pt] [0.0 0.0 0.0])
  (weight [pt] 0.0)
  SubSpace
  (norm2 [pt] (reduce #(+ %1 (* %2 %2)) 0.0 (:e pt)))
  (norm [pt] (Math/sqrt (norm2 pt))))

(defrecord FlatPoint [e])
(defrecord PointPair [e1 e2])

(defrecord Line [e d]
  Conformal
  (locate [ln] (:e ln))
  (direct [ln] (:d ln))
  (weight [ln] 0.0)
  SubSpace
  (norm2 [ln] (reduce #(+ %1 (* %2 %2)) 0.0 (:e ln)))
  (norm [ln] (Math/sqrt (norm2 ln))))

(defrecord Direction [e])
(defrecord Plane [e n])
(defrecord Circle [e n r])
(defrecord Sphere [e r])

(defrecord Univector [])
(defrecord Bivector [])
(defrecord Trivector [])


(extend-protocol SubSpace
  clojure.lang.PersistentVector
  (norm2 [quantity]
         (cond (empty? quantity) 0.0
               :else (reduce #(+ %1 (* %2 %2)) 0.0 quantity)))
  (norm [quantity] (Math/sqrt (norm2 quantity)))

  Number
  (norm2 [quantity] (Math/abs quantity))
  (norm [quantity] (Math/sqrt (norm2 quantity))))


(defn normalize
  "make the object have size 1."
  [vect]
  (let [weight (norm vect)]
    (if (tol/near-zero? :default weight)
      nil
      (mapv #(/ % weight) vect))))

(defn ptwist
  "twist a vector making an arbitrary perpendicular vector.
  There are any number of solutions to this problem.
  Using the fact that a zero dot-product is a sign of
  perpendicularity, three solutions are [-y x 0], [0 z -y]
  and [-z 0 x].  We are looking for a solution that
  is insensitive to zeros in the input vector, this
  can be achieved via a linear combination of these
  three primitive solutions. "
  [vect]
  (let [[x y z] vect] [(- (+ y z)) (+ x z) (- x y)]))


(defn quat-normalize
  "Take a quaternion and normalize it to a unit quaternion."
  [[q0 q1 q2 q3]]
  (let [magnitude (Math/sqrt (+ (* q0 q0) (* q1 q1) (* q2 q2) (* q3 q3)))]
    [(/ q0 magnitude) (/ q1 magnitude) (/ q2 magnitude) (/ q3 magnitude)]) )


(defn double-angle
  "The angle specified in [sine cosine] form is doubled.
  Make sure to handle the different quadrants.
  Probably should handle tolerance around zero as well."
  [angle]
  (let [[sine cosine] angle
        rad-cos (* 2.0 (Math/acos cosine))
        rads ((if (pos? sine) + -) rad-cos)]
    [(Math/sin rads) (Math/cos rads)]))


(defn axis-angle->quaternion
  "Produce a quaternion from an axis and and angle.
  The axis need not be unit and the angle is [sine cosine] form.
  The angle specified in [sine cosine] form is in halved. "
  [axis angle]
  (let [uaxis (normalize axis)
        [a1 a2 a3] uaxis]
    (if (zero? (reduce + angle))
      [1.0 (* 0.5 a1) (* 0.5 a2) (* 0.5 a3)]
      (let [[sine cosine] angle
            rad-cos (/ (Math/acos cosine) 2.0)
            rads ((if (pos? sine) + -) rad-cos)
            half-sine  (Math/sin rads)
            half-cosine (Math/cos rads)]
        (into [] (cons half-cosine (map #(* % half-sine) uaxis) ))))))

(defn axis-pi-angle->quaternion
  "Produce a quaternion from an axis and and angle.
  The axis need not be unit and the angle is [sine cosine] form.
  The angle specified in [sine cosine] form is in halved. "
  [axis angle]
  (let [uaxis (normalize axis)
        [a1 a2 a3] uaxis
        half-angle (* 0.5 angle Math/PI)
        half-sine (Math/sin half-angle)
        half-cosine (Math/cos half-angle)]
    (into [] (cons half-cosine (map #(* % half-sine) uaxis) ))))

(defn quaternion->axis-angle
  "Produce an axis and an angle from a quaternion.
  The angle specified in [sine cosine] form is in halved. "
  [quaternion]
  (let [[q0 q1 q2 q3] quaternion
        sin-squared (+ (* q1 q1) (* q2 q2) (* q3 q3))]
    (if (zero? sin-squared)
      [[(* 2.0 q1) (* 2.0 q2) (* 2.0 q3)] [0.0 1.0]]
      (let [sine-theta (Math/sqrt sin-squared)
            k (* 2.0 (/ (Math/atan2 sine-theta q0) sine-theta))]
        [[(* k q1) (* k q2) (* k q3)] []]))))

(defn quat-conj
  "Produce the quaternion conjugate, it does the opposite."
  [[q0 q1 q2 q3]]  [q0 (- q1) (- q2) (- q3)])

(defn quat-sandwich
  "Perform a transformation using the quaternion.
  q v q-1 = v'
  "
  [quaternion point]
  (let [[p1 p2 p3] point
        [q0 q1 q2 q3] quaternion
        r1 (+ (* q0 p1 q0 +1) (* q0 p2 q3 -2) (* q0 p3 q2 +2)
              (* q1 p1 q1 +1) (* q1 p2 q2 +2) (* q1 p3 q3 +2)
              (* q2 p1 q2 -1)
              (* q3 p1 q3 -1))
        r2 (+ (* q0 p2 q0 +1) (* q0 p3 q1 -2) (* q0 p1 q3 +2)
              (* q2 p2 q2 +1) (* q2 p3 q3 +2) (* q2 p1 q1 +2)
              (* q3 p2 q3 -1)
              (* q1 p2 q1 -1) )
        r3 (+ (* q0 p3 q0 +1) (* q0 p1 q2 -2) (* q0 p2 q1 +2)
              (* q3 p3 q3 +1) (* q3 p1 q1 +2) (* q3 p2 q2 +2)
              (* q1 p3 q1 -1)
              (* q2 p3 q2 -1) ) ]
    [(if (tol/near-zero? :tiny r1) 0.0 r1)
     (if (tol/near-zero? :tiny r2) 0.0 r2)
     (if (tol/near-zero? :tiny r3) 0.0 r3)]))


(defn- quat-prod
  "Multiply two quaternion."
  [q1 q2]
  (let [[a1 b1 c1 d1] q1
        [a2 b2 c2 d2] q2]
    [(+ (+ (* a1 a2)) (- (* b1 b2)) (- (* c1 c2)) (- (* d1 d2)))
     (+ (+ (* a1 b2)) (+ (* b1 a2)) (+ (* c1 d2)) (- (* d1 c2)))
     (+ (+ (* a1 c2)) (- (* b1 d2)) (+ (* c1 a2)) (+ (* d1 b2)))
     (+ (+ (* a1 d2)) (+ (* b1 c2)) (- (* c1 b2)) (+ (* d1 a2)))]))

(defn- axis-angle->quat
  "Create a quaternion from an axis-angle.
  axis : a vector representing the rotation axis.
  angle : the rotation specified in radians,
  as viewed looking out along the axis. "
  [axis angle]
  (let [[u1 u2 u3] (normalize axis)
        hangle (* 0.5 angle)
        cosine (Math/cos hangle)
        sine (Math/sin hangle)]
    [cosine (* sine u1) (* sine u2) (* sine u3)]))


(defn- quat-log
  "Convert quaternion into axis angle representation."
  [q]
  (let [[q0 q1 q2 q3] q
        qi [q1 q2 q3]
        i (normalize qi) ]
    {:axis i
     :hangle [q0 (/ (norm qi) (norm i))] } ))


(defn point
  "A point is simply a typed 3-tuple."
  [triple] (->Point triple))

(defn point?
  "Returns 'true' if object is a 'point'."
  [object] (instance? Point object))


(defn line
  "Returns a line object with direction
  axis passing through a location in space."
  [anchor axis]
  (->Line anchor (normalize axis)))

(defn line?
  "Returns 'true' if object is a line."
  [object] (instance? Line object))


(defn scalar-prod
  "The product of a scalar and a vector."
  [ss1 ss2] (map #(* % ss1) ss2))

(defmethod inner-prod [Number
                       clojure.lang.PersistentVector]
  [ss1 ss2 & sss] (scalar-prod ss1 ss2))


(defmethod inner-prod [clojure.lang.PersistentVector
                       clojure.lang.PersistentVector]
  [v1 v2 & xs]
  (reduce + (map * v1 v2)))


(defmethod outer-prod [Number
                       clojure.lang.PersistentVector]
  [ss1 ss2 & sss] (scalar-prod ss1 ss2))


(defmethod outer-prod [clojure.lang.PersistentVector
                       clojure.lang.PersistentVector]
  [v1 v2 & xs]
  (let [[v1-1 v1-2 v1-3] v1
        [v2-1 v2-2 v2-3] v2 ]
    [(- (* v1-2 v2-3) (* v1-3 v2-2))
     (- (* v1-3 v2-1) (* v1-1 v2-3))
     (- (* v1-1 v2-2) (* v1-2 v2-1))]))




(defn plane
  "Create a plane object with normal vector passing through point."
  [anchor normal]
  (->Plane anchor (normalize normal)))


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
  (println "copy unimpl")
  )


(defn cylinder
  "Return a cylinder object with axial line, whose circular
  profile is defined by axis and radius."
  [line axis radius]
  (println "cylinder unimpl")
  )


(defn ellipse?
  "Returns 'true' if object is an ellipse."
  [object]
  (println "ellipse? unimpl")
  )


(defn ellipse+r
  "Create an ellipse+r object that records an ellipse, a rotational axis and
  a length, to make an objec twith a single DOF (combined translational
  and rotational )."
  [ellipse axis length]
  (println "ellipse+r unimpl")
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

(defn vec-diff
  "Vector difference of vector-1 and vector-2.
  Produces a vector from vector-subtrahend to vector-minuend."
  [vector-minuend vector-subtrahend]
  (mapv - vector-minuend vector-subtrahend))

(defn vec-scale
  "Returns a vector which is original vector times scalar."
  [vector-0, scale]
  (println "vect-scale unimpl")
  )

(defn vec-sum
  "Vector sum of vector-1 and vector-2."
  [vector-1 vector-2]
  (mapv (fn [a b]
          (let [ab (+ a b)]
            (if (tol/near-zero? :tiny ab) 0.0 ab))) vector-1 vector-2))

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
  {:pre [ (instance? clojure.lang.PersistentVector vector-1)
          (instance? clojure.lang.PersistentVector vector-2)
          (instance? clojure.lang.PersistentVector axis) ]}
  (let [scale (inner-prod vector-2 axis)
        center (into [] (map #(* % scale) axis))
        diff-1 (normalize (vec-diff vector-1 center))
        diff-2 (normalize (vec-diff vector-2 center))]

    (if (some nil? [diff-1 diff-2])
      [0.0 1.0]
      (let [cosine (inner-prod diff-1 diff-2)

            sine-vec (outer-prod diff-1 diff-2)
            sine (if (pos? (inner-prod sine-vec axis))
                   (norm sine-vec)
                   (- (norm sine-vec)))]
        [sine cosine]))))


(defmulti versor-apply
  "Place the point into the global coordinate frame using the versor."
  (fn [versor gobj] (class gobj) )
  :default
  (fn [versor gobj]
    (println "no dispatch method for versor-apply" (class gobj))))

(defmethod versor-apply clojure.lang.PersistentVector
  [versor gobj]
  (let [versor-rotate (get versor :rotate [1.0 0.0 0.0 0.0])
        versor-translation (get versor :xlate [0.0 0.0 0.0])
        rot-loc (quat-sandwich versor-rotate gobj)]
    (mapv + versor-translation rot-loc)))

(defmethod versor-apply Point
  [versor gobj]
  (let [versor-rotate (get versor :rotate [1.0 0.0 0.0 0.0])
        versor-translation (get versor :xlate [0.0 0.0 0.0])
        rot-loc (quat-sandwich versor-rotate (:e gobj))]
    (mapv + versor-translation rot-loc)))

(defn gmp
  "marker position (in global coordinate frame).
  The marker is of the form
  [['<link-name>' '<marker-name>']
  [marker kb] { :e [<position>] :pi <rotation-angle>
  :q [<axis-of-rotatation>]}] "
  [marker kb]
  (let [[[link-name _] marker-place] marker
        marker-loc (point (get marker-place :e [0.0 0.0 0.0]))
        link @(get-in kb [:link link-name])]
    (versor-apply (:versor link) marker-loc)))

(defn gmx
  "marker x-axis vector (in global coordinate frame).
  The marker is of the form
  [['<link-name>' '<marker-name>']
  [marker kb] { :e [<position>] :pi <rotation-angle>
  :q [<axis-of-rotatation>]}]
  The x-axis is perpendicular to the z-axis."
  [marker kb]
  #_(pp/pprint ["gmx" marker])
  (let [[[link-name _] marker-place] marker
        marker-axis (get marker-place :q [0.0 0.0 0.0])
        marker-twist (* Math/PI (get marker-place :pi 0.0))
        link @(get-in kb [:link link-name])
        marker-quat (axis-angle->quat marker-twist marker-axis)]
    #_(pp/pprint ["versor-apply" marker-quat marker-axis marker-twist])
    (versor-apply (:versor link) marker-quat)))


(defn gmz
  "marker z-axis vector (in global coordinate frame).
  The marker is of the form
  [['<link-name>' '<marker-name>']
  [marker kb] { :e [<position>] :pi <rotation-angle>
  :q [<axis-of-rotatation>]}]
  In most cases the :q value is the axis of rotation.
  In the special case where :q is zero the z-axis is [0 0 1]."
  [marker kb]
  ;; (pp/pprint ["gmz" marker])
  (let [[[link-name _] marker-place] marker
        marker-axis (get marker-place :q [0.0 0.0 1.0])
        link @(get-in kb [:link link-name])
        marker-axis (if (tol/near-zero? :tiny marker-axis)
                      [0.0 0.0 1.0]
                      (normalize marker-axis))]
    (versor-apply (:versor link) marker-axis)))


(defn helix
  "Returns a helix object with axial line defined by point
  and axis, and with specified radius and pitch."
  [point axis radius pitch]
  (println "helix unimpl")
  )



(defn inverse
  "Returns the inverse of a transform."
  [transform]
  (println "inverse unimpl")
  )

(defn lcf
  "Returns the transform matrix for the local coordinate frame of the marker"
  [marker]
  (println "lcf unimpl")
  )

(defn lmp
  "marker position in link coordinate frame."
  [marker]
  (println "lmp unimpl")
  )

(defn lmx
  "marker x-axis position in link coordinate frame."
  [marker]
  (println "lmx unimpl")
  )

(defn lmz
  "marker z-axis position in link coordinate frame."
  [marker]
  (println "lmz unimpl")
  )


(defn modulo
  "Returs quantity modulo modulus"
  [quantity modulus]
  (println "modulo unimpl")
  )

(defn normal
  "Returns the normal of a plane."
  [plane]
  (println "normal unimpl")
  )

(defn null?
  "Returns 'true' if quantity has the nil value."
  [quantity]
  (println "null? unimpl")
  )



(defmethod inner-prod [clojure.lang.PersistentVector
                       Line]
  [l1 l2 & xs]
  (let [ {e2 :e, d2 :d} l2]
    (reduce + (map * l1 d2))))



(defmethod outer-prod [Line Line]
  [l1 l2 & xs]
  (let [{[e11 e12 e13] :e, [d11 d12 d13] :d} l1
        {[e21 e22 e23] :e, [d21 d22 d23] :d} l2
        ln-axis (into []  (outer-prod )) ]
    ))


(comment "Calculates the intersection of three planes.")
(defmethod meet [Plane Plane Plane]
  [s1 s2 & xs]
  ;; (pp/pprint ["s1" s1 "s2" s2 "xs" xs])
  (let [ a s1, b s2, c (first xs)
         det-helper (fn [v1 v2 v3]
                      (let [ [v11 v12 v13] v1
                             [v21 v22 v23] v2
                             [v31 v32 v33] v3]
                        (- (+ (* v11 v22 v33)
                              (* v12 v23 v31)
                              (* v13 v21 v32) )
                           (+ (* v11 v23 v32)
                              (* v12 v21 v33)
                              (* v13 v22 v31) ))))
         {a- :e, an :n} a
         {b- :e, bn :n} b
         {c- :e, cn :n} c
         anbncn (det-helper an bn cn)]

    (if (zero? anbncn) nil
      (let [ base-helper
             (fn [pnt v1 v2 v3]
               (let [ [p1  p2  p3] pnt
                      [v11 v12 v13] v1
                      [v21 v22 v23] v2
                      [v31 v32 v33] v3

                      dot (+ (* p1 v11) (* p2 v12) (* p3 v13) )

                      cross [(- (* v22 v33) (* v32 v23))
                             (- (* v23 v31) (* v21 v33))
                             (- (* v21 v32) (* v22 v31))]]
                 (mapv #(* dot %) cross)))
             a-abc (base-helper a- an bn cn)
             b-bca (base-helper b- bn cn an)
             c-cab (base-helper c- cn an bn)]
        (mapv #(/ (+ %1 %2 %3) anbncn) a-abc b-bca c-cab)))) )


(comment "Calculates the intersection of two planes.")
(defmethod meet [Plane Plane]
  [{e1 :e, n1 :n, :as s1}
   {e2 :e, n2 :n, :as s2} & ss1]
  (let [ln-axis (outer-prod n1 n2)]
    (if (tol/near-zero? :tiny ln-axis)
      (do
        (pp/pprint ["degenerate/contradictory" ln-axis "s1" s1 "s2" s2])
        ;; FIXME What to do about parallel planes?
        ;; The parallel planes can be detected when ln-axis has a 0 norm.
        :degenerate )
      (as-> ln-axis $       ; the axis of the line-of-intersection
            (plane e1 $)    ; an arbitrary orthogonal plane
            (meet $ s1 s2)  ; point on the line-of-intersection
            (line $ ln-axis) ))))  ; the line-of-intersection


(comment "Calculates the intersection of a line and a plane.
  Find the distance from the line-pt to the plane
  along the the line-axis. ")
(defmethod meet [Line Plane]
  [{e1 :e, d1 :d, :as l1}
   {e2 :e, n2 :n, :as s2} & sss]
  (as-> (vec-diff e1 e2) $  ; vector from point-on-line to point-on-plane
        (inner-prod $ n2)   ; dist from point-on-line to plane
        (outer-prod $ n2)   ; vector from plane to point-on-line
        (vec-diff e1 $)))   ; intersection point


(defmethod rejection [Point Point]
  [pnt1 pnt1] (println "rejection of a point onto a point is undefined. "))

(defmethod rejection [clojure.lang.PersistentVector Line]
  [pnt {el :e, dl :d, :as ln}]
  (let [hypo (vec-diff pnt el)]
    (as-> hypo $               ; vector from point to point-on-line
          (inner-prod $ dl)    ; coord of dl as basis
          (scalar-prod $ dl)   ; projection of hypo onto line
          (vec-diff hypo $)))) ; shortest vector from point to line

(defmethod rejection [clojure.lang.PersistentVector Plane]
  [pnt {ep :e, np :n, :as pln}]
  (as-> (vec-diff pnt ep) $  ; vector from point-on-plane to point
        (inner-prod $ np)    ; distance from plane to point
        (scalar-prod $ np))) ; shortest vector from plane to point


(defmethod separation [Point Point]
  [pnt1 pnt1] (norm (vec-diff pnt1 pnt1)))

(defmethod separation [clojure.lang.PersistentVector Line]
  [pnt, ln]
  (norm (rejection pnt ln)))  ; the magnitude of the rejection


(defmethod separation [clojure.lang.PersistentVector Plane]
  [pnt {ep :e, np :n, :as pln}]
  (as-> (vec-diff pnt ep) $  ; vector from point-on-plane to point
        (inner-prod $ np)))  ; distance from plane to point


(defn on-surface?
  "Returns 'true' if ?point lies on ?surface."
  [?point ?surface]
  (zero? (rejection ?point ?surface)) )

(defn parallel?
  "Returns 'true' if ?axis-1 and ?axis-2 are parallel.
  If ?direction-matters is 'true', then the axis
  must also be pointed in the same direction.
  If ?direction-maters is 'false', then the axis
  may be either parallel or anti-parallel."
  [?axis-1 ?axis-2 ?direction-matters]
  (let [size (norm (outer-prod (normalize ?axis-1) (normalize ?axis-2)))]
    (cond (not (tol/near-zero? :default size)) false
          (not ?direction-matters) true
          (pos? size) true
          :else false )))


(defn pc-check
  "For use with curve in {screw ellipse+r}.
  If translation and rotation are both non-null,
  return 'true' if the stranslation and rotation are
  consistent with curve parameters, and 'false' otherwise.
  If one of translation or rotation is nil, then
  return a value that is consistent with curve parameters.
  If multiple choices are possible, disambiguate with branch."
  [curve translation rotation branch]
  (println "pc-check unimpl")
  )

(defn pc-error
  "Returns an error term for curve with inconsistent
  translation and rotation."
  [curve translation rotation]
  (println "pc-error unimpl")
  )

(defn pc-locus
  "Returns the locus of locus-point, which is on the
  same rigid body as constrained-point consstrained to
  lie on curve."
  [curve constrained-point locus-point]
  (println "pc-locus unimpl")
  )



(defn screw
  "Create a screw object with translational constraint
  defined by line, and with screw motion defined by
  point an pitch."
  [line point pitch]
  (println "screw unimpl")
  )


(defn sphere
  "Returns a sphere object centered at point with specified radius."
  [point radius]
  (println "sphere unimpl")
  )

(defn transform
  "If argument is a link, returns the coordinate transform of the link.
  If argument is a marker, returns the coordinate transform of the link
  to which the marker is attached."
  [argument]
  (println "transform unimpl")
  )

(defmethod projection [Point Point]
  [pnt1 pnt1] (println "projection of a point onto a point is undefined. "))

;; project the point onto the line
(defmethod projection [Point Line]
  [point {anchor :e, axis :d, :as ln}]
  (as-> (vec-diff point anchor) $
        (inner-prod $ axis)
        (scalar-prod $ axis)
        (vec-sum anchor $)))


(comment "project point onto line")
(defmethod projection [clojure.lang.PersistentVector
                       Line]
  [point {anchor :e, axis :d, :as ln}]
  (as-> (vec-diff point anchor) $ ; a vector from line to point
        (inner-prod $ axis)   ; distance along line to point-projection
        (scalar-prod $ axis)  ; vector along line to point-projection
        (vec-sum anchor $)))  ; projected-point on line


(comment "project point onto the plane")
(defmethod projection [Point Plane]
  [point, {anchor :e, normal :n, :as plane}]
  (as-> (vec-diff point anchor) $ ; a vector from plane to point
        (inner-prod $ normal)  ; distance from plane to point
        (scalar-prod $ normal) ; vector from plane to point
        (vec-diff point $)))   ; point on the plane


(defn x-mul
  "Multiply transform times vector-or transform."
  [transform vector-or-transform]
  (println "x-mul unimpl")
  )

(defn rotate
  "rotate a link about the point and axis by an angle.
  Performed by composition of rotations.
  e(-i*theta/2) = e(-i2*theta2/2) * (-i1*theta1/2)
  Where
  e(i*theta/2) = cos(theta/2) + i*sin(theta/2)
  Note it is assumed that the translation
  is expressed in terms of the point.
  "
  [link point axis angle]
  {:pre [(instance? clojure.lang.PersistentVector point)
         (instance? clojure.lang.PersistentVector axis)
         (instance? clojure.lang.PersistentVector angle)] }
  (cond
   (tol/near-zero? :tiny (first angle)) link
   :else
   (let [q1 (get-in link [:versor :rotate])
         q2 (axis-angle->quaternion axis angle)
         q12 (quat-prod q2 q1)

         x1 (get-in link [:versor :xlate])
         x2 (vec-diff point x1)
         x3 (quat-sandwich (quat-conj q1) x2)
         x4 (quat-sandwich q12 x3)
         x12 (vec-diff point x4)]
     (merge link {:versor {:xlate x12 :rotate q12} }))))


(defn translate
  "translate a link by the specified vector.
  This receives a full placement and returns a full placement.
  The vector points in the direction of the translation.
  The translation is implemented by updating the
  :xlate field of the :versor.
  The most likely choices for action-fn are
  vec-sum and vec-diff. "
  [link action-fn vect]
  (let [action-fn (or action-fn vec-sum)]
    (merge link {:versor  ; replace with updated information
                 (merge-with action-fn
                             (:versor link)
                             {:xlate vect})})))
