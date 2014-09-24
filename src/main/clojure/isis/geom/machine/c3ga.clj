;; see C3.js https://github.com/weshoke/versor.js/blob/ms/C4.js
(ns isis.geom.machine.c3ga)


;; (defrecord Basis [^long blade ^double weight]
;;   (involution [this]

;;               "construct by involution" )
;;   (revertion [this] "construct by involution")
;;   (conjugation [this] "construct by involution")

;;   (gp [this that] "total product")
;;   (ip [this that] "inner product")
;;   (op [this that] "outer product")

;;   (gt [this that] "is the this greater than that")
;;   (eq [this that] "is the this equal to that") )

;; (defprotocol MultiVector
;;   "The multivector is an object of the
;;   geometric algebra having basies."
;;   (gp [mv1 mv2 & mvs] "The geometric product.")
;;   (op [mv1 mv2 & mvs] "The outer product.")
;;   (lp [mv1 mv2]  "The left conjunction (an inner product).")

;;   (push-mink [mv] "A push Minkowski factory?")
;;   (pull-mink [mv] "A pull Minkowski factory?"))

;; (defprotocol Subspace
;;   "A subspace of vectors."
;;   (dual [space] "produce the dual subspace.")
;;   (rejection [ss1 ss2] "(A^B)!B ")
;;   (projection [ss1 ss2] "(A<<B)!B ")
;;   (meet [ss1 ss2] "The intersection of the two subspaces.")
;;   (join [ss1 ss2] "The extension."))

;; (defrecord Scalar [value]
;;   MultiVector
;;   (ip [mv1 mv2 & mvs]
;;       ())
;;   (op [mv1 mv2 & mvs] "unimpl")
;;   Subspace
;;   (dual [space] "unimpl")
;;   rej)

;; (defrecord Vector [value]
;;   MultiVector
;;   (ip [mv1 mv2 & mvs] "unimpl")
;;   (op [mv1 mv2 & mvs] "unimpl")
;;   Subspace
;;   (dual [space] "unimpl"))

;; (defrecord Bivector [value]
;;   MultiVector
;;   (ip [mv1 mv2 & mvs] ["unimpl"])
;;   (op [mv1 mv2 & mvs] ["unimpl"])
;;   Subspace
;;   (dual [space] "unimpl"))

;; (defrecord Trivector [value]
;;   MultiVector
;;   (ip [mv1 mv2 & mvs] ["unimpl"])
;;   (op [mv1 mv2 & mvs] ["unimpl"])
;;   Subspace
;;   (dual [space] "unimpl"))


;; (defprotocol Conformal
;;   "Objects whose angles are preserved over transformation"
;;   (weight [c] "The weight, magnitude, of the object.")
;;   (dir [c] "The attitude, direction, of the object.")
;;   (loc [c] "The location of the object."))

;; (defprotocol Round
;;   "A round does not contain the infinity basis")

;; (defrecord Point
;;   MultiVector
;;   [^double e1 ^double e2 ^double e3])

;; (defrecord PointPair  )
;; (defrecord Circle  )
;; (defrecord Sphere  )

;; (defprotocol Flat
;;   "A flat does contain the infinity basis" )

;; (defrecord Line  )
;; (defrecord Plane  )


;; (defprotocol Versor
;;   (spin [this mv] "apply the versor to a multi-vector")
;;   (pin [this mv] "apply the versor to a multi-vector"))


;; (defprotocol Rotor
;;   (space-like [this] "returns the space-like component of this rotor")
;;   (time-like [this] "returns the time-like component of this rotor")
;;   (light-like [this] "returns the light-like component of this rotor")
;;   (exp [this mv] "applies this rotor to the provided bivector")
;;   (log [this] "applies this rotor to the provided bivector")
;;   )

;; (defrecord Translator
;;   (log [this] "applies this rotor to the provided bivector"))


;; (defrecord Motor
;;   (rot [this] "returns the Rotor component of this Motor")
;;   (dir [this] "returns the Translator component of this Motor")
;;   (exp [this line] "applies this rotor to the provided line")
;;   (log [this] "applies this rotor to the provided bivector") )

;; (defrecord Dilator
;;   (exp [this pnt-pair] "applies this dilator to the provided point-pair"))

