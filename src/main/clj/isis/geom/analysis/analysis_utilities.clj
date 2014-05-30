(ns isis.geom.analysis.analysis-utilities
  "Algorithms : Utilities E.3.1"
  (:require isis.geom.machine.functions) )

(def ^:dynamic *invariant-position*
  "A list used to store the names of markers which have invariant positions"
  (ref #{}) )

(def ^:dynamic *invariant-z*
  "A list used to store the names of markers which have invariant z-axes"
  (ref #{}) )

(def ^:dynamic *invariant-x*
  "A list used to store the names of markers which have invariant x-axes"
  (ref #{}) )

(defn ^:dynamic *clear-invariants!*
  "clear the invariant lists"
  []
  (swap! @*invariant-position* #{})
  (swap! @*invariant-z* #{})
  (swap! @*invariant-x* #{}))

(defn add-invariant!
  "Abstract away the addition of the invariant so
  programs do not have to reference a global variable."
  [?name ?type]
  (dosync
   (case ?type
     :position (alter *invariant-position* conj ?name)
     :z (alter *invariant-z* conj ?name)
     :x (alter *invariant-x* conj ?name))) )

(defn invariant?
  "Abstract the testing of invariance so programs
  do not have to reference a global variable."
  [?name ?type]
  (find ?name
        (case ?type
          :position @*invariant-position*
          :z @*invariant-z*
          :x @*invariant-x*)) )

(def constraint-precondition
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained."
  {:coincident (fn [?m-1 ?m-2] (when (invariant? ?m-1 :position) ?m-2))
   :in-line-fp (fn [?m-1 ?m-2]
                    (cond (invariant? ?m-1 :position)  ?m-2
                          (and (invariant? ?m-2 :position)
                                (invariant? ?m-2 :z))
                           ?m-1))
   :in-plane-fp (fn [?m-1 ?m-2]
                    (cond (invariant? ?m-1 :position)  ?m-2
                          (and (invariant? ?m-2 :position)
                               (invariant? ?m-2 :z))
                           ?m-1))
   :parallel-z (fn [?m-1 ?m-2] (when (invariant? ?m-1 :position) ?m-2))
   :offset-z (fn [?m-1 ?m-2] (when (invariant? ?m-1 :position) ?m-2))
   :offset-x (fn [?m-1 ?m-2]
                    (when (and (invariant? ?m-1 :z)
                               (invariant? ?m-2 :z)
                               (invariant? ?m-1 :x))
                           ?m-2))
   :helical (fn [?m-1 ?m-2]
                    (when (and (invariant? ?m-1 :z)
                               (invariant? ?m-2 :z))
                           ?m-2)) })


(def constraint-postcondition
  "Associated with each constraint type is a function which
  checks the postconditions for after the constraint has been satisfied."
  {:coincident (fn [_ ?m-2] (add-invariant! ?m-2 :position))
   :in-line-fp (fn [_ _])
   :in-plane-fp (fn [_ _])
   :parallel-z (fn [_ ?m-2] (add-invariant! ?m-2 :z))
   :offset-z (fn [_ _])
   :offset-x (fn [_ ?m-2] (add-invariant! ?m-2 :z))
   :helical (fn [_ _]) } )


(defn precondition-satisfied?
  [?constraint ?m-1 ?m-2]
  ((get constraint-precondition ?constraint) ?m-1 ?m-2))

(defn assert-preconditions
  [?constraint ?m-1 ?m-2]
  ((get constraint-postcondition ?constraint) ?m-1 ?m-2))

(defn c-type "The constraint type." [?constraint] (nth ?constraint 0))
(defn c-m1 "The first marker of the constraint." [?constraint] (nth ?constraint 1))
(defn c-m2 "The second marker of the constraint." [?constraint] (nth ?constraint 2))

(defn find-geom-for-marker
  "If marker is not one of m13-1 m13-3 m23-3, consider it a grounded marker."
  [?m]
  (case ?m
    (m13-1 m13-3) 'l13
    (m23-2 m23-3) 'l23
    else 'l12 ))

(defn tdof "The TDOF component of the geom ?status." [?status] (nth ?status 0))
(defn rdof "The RDOF component of the geom ?status." [?status] (nth ?status 1))
