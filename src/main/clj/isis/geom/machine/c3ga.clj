;; see C3.js https://github.com/weshoke/versor.js/blob/master/C4.js
(ns isis.geom.machine.c3ga)


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

