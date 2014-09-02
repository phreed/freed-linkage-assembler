(ns versor.c3ga-experiment
  (:require [midje.sweet :refer [facts fact]] ))

(defn -*- "the outer product" [a b] (* a b))

(defn -.- "the inner product" [a b] (* a b))

(defn -x- "the geometric product" [a b] (* a b))

(defn -%- "the dual" [a] (* a a))

(defn -<- "the right conjunction" [a b] (* a b))
(defn ->- "the left conjunction" [a b] (* a b))

(defn -+- "the component sum" [a b] (* a b))
(defn --- "the component difference" [a b] (* a b))

;;;; ======== Some general purpose functions

(defn minus-pow
  "determine a sign based on the exponent."
  [exp] (if (even? exp) +1 -1))


(defn bit-count-kernigan
  "Kernigan's bit counting algorithm."
  [n0]
  (loop [count 0, n n0]
    (if (zero? n)
      count
      (recur (inc count) (bit-and n (dec n))))))

(defn bit-count-faster
  "Faster bit count, from numerous sources.
  I have some doubts that this is faster
  when working with a maximum of five bits."
  [a]
  (let [b (- a (bit-and 16r55555555 (bit-shift-right a 1)))
        c (+ (bit-and b 16r33333333) (bit-and (bit-shift-right b 2) 16r33333333))
        d (bit-and (+ c (bit-shift-right c 4)))
        e (+ d (bit-shift-right d 8))
        f (+ e (bit-shift-right e 16))]
    (bit-and f 16r0000003F)))

(def bit-count bit-count-kernigan)


;;;;========= Basis-blades are represented by a bitmap (blade) and a weight

