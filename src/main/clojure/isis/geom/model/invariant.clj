(ns isis.geom.model.invariant
  "Algorithms : Utilities E.3.1"
  (:require [isis.geom.machine.geobj :as ga]) )


(defn init-marker
  "Create a marker invariant with references"
  []
  {:loc (ref #{}) :dir (ref #{}) :twist (ref #{})})


(defn set-marker!
  "Abstract away the addition of the invariant so
  programs do not have to reference a global variable.
  The current types of invariant are:
    position, z-axis, and x-axis vectors."
  [kb link-name proper-name invariant-type]
  (alter (get-in kb [:invar invariant-type])
         conj [link-name proper-name]) )


(defn- marker?
  "Abstract the testing of invariance so programs
  do not have to reference a global variable."
  [marker marker-invs]
  (let [[marker-name _] marker
        [marker-link-name _] marker-name ]
    (cond (contains? @marker-invs marker-name) true
          (contains? @marker-invs [marker-link-name]) true
          :else false )))

(defn marker-position?
  "Abstract the testing of invariance so programs
  do not have to reference a global variable."
  [kb marker]
  (marker? marker (get-in kb [:invar :loc])))

(defn marker-direction?
  "Abstract the testing of invariance so programs
  do not have to reference a global variable."
  [kb marker]
  (marker? marker (get-in kb [:invar :dir])))

(defn marker-twist?
  "Abstract the testing of invariance so programs
  do not have to reference a global variable."
  [kb marker]
  (marker? marker (get-in kb [:invar :twist])))



(defn- link-filter
  [inv-set link-name]
  (into #{[link-name]}
        (filter #(let [[lname] %] (not= link-name lname))
                (seq inv-set))))

(defn set-link!
  "When a link has no degrees of freedom all properties
  for all of its markers become invariant."
  [kb link-name]
  (let [loc-inv (get-in kb [:invar :loc])
        z-axis-inv (get-in kb [:invar :dir])
        x-axis-inv (get-in kb [:invar :twist]) ]
    (alter loc-inv link-filter link-name)
    (alter z-axis-inv link-filter link-name)
    (alter x-axis-inv link-filter link-name)
    kb ))


(defn make
  "Create a invariant in the link as outlined in C.3
  :p : list of points having invariant position
  :e1d  : list of (point locus) pairs, where point is restricted to a 1d-locus
  :e2d  : list of (point locus) pairs, where point is restricted to a 2d-locus
  :v    : list of vectors with invariant orientation
  :v1d  : list of (vector locus) pairs, where the
          orientation of the vector is restricted to a 1d-locus"
  [& {:as opts}]
  (merge {:p [], :p1d [], :p2d [], :v [], :v1d []} opts))


(defn init-link
  "An invariant for a single link.
  :versor - the placement of the link in global coordinates.
      rotate is specified as a quaternion.
  :tdof - a tdof descrittion
     :# - the number of dof remaining
  :rdof - a tdof description
     :# - the number of dof remaining."
  [type]
  (case type
    :fixed (ref {:tdof {:# 0} :rdof {:# 0}
            :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0] }})

    :free (ref {:tdof {:# 3} :rdof {:# 3}
            :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}) ))

