;; see C3.js https://github.com/weshoke/versor.js/blob/master/C4.js
(ns isis.geom.machine.c3ga)


(defprotocol MultiVector
  "The multivector is an object of the
  geometric algebra having basies."
  (>< [mv1 mv2 & mvs] "The geometric product.")
  (A [mv1 mv2 & mvs] "The outer product.")
  (<< [mv1 mv2]  "The left conjunction (an inner product).")
  (push-mink [mv] "A push Minkowski factory?")
  (pull-mink [mv] "A pull Minkowski factory?"))

(defprotocol Subspace
  "A subspace of vectors."
  (D [space] "produce the dual subspace.")
  (rejection [ss1 ss2]
             "(A^B)!B ")
  (projection [ss1 ss2]
             "(A<<B)!B ")
  (meet [ss1 ss2]
        "The intersection of the two subspaces.")
  (join [ss1 ss2]
        "The extension."))

(defrecord Scalar [value]
  MultiVector
  (>< [mv1 mv2 & mvs] "unimplemented")
  (A [mv1 mv2 & mvs] "unimplemented")
  Subspace
  (D [space] "unimplemented"))

(defrecord Vector [value]
  MultiVector
  (>< [mv1 mv2 & mvs] "unimplemented")
  (A [mv1 mv2 & mvs] "unimplemented")
  Subspace
  (D [space] "unimplemented"))

(defrecord Bivector [value]
  MultiVector
  (>< [mv1 mv2 & mvs] ["unimplemented"])
  (A [mv1 mv2 & mvs] ["unimplemented"])
  Subspace
  (D [space] "unimplemented"))

(defrecord Trivector [value]
  MultiVector
  (>< [mv1 mv2 & mvs] ["unimplemented"])
  (A [mv1 mv2 & mvs] ["unimplemented"])
  Subspace
  (D [space] "unimplemented"))


(defprotocol Conformal
  "?"
  (weight [c] "The weight, magnitude, of the object.")
  (dir [c] "The attitude, direction, of the object.")
  (loc [c] "The location of the object."))

(defprotocol Round)

