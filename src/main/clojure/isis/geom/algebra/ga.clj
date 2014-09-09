(defprotocol Productable
  (total [this that & those] "take the total product of this, that and those")
  (interior [this that & those] "take the inner product of this, that and those")
  (exterial [this that & those] "take the outer product of this, that and those"))


(extend-type MultiVector
  Productable)
