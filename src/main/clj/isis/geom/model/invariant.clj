(ns isis.geom.model.invariant
  "Algorithms : Utilities E.3.1"
  (:require isis.geom.machine.functions) )


(defn init-marker-invariant
  "Create a marker invariant with references"
  []
  {:p (ref #{}) :z (ref #{}) :x (ref #{})})

(defn init-graph-invariant
  "Create a marker invariant with references"
  []
  {:p (ref {})})


(defn make->invariant
  "Create a invariant in the link as outlined in C.3
  :p : list of points having invariant position
  :p1d  : list of (point locus) pairs, where point is restricted to a 1d-locus
  :p2d  : list of (point locus) pairs, where point is restricted to a 2d-locus
  :v    : list of vectors with invariant orientation
  :v1d  : list of (vector locus) pairs, where the
          orientation of the vector is restricted to a 1d-locus"
  [& {:as opts}]
  (merge {:p [], :p1d [], :p2d [], :v [], :v1d []} opts))

(defn marker->add-invariant!
  "Abstract away the addition of the invariant so
  programs do not have to reference a global variable.
  This is a set of lists: position, z-axis, and x-axis vectors."
  [marker-invs marker-name invariant-type]
  (let [{:keys [p z x]} marker-invs]
    (dosync
     (case invariant-type
       :p (alter p conj marker-name)
       :z (alter z conj marker-name)
       :x (alter x conj marker-name))) ))


(defn marker->invariant?
  "Abstract the testing of invariance so programs
  do not have to reference a global variable."
  [marker-invs marker-name invariant-type]
  (let [{inv-p :p inv-z :z inv-x :x} marker-invs]
    (contains? marker-name
               (case invariant-type
                 :p @inv-p
                 :z @inv-z
                 :x @inv-x)) ))


