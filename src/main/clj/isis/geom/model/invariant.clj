(ns isis.geom.model.invariant
  "Algorithms : Utilities E.3.1"
  (:require isis.geom.machine.functions) )


(defn init-marker-invariant-s
  "Create a marker invariant with references"
  []
  {:p (ref #{}) :z (ref #{}) :x (ref #{})})

(defn init-link-invariant-s
  "Create a map of link invariants within references."
  [] {})

(defn init-link-invariant
  "An invariant for a single link.
  :p - the placement of the link in global coordinates.
  :tdof - a tdof descrittion
     :# - the number of dof remaining
  :rdof - a tdof description
     :# - the number of dof remaining."
  [type]
  (case type
    :fixed (ref {:tdof {:# 0} :rdof {:# 0}
            :p {:e [0.0 0.0 0.0] :z [0.0 0.0 1.0] :x [1.0 0.0 0.0]}})

    :free (ref {:tdof {:# 3} :rdof {:# 3}
            :p {:e [0.0 0.0 0.0] :z [0.0 0.0 1.0] :x [1.0 0.0 0.0]}}) ))


(defn make->invariant
  "Create a invariant in the link as outlined in C.3
  :p : list of points having invariant position
  :e1d  : list of (point locus) pairs, where point is restricted to a 1d-locus
  :e2d  : list of (point locus) pairs, where point is restricted to a 2d-locus
  :v    : list of vectors with invariant orientation
  :v1d  : list of (vector locus) pairs, where the
          orientation of the vector is restricted to a 1d-locus"
  [& {:as opts}]
  (merge {:p [], :e1d [], :e2d [], :v [], :v1d []} opts))

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
  [ikb marker invariant-type]
  (let [{m :m, g :g} ikb,
        marker-invs (invariant-type m),
        [marker-name _] marker]
    (contains? @marker-invs marker-name)))

