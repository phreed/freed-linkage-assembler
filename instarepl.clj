;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
(require 'clojure.data)

(map (fn [[a b]]
       [b a])
     {:b 'foo :c 'bar})


(def print-lines
  "Write out the specified number of lines from the input."
  (let [writer (ref println)
        eol #".*(?:(?:\r\n|\n|\r)|$)"]
    (fn [text from to]
      (->>
       (re-seq eol text)
       (drop from)
       (take (- to from))
       (map-indexed #(println (inc %1) "\t" %2))))))

(println-str
 (print-lines
 "this is a line
 and so is this.
 and this." 1 2))

(require '[isis.geom.machine.misc :as misc])

(defn ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) [:ref @%] %) form))

(defn- ref->checker
  "A checker that allows the names of references to be ignored."
  [rhs]
  (fn [lhs]
    (= rhs
       (clojure.walk/postwalk
        #(if (misc/reference? %) [:ref @%] %) lhs) )))

(def a {:a 'abc :d "efg" :h (ref {:i 32})})

(update-in a [:a] (fn [o n] n) 45)
(def b (assoc-in a [:h]  '(:ref {:i 32})))

((ref->checker b) a)
(= (ref->str a) b)
