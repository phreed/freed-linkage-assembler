(ns isis.geom.analysis.locus-analysis
  "Locus analysis : E.3.3
  This section presents an implementation of locus analysis.
  First, the various tables are defined, and interfacing
  functions are provided. "
  (:require
    (isis.geom.machine.functions)
    (isis.geom.design [graph-state
                       :refer [get-geom-status
                               update-geom-status!]])))

(def frog "ribbit")

(def point-locus-table
  "The point locus table, with each entry of the form '[tdof rdof] :locus'.
  This encoding assumes that for the [2 1] case,
  the axis is normal to the plane, allowing a planar locus."
  {[1 0] :line
   [2 0] :plane
   [0.5 0.5] :helix
   [0 1] :circle
   [1 1] :cylinder
   [2 1] :plane
   [0 2] :sphere
   [0 3] :sphere } )

(defn point-locus
  "Given a geom status of the form [tdof rdof],
  returns the locus if it exists, or nil otherwise."
  [?status]
  (get point-locus-table ?status))


(def line-locus-table
  "The line locus table, with each entry of the form '[tdof rdof] :locus'."
  {[1 0] :plane
   [0.5 0.5] :helical-surface
   [0 1] :hyperboloid } )

(defn line-locus
  "Given a geom status of the form [tdof rdof],
  returns the locus if it exists, or nil otherwise."
  [?status]
  (get line-locus-table ?status))


(def orient-locus-table
  "The orientation locus table, with each entry of the form '[tdof rdof] :locus'."
  {[0.5 0.5] :circle
   [0 1] :circle
   [1 1] :circle
   [2 1] :circle
   [3 1] :circle } )

(defn orient-locus
  "Given a geom status of the form [tdof rdof],
  returns the locus if it exists, or nil otherwise."
  [?status]
  (get orient-locus-table ?status))

(def locus-intersection
  "The ist of loci whose pairwise intersections yield
  a discrete set of points."
  #{[:line :line] [:line :plane] [:line :helix] [:line :circle] [:line :cylinder]
    [:line :sphere] [:line :helical-surface] [:line :hyperboloid] [:plane :helix]
    [:plane :circle] [:helix :helix] [:helix :circle] [:helix :cylinder]
    [:helix :sphere] [:helix :helical-surface] [:helix :hyperboloid]
    [:circle :circle] [:circle :cylinder] [:circle :sphere]
    [:circle :helical-surface] [:circle :hyperboloid] })

(defn loci-intersect-uniquely?
  "Returns 'true' if loci intersect uniquely."
  [?locus-1 ?locus-2]
  (or (contains? locus-intersection [?locus-1 ?locus-2])
      (contains? locus-intersection [?locus-1 ?locus-2]) ))

(defn point-point-intersect?
  "Returns 'true' if geoms with statuses ?status-1 and ?status-2
  have point loci that intersect in a discrete set of points."
  [?status-1 ?status-2]
  (let [pl1 (point-locus ?status-1)
        pl2 (point-locus ?status-2)]
    (loci-intersect-uniquely? pl1 pl2)))

(defn point-line-intersect?
  "Returns 'true' if a point locus from geom with ?status-1
  and a line locus from a geom with ?status-2 intersect in
  a discrete set of points."
  [?status-1 ?status-2]
  (let [p1 (point-locus ?status-1)
        l1 (line-locus ?status-2)]
    (loci-intersect-uniquely? p1 l1)))


(defn orient-orient-intersect?
  "Returns 'true' if geoms with statuses ?status-1 and
  ?status-2 have orientientation loci that intersect
  in a discrete set of points."
  [?status-1 ?status-2]
  (let [o1 (orient-locus ?status-1)
        o2 (orient-locus ?status-2)]
    (loci-intersect-uniquely? o1 o2)))



(def locus-analysis-cases
  "Information to use with the locus-analysis function.
  The locus analysis code below condenses the four
  algorithm fragments of Section 4.2 through the
  use of the variable locus-analysis-cases."
  [[:coincident point-point-intersect? :coincident :coincident :position]
   [:in-line point-line-intersect? :coincident :in-line :position]
   [:parallel-z orient-orient-intersect? :parallel-z :parallel-z :z]
   [:offset-z orient-orient-intersect? :parallel-z :parallel-z :z] ])

(defn locus-analysis
  "Perform one locus analysis reformulation and terminate."
  []
  (loop [[locus-case & remaining-cases]  locus-analysis-cases]
    (if (empty? remaining-cases)
      false
      (let [[c-type intersect-fn m1-c-type m2-c-type invariant-type] locus-case]
        ;; locus intersection is applicable when the
        ;; constraint type is present an the loci are intersectable.
        (if-not (and (joint-has-constraint-type? 'j3 c-type)
                     (intersect-fn (get-geom-status 'l13) (get-geom-status 'l23)))
          (recur remaining-cases)

          (let [x (intern (string (gensym "M-")))
                c (find-constraint-of-type 'j3 c-type)
                m1 (c-m1 c)
                m2 (c-m2 c)]
            ;; remove constraint and its symetric form
            (remove-constraint! 'j3 c)
            ;; add reformulated constraints to the appropriate geom
            (case (find-geom-for-marker m1)
              l13 (add-constraint 'j1 [m1-c-type x m1])
              l23 (add-constraint 'j2 [m1-c-type x m1]))
            (case (find-geom-for-marker m2)
              l13 (add-constraint 'j1 [m2-c-type x m2])
              l23 (add-constraint 'j2 [m2-c-type x m2]))
            ;; mark the auxiliary marker as having the proper invariant-type
            (add-invariant! x invariant-type)) ) ))))
