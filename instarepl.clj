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

(defn initial-board []
  [\r \n \b \q \k \b \n \r
   \p \p \p \p \p \p \p \p
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \P \P \P \P \P \P \P \P
   \R \N \B \Q \K \B \N \R  ])

(let [file-key (int \a), rank-key (int \0)]
  (letfn [(file-component [file] (->> file-key (- (int file)) ))
          (rank-component [rank] (->> rank-key (- (int rank)) (- 8) (* 8)))
          (index [file rank]
                 (let [f (file-component file)
                       r (rank-component rank)]
                   (+ f r)))]
    (defn lookup [board pos]
      (let [[file rank] pos]
        (nth board (index file rank))))
    (defn whoa [] "whoa")))

(let [board (initial-board)]
  (lookup board "b1"))

(whoa)

(defn lookup2 [board pos]
  (let [file-key (int \a)
        rank-key (int \0)
        [file rank] (map int pos)
        file-component (->> file-key (- file))
        rank-component (->> rank-key (- rank) (- 8) (* 8))
        index (+ file-component rank-component)]
    (nth board index)))

(let [board (initial-board)]
  (lookup2 board "c1"))

(+ 0 60/21)

(as-> [1 2 3] x
      (map inc x)
      (vec x)
      (conj x 5))

(require '[clojure.pprint :as pp])

(false? Boolean/FALSE)
(false? (Boolean/valueOf "false"))


(require '[clojure.zip :as z])

(def z [[1 2 3] [4 [5 6] 7] [8 9]])

(def zp (z/zipper vector? seq (fn [_ c] c) z))

(identity zp)
(-> zp z/down z/down)
(defmacro docstring
  [strings]
  )

(defmacro build-movie-set [& scenes]
  (let [name-vals (partition 2 scenes)]
    `(do
       ~@(for [[name val] name-vals]
           `(defmacro ~(symbol (str "with-" name "s"))
              ([~'& body#]
                 `(do
                    ~@(interpose `(println ~~val)
                                 body#))))))))
(macroexpand-1 '(build-movie-set dog 3 cat 2 cow 7))

(defmulti foo )

(defmacro def-transform-asymetric-methods
  "generate the defmethods"
  [multifn]
  `(do
     ~@(for [tdof [0 1 2 3]
             rdof [0 1 2 3]
             motive [:fixed :mobile]]
         `(defmethod ~multifn
            {:tdof ~tdof :rdof ~rdof :motive ~motive}
            [~'kb ~'m1 ~'m2 ~'motive]
            (~(symbol (str (name motive) "/transform!->t" tdof "-r" rdof))
            ~'kb ~'m1 ~'m2 )))))

(pp/pprint (macroexpand-1 '(def-transform-asymetric-methods foo)))
