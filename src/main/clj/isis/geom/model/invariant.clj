(ns isis.geom.model.invariant
  "Algorithms : Utilities E.3.1"
  (:require isis.geom.machine.functions) )


(defn init-marker-invariant-s
  "Create a marker invariant with references"
  []
  {:p (ref #{}) :z (ref #{}) :x (ref #{})})

(defn init-link-invariant-s
  "Create a map of link invariants within references."
  [] (ref {}))

(defn init-link-invariant
  "An invariant for a single link.
  :p - the placement of the link in global coordinates.
  :tdof - a tdof descrittion
     :# - the number of dof remaining
  :rdof - a tdof description
     :# - the number of dof remaining."
  []
  {:p {:p1 0.0 :p2 0.0 :p3 0.0 :i1 0.0 :i2 0.0 :i3 1.0 :t 0.0}
   :tdof {:# 3} :rdof {:# 3}})


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
  [invariants marker invariant-type]
  (let [{m :m, g :g} invariants,
        marker-invs (invariant-type m),
        [marker-name _] marker]
    (contains? @marker-invs marker-name)))