(def blade-label-hash
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

(def blade-bitmap-hash
  (into {}
        (map (fn [[label [id _]]] [id label]) blade-label-hash)))

(defn ->bb
  "Make a weighted blade.
  If a single arity call is made we have the weight of a scalar.
  The blade can be specified by keyword or bitmap."
  ;; the empty basis blade
  ([] {:weight 0.0 :blade 0 :grade 0})
  ;; a scalar basis blade
  ([weight]
   {:weight weight :blade 0 :grade 0})
  ;; a proper basis blade
  ([blade weight]
   (cond (keyword blade)
         (let [[blade grade] (blade blade-label-hash)]
           {:weight weight :blade blade :grade grade})
         :else
         {:weight weight :blade blade :grade (bit-count blade)})))

(defn ->bb-rev
  "Produce a reversed basis blade."
  [a]
  (let [{blade :blade weight :weight grade :grade} a]
    (->bb blade (* weight (minus-pow 0.5 grade (dec grade))))))

(defn ->bb-inversion
  "Produce a grade inversion of the basis blade."
  [a]
  (let [{blade :blade weight :weight grade :grade} a]
    (->bb blade (* weight (minus-pow grade)))))

(defn ->bb-conj
  "Produce a clifford conjugate of the basis blade."
  [a]
  (let [{blade :blade weight :weight grade :grade} a]
    (->bb blade (* weight (minus-pow 0.5 grade (inc grade))))))

(defn ->bb-clone [bb] (->bb (:blade bb) (:weight bb)))
(defn ->bb-copy [bb] (->bb (:blade bb) (:weight bb)))

(defn show-bb [bb]
  "not implemented")



(defn order-bb
  "Determine the order of blades and return 1 or -1.
  An even number of swaps : return +1.
  An odd number of swaps : return -1.
  This is for anticommutivity.
  It checks the relative positions of the bits
  and determines whether the result needs to be flipped.
  (see GAfCS fig. 19.1"
  [a b]
  (loop [a (bit-shift-right a 1), sum 0]
    (if (= 0 a)
      (if (even? sum) +1 -1)
      (recur (bit-shift-right a 1)
             (+ sum (bit-count (bit-and a b)))))))

(defn gp-bb
  "Geometric product of basis blades.
  (see GAfCS fig. 19.2"
  [a b]
  (let [a-blade (:blade a), b-blade (:blade b)]
    (->bb (bit-xor a-blade b-blade)
          (* (order-bb a-blade b-blade) (:weight a) (:weight b)))))

(defn op-bb
  "Outer product of basis blades.
  Check for dependencies.
  (see GAfCS fig. 19.2"
  [a b] (if (= 0 (bit-and a b)) (gp-bb a b) (->bb 0.0)))



;;;;========= Metric are used in metric product of basis-blades

(def metric-r4:1-values
  (into {}
        (map (fn [[[a b] v]]
               [[(a blade-label-hash) (b blade-label-hash)] v])
             {[:no :ni] -1, [:e1 :e1] 1, [:e2 :e2] 1, [:e3 :e3] 1, [:ni :no] -1})))

(declare mp-bb)

(defn metric-transform
  "Transform a basis-blade to a new basis."
  [a m]
  (loop [ix 0, blade (:blade a), new-blades [(->bb (:weight a))]]
    (cond (= 0 blade) new-blades
          (even? blade) (recur (inc ix) (bit-shift-right blade 1) new-blades)
          :else (for [[[i j] v] m, nb new-blades
                      :when (= ix i)]
                  (mp-bb nb (->bb (bit-shift-left 1 i) v))))))

(defmacro build-metric-inverse-eigen-hash [basis-blades])

(metric-transform (->bb :e1 3.0) metric-r4:1-values)

;(defn ->metric
;  "INCOMPLETE : Construct a metric from the input matrix."
;  [m]
;  (let [eig (metric-eigen-value-decomp m)
;        inv-eig (transpose eig)
;        eig-metric (for #() (get eig :real))
;        is-diagonal? (is-diagnonal m)
;
;        [is-euclidean? is-anti-euclidean?]
;        (if (not is-diagonal?) [false false]
;          (loop [is? true, anti? true, ix 0]
;            (let [value (get-in m [ix ix])]
;              (cond value [is? anti?]
;                    (not= 1.0 value)
;                    (recur false anti? (inc ix))
;                    (not= -1.0 value)
;                    (recur is? false (inc ix))
;                    ))))]
;    {:is-diagonal? is-diagonal?
;     :eigen-basis (fn [bb & bbs] nil)} ))


; (def metric-r4:1
;   {:keys metric-r4:1-values
;    :eigen-metric []
;    :bb->eigen-basis #(get metric:r4-1-inverse-eigen % %) })


;;;;========= Basis-blade functions which use a Metric


(defn mp-bb-res
  "Metric product of basis blades in a specified metric.
  Computes the geometric product of two basis blades in
  restricted non-euclidian metric."
  [a b metric]
  (loop [metric-index 0
         result (gp-bb a b)
         blade (bit-and (:blade a) (:blade b))]
    (if
      ;; no annihilating blades
      (= 0 blade) result
      (recur
       (inc metric-index)
       (if (= 0 (bit-and blade 1)) result
         ;; multiply annihilating blade's weight by metric
         (update-in result [:weight] * (get metric metric-index)))
       (bit-shift-right blade 1)))))

(defn mp-bb
  "Computes the geometric product of two basis blades in
  arbitrary non-euclidian metric.
  Returns an array list of "
  [a b m]
  (let [{:keys [bb->eigen-basis eigen-metric bb->metric-basis]} m]
    (bb->metric-basis
     (map (fn [a b] (mp-bb-res a b eigen-metric))
          (for [eba (bb->eigen-basis a) ebb (bb->eigen-basis b)]
            [eba ebb])))))


(defn ip-bb-aux0
  "Applies the rules to turn a geometric product into an inner product
  * ga : Grade of argument 'a'
  * gb : Grade of argument 'b'
  * bb : the basis blade to be filtered
  * prod-type : the type of inner product required:
  LEFT_CONTRACTION
  RIGHT_CONTRACTION
  HESTENES_INNER_PRODUCT or
  MODIFIED_HESTENES_INNER_PRODUCT
  return either a 0 basis blade, or result. "
  [ga gb bb prod-type]
  (case prod-type
    :left-contraction
    (cond (> ga gb) (->bb 0.0)
          (= (:grade bb) (- ga gb)) (->bb 0.0)
          :else bb)
    :right-contraction
    (cond (< ga gb) (->bb 0.0)
          (= (:grade bb) (- gb ga)) (->bb 0.0)
          :else bb)
    :hestenes-inner-product
    (cond (zero? ga) (->bb 0.0)
          (zero? gb) (->bb 0.0)
          (not= (:grade bb) (Math/abs (- ga gb))) (->bb 0.0)
          :else bb)
    :modified-hestenes-inner-product
    (cond (not= (:grade bb) (Math/abs (- ga gb))) (->bb 0.0)
          :else bb) ))


(defn ip-bb-aux1
  "Convert a geometric product into an inner product."
  [ga gb bb prod-type]
  (filter #(not (zero? (:weight %)))
          (map #(ip-bb-aux0 ga gb % prod-type) bb)))


(defn ip-bb
  "Inner product of basis blades."
  [a b metric prod-type]
  (ip-bb-aux1 (:grade a) (:grade b)
              (mp-bb a b metric) prod-type))

(defn =bb
  "Check for equality."
  [a b]
  (cond (not= (:blade a) (:blade b)) false
        (not= (:weight a) (:weight b)) false
        :else true))

(defn ->round-bb
  "Rounds the weight of a basis blade to the nearest multiple of scale.
  This is useful when eigen-basis is used to perform products in
  arbitrary metric, leading to small round off errors.
  You do not want to keep these round off errors when
  you are computing a multiplication table."
  [a scale epsilon]
  (let [weight (:weight a)
        new-weight (* scale (rem (:weight a) scale))
        change (Math/abs (- weight new-weight))]
    (if (< (- epsilon) change (+ epsilon))
      (->bb (:blade a) new-weight)
      a)))



;;;;========= Multivectors are represented by a list of weighted basis-blades

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
    (loop [a-s a-bases, b-s b-bases, c {}]
      (cond
       (empty? a-s) (clean-mv (compress-mv c))
       (empty? b-s) (recur (rest a-s) b-bases c)
       :else (let [prod (gp-bb (second (first a-s))
                               (second (first b-s))) ]
               (recur a-s (rest b-s) (conj c [(:blade prod) prod] ) ))))))



(defn ->mv
  "Make a multivector from a set of weighted blades."
  [a b]
  {:bases {(get a :blade) a (get b :blade) b}} )


(let [a (->bb :e1 5)
      b (->bb :e2 11)]
  (facts "about blades"
         (fact "about blade products"
               (gp-bb a b) =>
               '{:blade 6, :weight 55, :grade 2})))

(facts "about multivectors"
       (fact "check bit count on 2r0011"
             (bit-count 3) => 2)
       (fact "check general product"
             (gp-mv (->mv (->bb :e1 5) (->bb :e2 11))
                    (->mv (->bb :e1 2) (->bb :e2 3))) =>
             '{ 6 {:blade 6 :weight -22 :grade 2}
                0 {:blade 0 :weight 33 :grade 0} }))



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
       (->mv {:blade b1 :grade (bit-count b1) :weight (/ w 2.0) }
             {:blade b2 :grade (bit-count b2) :weight (/ w 2.0) }))

     ;; contains the infinite
     (not= 0 (bit-and t ni))
     (let [b (bit-xor t no)
           b1 (bit-xor b e+)
           b2 (bit-xor b e-)]
       (->mv {:blade b1 :grade (bit-count b1) :weight (* w -1.0) }
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
       (->mv {:blade b1 :grade (bit-count b1) :weight w}
             {:blade b2 :grade (bit-count b2) :weight (/ w -2.0) }))

     ;; contains the infinite
     (not= 0 (bit-and t e-))
     (let [b (bit-xor t e-)
           b1 (bit-xor b no)
           b2 (bit-xor b ni)]
       (->mv {:blade b1 :grade (bit-count b1) :weight w }
             {:blade b2 :grade (bit-count b2) :weight (/ w 2.0)})) )))










