;; see C3.js https://github.com/weshoke/versor.js/blob/master/C4.js
(ns isis.geom.machine.c3ga)

(defn is-c3ga-working?
  "A simple check to make sure c3ga is working."
  []
  (throw (UnsupportedOperationException. "is-c3ga-working"))
  #_(let [ mv (c3ga/mv.)]
    (.set mv 25.0)
    (println "multi-vector =" (.toString mv)))

  #_(let [ e1 (c3ga/vectorE1)
         e2 (c3ga/vectorE2)
         e3 (c3ga/vectorE3)
         e1+e2 (c3ga/add e1 e2)]
    (println "e1 + e2 = " (.toString e1+e2))) )


(defn- dual-dispatch
  [versor])

(defmulti dual
  #'dual-dispatch)

(defmethod dual :vector
  [versor]
  (throw )
  )

(def vectorE1 1)
(def vectorE2 2)
(def vectorE3 3)

(defn add
  [a b]
  (throw (UnsupportedOperationException. "c3ga/add")))

(defn exp [versor])

(defn trigh [fun versor]
	(fun (exp versor) (* 0.5 (exp (- versor)))))

(defn cosh [versor] (trigh + versor))
(defn sinh [versor] (trigh - versor))

