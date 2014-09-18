(ns isis.geom.algebra.versor
  "The functions for developing and working with versors.
  see https://github.com/weshoke/versor.js/blob/mas ter/versor.js
  "
  (:import [java.lang.Character]))


(defrecord Blade [^int bitmap ^double weight])



(defn make->blade
  "Data structure representing a blade (coordinate + scale factor)

  bc - bitwise representation of coordinate
  wt - scale factor"
  [bc wt] (->Blade bc wt))

(defrecord Type [key bases name generated? dual?])

(defn make->type
  ""
  [key bases name] (->Type key bases name false false))


(defn classname [name] (str "_" name))

(defn grade
  "Calculate the grade of a coordinate
  bc - bitwise representation of coordinate.
  Count the number of active bits."
  [b]
  (loop [cnt 0 b b]
    (if (= 0 b)
      cnt
      (let [b1 (bit-shift-right b 1)
            incr (if (bit-test b 0) 1 0)]
        (recur (+ cnt incr) b1)))))

(defn sign
  "Calculate the sign of the product of two coordinates
  a - bitwise representation of coordinate
  b - bitwise representation of coordinate"
  [a b]
  (loop [n (bit-shift-right a 1)
         sum 0]
    (if (= 0 n)
      (if (bit-test sum 0) -1 1)
      (recur (bit-shift-right n 1)
             (+ sum (grade (bit-and n b))))))  )


(defn product
  "Calculate the product between two coordinates
  a - bitwise representation of coordinate
  b - bitwise representation of coordinate
  returns a blade."
  [a  b]
  (make->blade (bit-xor a b) (sign a b)))


(defn outer
  "Calculate the outer product between two coordinates
  a - bitwise representation of coordinate
  b - bitwise representation of coordinate
  returns a blade."
  [a b]
  (if (= 0 (bit-and a b)) (product a b) (make->blade 0 0)))

(defn involute
  "Derive the involute blade given a bitwise representation of the coordinate."
  [x]
  (make->blade x (if (odd? (grade x)) -1 1)))

(defn reversed [x]
  (let [g (grade x)
        o (/ (* g (dec g)) 2)]
    (make->blade x (if (odd? o) -1 1))))

(defn conjugate [x]
  (let [g (grade x)
        o (/ (* g (inc g)) 2)]
    (make->blade x (if (odd? o) -1 1))))


(defn basis-string
  "Calculate the name of a coordinate
  b - bitwise representation of coordinate.
  2r01101 => e134 "
  [basis]
  (loop [b basis, n 0, res ""]
    (if (> 1 b)
      (cond (> 1 n) "s"
            :else (str "e" res))
      (recur (bit-shift-right b 1)
             (inc n)
             (if (bit-test b 0) (str res (inc n)) res)) )))


(defn basis-bit
  "The inverse of basis-string.
  Given a string compute the basis.
  e134 => 2r1101
  s => 2r0000 "
  [basis-name]
  (if (= "s" basis-name) 0
    (reduce
     (fn [w c]
       (if (Character/isDigit c)
         (bit-set w (dec (Character/digit c 10)))
         w))
     0 basis-name)))

(defn basis-bits
  "Given a seq of basis names build a seq of bitwise coordinates."
  [bases]
  (map #(basis-bit %) bases))


(defn basis-names
  "Sort the list of basis-coordinates into a list of basis-strings."
  [ty] (map basis-string (sort < ty)))

(defn key-check
  "Compare two lists of names"
  [k1 k2] (= k1 k2))

(defn order
  "Return the order of the supplied multi-vector."
  [c]
  (let [tblades (map [] #(Integer/parseInt %) c)
        ordered-tblades (sort < tblades)]
    {:blades ordered-tblades, :inst c}))

(defn compress
  "Collect like terms for a set of blades.
  Given a seq of blades, if two terms have
  the same bit encoding sum their weights. "
  [xs]
  (->> xs
       (group-by :bitmap)
       (map (fn [[k v]]
              (make->blade k
                           (reduce #(+ %1 (:weight %2)) 0.0 v))))
       (into [])))

(def print-lines
  "Write out the specified number of lines from the input."
  (let [eol #".*(?:(?:\r\n|\n|\r)|$)"
        writer (ref str)]
    (fn [text from to]
      (->>
       (re-seq eol text)
       (drop from)
       (take (- to from))
       (map-indexed #(@writer (inc %1) "\t" %2))))))


(defmacro versor->create
  [name-space props]
  `(do
     (defn ~(symbol "i*") [~(symbol 'a) ~(symbol 'b)] )
     (defn ~(symbol "o*") [~(symbol 'a) ~(symbol 'b)] )
     (defn ~(symbol "t*") [~(symbol 'a) ~(symbol 'b)] )
     (defn ~(symbol "dual") [~(symbol 'a)] )
     (defn ~(symbol "pnt")
       [~(symbol 'x) ~(symbol 'y) ~(symbol 'z)
        ~(symbol 'no) ~(symbol 'ni) ]
       {:e1 ~(symbol 'x) :e2 ~(symbol 'y) :e3 ~(symbol 'z) :e4 ~(symbol 'no) :e5 ~(symbol 'ni)})
     ))


;var printLines = function(text, from, to) {
;	var lines = text.match(/^.*((\r\n|\n|\r)|$)/gm);
;	from = from || 0;
;	to = to || lines.length;
;
;	for(var i=from; i < to; ++i) {
;		console.log((i+1)+"\t"+lines[i].substr(0, lines[i].length-1));
;	}
;}


;/*	Representation of a GA space
;*/
;var Space = function(props) {
;	props = props || {};
;	props.metric = props.metric || [1, 1, 1];
;	props.types = props.types || [];
;	props.binops = props.binops || [];
;
;	this.metric = props.metric;
;	this.basis = this.buildBasis();
;	this.types = this.buildTypes();
;	if(props.conformal) {
;		this.values = this.buildConformalValues();
;		this.products = this.buildConformal();
;	}
;	else {
;		this.products = this.buildEuclidean();
;	}
;	this.subspaces = this.buildSubspaces();
;	this.registerSubspaces();
;	this.createTypes(props.types);
;
;	this.api = this.generate(props);
;	for(var name in this.api.constructors) {
;		this[name] = this.api.constructors[name];
;	}
;	this.initialized = true;
;}
;
;Space.prototype.generate = function(props) {
;	var binopCode = this.generateBinops(props.binops);
;	var typeCode = this.generateRegisteredTypes();
;	var typeCodeAliases = {};
;	for(var name in typeCode) {
;		var ty = this.types[name];
;		if(ty.alias && name == ty.alias) {
;			typeCodeAliases[name] = typeCode[name];
;		}
;	}
