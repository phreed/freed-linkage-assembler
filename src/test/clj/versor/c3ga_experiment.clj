(ns versor.c3ga-experiment
  (:require [expectations :refer [expect]]))

(defn -*- "the outer product" [a b] (* a b))

(defn -.- "the inner product" [a b] (* a b))

(defn -x- "the geometric product" [a b] (* a b))

(defn -%- "the dual" [a] (* a a))

(defn -<- "the right conjunction" [a b] (* a b))
(defn ->- "the left conjunction" [a b] (* a b))

(defn -+- "the component sum" [a b] (* a b))
(defn --- "the component difference" [a b] (* a b))



(def blade-hash
  {:s [2r00000 0] :n0-e1-e2-e3-ni [2r11111 5] :I [2r11111 5]

   :no [2r00001 1] :e1-e2-e3-ni [2r11110 4]
   :e1 [2r00010 1] :no-e2-e3-ni [2r11101 4]
   :e2 [2r00100 1] :no-e1-e3-ni [2r11011 4]
   :e3 [2r01000 1] :no-e1-e2-ni [2r10111 4]
   :ni [2r10000 1] :no-e1-e2-e3 [2r01111 4]

   :no-e1 [2r00011 2]  :e2-e3-ni [2r11100 3]
   :no-e2 [2r00101 2]  :e1-e3-ni [2r11010 3]
   :no-e3 [2r01001 2]  :e1-e2-ni [2r10110 3]
   :no-ni [2r10001 2]  :e1-e2-e3 [2r01110 3]
   :e1-e2 [2r00110 2]  :no-e3-ni [2r11001 3]
   :e1-e3 [2r01010 2]  :no-e2-ni [2r10101 3]
   :e1-ni [2r10010 2]  :no-e2-e3 [2r01101 3]
   :e2-e3 [2r01100 2]  :no-e1-ni [2r10011 3]
   :e2-ni [2r10100 2]  :no-e1-e3 [2r01011 3]
   :e3-ni [2r11000 2]  :no-e1-e2 [2r00111 3]  })

(def blade-title-hash
  (into {}
        (map (fn [[title [id _]]] [id title]) blade-hash)))

(defn make-bb
  "Make a weighted blade."
  [title weight] {:weight weight
                  :blade (get-in blade-hash [title 0])
                  :grade (get-in blade-hash [title 1])})

(defn bit-count
  "Kernigan's bit counting algorithm."
  [n0]
  (loop [count 0, n n0]
    (if (zero? n)
      count
      (recur (inc count) (bit-and n (dec n))))))


(defn order-bb
  "Determine the order of blades and return 1 or -1.
  This is for anticommutivity.
  It checks the relative positions of the bits
  and determines whether the result needs to be flipped."
  [a b]
  (loop [a (bit-shift-right a 1)
         n 0]
    (if (= 0 a)
      (if (bit-test n 0) -1 1)
      (recur (bit-shift-right a 1)
             (+ n (bit-count (bit-and a b)))))))

(defn gp-bb
  "Geometric product of basis blades."
  [a b]
  (let [a- (:blade a) b- (:blade b)
        r (bit-xor a- b-)
        w (* (:weight a) (:weight b))
        s (order-bb a- b-)]
    {:blade r :weight (* w s) :grade (bit-count r)}) )

(defn ip-bb
  "Inner (left contraction) product of basis blades."
  [a b]
  (let [r (gp-bb a b)]
    (cond (> (:grade a) (:grade b)) {}
          (not= (:grade r) (- (:grade a) (:grade b))) {}
          :else r)))


(defn op-bb
  "Outer product of basis blades."
  [a b]
  (if (not= 0 (bit-and (:grade a) (:grade b)))
    {}
    (gp-bb a b)))

(defn clean-mv
  "Eliminate zero terms."
  [a]
  a)

(defn compress-mv
  "Combine like terms."
  [a]
  a)

