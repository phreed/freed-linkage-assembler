(ns isis.geom.machine.tolerance)

(def ^:dynamic *default-tolerance* 0.01)
(def ^:dynamic *tiny-tolerance* 1e-10)

(defn set-default-tolerance
  ""
  [value] (binding [*default-tolerance* value] *default-tolerance*))

(defn near-equal?
  "A function which takes a set of scalar arguments
  and compares them making sure that they are within
  the specified tolerance."
  [tol-value & xs]
  (let [[amin amax] (apply (juxt min max) xs)]
    (< (- amax amin)
       (cond (= :default tol-value) *default-tolerance*
             (= :tiny tol-value) *tiny-tolerance*
                :else tol-value))))

(defn near-zero?
  "A function which takes a scalar argument
  and verifies that its magnitude is sufficiently small."
  [tol-value x]
  (let [tv (cond (number? tol-value) tol-value
                 (= :default tol-value) *default-tolerance*
                 (= :tiny tol-value) *tiny-tolerance*
                 :else tol-value)]
    (cond (number? x) (< (- tv) x tv)
          (vector? x) (near-zero? tol-value (reduce #(+ %1 (* %2 %2)) x)))))


(defn near-same?
  "A function which compares a set of objects.
  They must all be of the same type and there
  values must be individually in range."
  [tol-value & xs]
  (cond (apply not= (map #(count %) xs)) false
        :else (every? identity
                      (apply map #(apply near-equal? tol-value %&) xs))))

(defn snap
  "Rounds the weight of a value to the nearest multiple of scale.
  This is useful when transendental functions are used and
  numerical cruft appears in the result due to small round-off errors."
  [value scale epsilon]
  (let [new-value (* scale (rem value scale))
        change (Math/abs (- value new-value))]
    (if (< (- epsilon) change (+ epsilon))
      new-value
      value)))
