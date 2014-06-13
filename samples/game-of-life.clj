;(use 'clojure.pprint)

(defn empty-board
  "create an empty board"
  [w h]
  (vec (repeat w (vec (repeat h nil)))))

(defn populate
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board living-cells))

(def glider (populate (empty-board 6 6)
                      #{[2 0] [2 1] [2 2] [1 2] [0 1]}))

(pprint glider)

(defn neighbors
  "Produce a list of the neighbors of the specified cell"
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn count-neighbors
  "count those neighbors who are in the board or have non-nil values"
  [board loc]
  (count (filter #(get-in board %) (neighbors loc))) )

(defn indexed-step
  [board]
  (let [w (count board)
        h (count (first board))]
    (loop [new-board board, x 0, y 0]
      (cond
       (>= x w) new-board
       (>= y h) (recur new-board (inc x) 0)
       :else
       (let [new-liveness
             (case (count-neighbors board [x y])
               2 (get-in board [x y])
               3 :on
               nil )]
         (recur (assoc-in new-board [x y] new-liveness) x (inc y)))))))

(-> (iterate indexed-step glider) (nth 8) pprint)


(defn window
  "return a sequence of 3-item windows"
  ([coll]
   (window nil coll))
  ([pad coll]
   (partition 3 1 (concat [nil] coll [nil]))))

(defn cell-block
  "create a seq of 3x3 windows from triple of 3 seq"
  [[left mid right]]
  (window (map vector left mid right))))


