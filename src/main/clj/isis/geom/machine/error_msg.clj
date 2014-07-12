(ns isis.geom.machine.error-msg)


(defn- error
  "Signals a run-time error, caused by degeneracies or over-constratin.
  measure is an expression which is zero if there is no error, an non-zero oterwise.
  The magnitude of measure increases wiht the severity of the error.
  If ERROR-MODE is 'fatal' and measure is greater than TOLERANCE, then
  string is returned with the error signal.
  If ERROR-MODE is 'accumlate' and measure is greater than TOLERANCE,
  then ERROR-ACC is set to ERROR-ACC plus measure."
  [measure payload string]
  (println "error : " payload string) )



(def emsg-1 "Cannot keep point-form on constraint-surface.")

(def emsg-2 "Required translation is inconsistent with coupled rotation.")

(defn emsg-3
  [axis-1 axis-2]
  (error 1 [axis-1 axis-2]
  "Required rotation is inconsistent with available DOF's."))

(defn e-dim-oc
  [from to center axis]
  (error 1 [" from-point: " from " to-point: " to
          " about-axis: " axis " about-center: " center]
         "Dimensional over-constraint."))

(def emsg-5 "Surfaces do not intersect in a point.")

(def emsg-6 "Surfaces do not intersect in a 1D curve.")

(defn e-mark-place-ic
  [dist from to]
  (error 1 [" distance: " dist " from-point: " from " to-point: " to]
         "Cannot put marker into position with available DOF's."))

(def emsg-8 "Cannot put marker into alignment with available DOF's.")

(def emsg-9 "Over-constrained marker in inconsistent location.")

(def emsg-10 "Unexpected degeneracy in geometry.")
