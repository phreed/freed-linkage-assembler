;; see C3.js https://github.com/weshoke/versor.js/blob/master/C4.js

(ns isis.geom.machine.versor)

(defn make->blade
  "Data structure representing a blade (coordinate + scale factor)

	bc - bitwise representation of coordinate
	wt - scale factor"
  [bc, wt] {:id bc, :w wt })

(defn make->type
  ""
  [key bases name] { :key key, :bases bases, :name name, :generated false, :dual false })


(defn classname [name] (str "_" name))

(defn grade
  "Calculate the grade of a coordinate
  bc - bitwise representation of coordinate.
  Count the number of active bits."
  [bc]
  (loop [cnt 0 b0 bc]
    (if (= 0 b0)
      cnt
      (let [b1 (bit-shift-right b0 1)
            incr (if (bit-test b0 0) 1 0)]
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
  (make->blade (^ a b) (sign a b)))


(defn outer
  "Calculate the outer product between two coordinates
	a - bitwise representation of coordinate
	b - bitwise representation of coordinate
	returns a blade."
  [a b]
  (if (= 0 (bit-and a b)) (product a b) (make->blade 0 0)))

(defn involute [x]
  (make->blade x (if (odd? (grade x)) -1 1)))

(defn reversed [x]
  (let [g (grade x)
        o (/ (* g (- g 1)) 2)]
    (make->blade x (if (odd? o) -1 1))))

(defn conjugate [x]
  (let [g (grade x)
        o (/ (* g (+ g 1)) 2)]
    (make->blade x (if (odd? o) -1 1))))


(defn basis-string
  "Calculate the name of a coordinate
  b - bitwise representation of coordinate.
  2r1001 => e14 "
  [b0]
  (loop [b b0, n 0, res ""]
    (if (< 1 b)
      (if (< 0 n) (str "e" res) "s")
      (recur (bit-shift-right b 1)
             (inc n)
             (if (bit-test b 0) (str res (inc n)) res)) )))


(defn basis-bit
  "The inverse of basis-string.
  Given a string compute the basis.
  e14 => 2r1001 "
  [name]
  (if (= "s" name) 0
    (reduce #(if (Character/isDigit %2)
               (bit-set %1 (dec (Integer/parseInt (clojure.string/join [%2]))))
               %1) 0 name)))

(defn basis-bits
  "Given a seq of basis names build a seq of bitwise coordinates."
   [bases]
  (map #(basis-bit %) bases))


(defn basis-names
  "Sort the list of basis-coordinates into a list of basis-strings."
  [ty] (map basis-string (sort < ty)))

(defn key-check
  "Compare tow lists of names"
  [k1 k2] (= k1 k2))

(defn order
  "Return the order of the supplied ?"
  [c]
  (let [tblades (map [] #(Integer/parseInt %) c)
        ordered-tblades (sort < tblades)]
    {:blades ordered-tblades, :inst c}))

(defn compress
  "Collect like terms"
  [x]
  )
;	var tally = {};
;
;	// collect like terms
;	for(var i=0; i < x.length; ++i) {
;		var iv = x[i];
;		if(tally[iv.id]) {
;			tally[iv.id].w += iv.w;
;		}
;		else {
;			tally[iv.id] = blade(iv.id, iv.w);
;		}
;	}
;
;	var res = [];
;	for(var id in tally) {
;		var iv = tally[id];
;		if(iv.w != 0) {
;			res.push(iv);
;		}
;	}
;	return res;
;}

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