(defn gp-mv
  "Geometric product of multivectors.
  The cartesian product of all bases."
  [a b]
  (let [a-bases (:bases a)
        b-bases (:bases b)]
    (loop [a- a-bases, b- b-bases, c {}]
      (cond
       (empty? a-) (clean-mv (compress-mv c))
       (empty? b-) (recur (rest a-) b-bases c)
       :else (let [prod (gp-bb (second (first a-))
                               (second (first b-))) ]
               (recur a- (rest b-) (conj c [(:blade prod) prod] ) ))))))



(defn make-mv
  "Make a multivector from a set of weighted blades."
  [b1 b2]
  {:bases {(get b1 :blade) b1 (get b2 :blade) b2}} )

(expect 2 (bit-count 3))

(let [a (make-bb :e1 5)
      b (make-bb :e2 11)]
  (expect
   '{:blade 6, :weight 55, :grade 2}
   (gp-bb a b)))

(expect
 '{ 6 {:blade 6 :weight -22 :grade 2}
    0 {:blade 0 :weight 33 :grade 0} }
 (gp-mv (make-mv (make-bb :e1 5) (make-bb :e2 11))
        (make-mv (make-bb :e1 2) (make-bb :e2 3))))


(def metric:r4-1 [1.0 1.0 1.0 1.0 -1.0])

(defn mp-bb
  "Geometric product of basis blades in R^4,1."
  [a b]
  (loop [i 0
         r (gp-bb a b)
         t (bit-and a b)]
    (cond (= 0 t) r
          (not= 0 (bit-and t 1))
          (recur (inc i) (* r (get metric:r4-1 i)) (bit-shift-right t 1))

          :else (recur (inc i) r (bit-shift-right t 1)))))

(def e-plane 2r11000)
(def no-ni 2r11000)
(def e+ 2r01000)
(def no 2r01000)
(def e- 2r10000)
(def ni 2r10000)

(defn push-bb
  "Switching from a null basis to a standard diagnonal."
  [a]
  (let [mv {}
        t (:blade a)
        w (:weight a)]
    (cond
     ;; no bits are shared
     (= 0 (bit-and t no-ni)) {:basis a}

     ;; blade contains E plane
     (= no-ni (bit-and t no-ni)) {:basis a}

     ;; contains the origin
     (not= 0 (bit-and t no))
     (let [b (bit-xor t no)
           b1 (bit-xor b e+)
           b2 (bit-xor b e-)]
       (make-mv {:blade b1 :grade (bit-count b1) :weight (/ w 2.0) }
                {:blade b2 :grade (bit-count b2) :weight (/ w 2.0) }))

     ;; contains the infinite
     (not= 0 (bit-and t ni))
     (let [b (bit-xor t no)
           b1 (bit-xor b e+)
           b2 (bit-xor b e-)]
       (make-mv {:blade b1 :grade (bit-count b1) :weight (* w -1.0) }
                {:blade b2 :grade (bit-count b2) :weight w })) )))


(defn pull-bb
  "Switching from a standard diagnonal to a null basis."
  [a]
  (let [t (:blade a)
        w (:weight a)]
    (cond
     ;; no bits are shared
     (= 0 (bit-and t e-plane)) {:basis a}

     ;; blade contains E plane
     (= e-plane (bit-and t e-plane)) {:basis a}

     ;; contains the origin
     (not= 0 (bit-and t e+))
     (let [b (bit-xor t e+)
           b1 (bit-xor b no)
           b2 (bit-xor b ni)]
       (make-mv {:blade b1 :grade (bit-count b1) :weight w}
                {:blade b2 :grade (bit-count b2) :weight (/ w -2.0) }))

     ;; contains the infinite
     (not= 0 (bit-and t e-))
     (let [b (bit-xor t e-)
           b1 (bit-xor b no)
           b2 (bit-xor b ni)]
       (make-mv {:blade b1 :grade (bit-count b1) :weight w }
                {:blade b2 :grade (bit-count b2) :weight (/ w 2.0)})) )))










