(ns isis.geom.model.invariant
  "Algorithms : Utilities E.3.1"
  (:require isis.geom.machine.functions) )


(defn marker->add-invariant!
  "Abstract away the addition of the invariant so
  programs do not have to reference a global variable."
  [marker-invs marker-name invariant-type]
  (let [[inv-p inv-z inv-x] marker-invs]
    (dosync
     (case invariant-type
       :p (alter inv-p conj marker-name)
       :z (alter inv-z conj marker-name)
       :x (alter inv-x conj marker-name))) ))

(defn marker->invariant?
  "Abstract the testing of invariance so programs
  do not have to reference a global variable."
  [marker-invs marker-name invariant-type]
  (let [[inv-p inv-z inv-x] marker-invs]
    (contains? marker-name
               (case invariant-type
                 :p @inv-p
                 :z @inv-z
                 :x @inv-x)) ))


