(ns isis.geom.analysis.analysis-utilities
  (:require isis.geom.machine.functions) )

(def *invariant-position*
  "A list used to store the names of markers which have invariant positions"
  nil )

(def *invariant-z*
  "A list used to store the names of markers which have invariant z-axes"
  nil )

(def *invariant-x*
  "A list used to store the names of markers which have invariant x-axes"
  nil )

(defn *clear-invariants*
  "clear the invariant lists"
  ()
  nil )

(defn add-invariant
  "Abstract away the addition of the invariant so
  programs do not have to reference a global variable."
  [?name ?type]
  (ecase ?type
         (:position (pushnew name *invariant-position*))
         (:z (pushnew name *invariant-z*))
         (:x (pushnew name *invariant-x*))))

(defn invariant?
  "Abstract the testing of invariance so programs
  do not have to reference a global variable."
  [?name ?type]
  (member ?name
          (ecase ?type
         (:position (pushnew name *invariant-position*))
         (:z (pushnew name *invariant-z*))
         (:x (pushnew name *invariant-x*)))) )

(def *constraint-precondition*
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrainted."
  {:coincident (fn [?m-1 ?m-2] (when (invariant? ?m-1 :position) ?m-2))
   :in-line-fp (fn [?m-1 ?m-2]
                    (cond ((invariant? ?m-1 :position) ?m-2)
                          ((and (invariant? ?m-2 :position)
                                (invariant? ?m-2 :z))
                           ?m-1)))
   :in-plane-fp (fn [?m-1 ?m-2]
                    (cond ((invariant? ?m-1 :position) ?m-2)
                          ((and (invariant? ?m-2 :position)
                                (invariant? ?m-2 :z))
                           ?m-1)))
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


(def *constraint-postcondition*
  "Associated with each constraint type is a function which
  checks the postconditions for after the constraint has been satisfied."
  {:coincident (fn [?m-1 ?m-2] (declare (ignore ?m-1)
                                        (add-invariant ?m-2 :position)))
   :in-line-fp (fn [?m-1 ?m-2] (declare (ignore ?m-1 ?m-2)))
   :in-plane-fp (fn [?m-1 ?m-2] (declare (ignore ?m-1 ?m-2)))
   :parallel-z (fn [?m-1 ?m-2] (declare (ignore ?m-1)
                                        (add-invariant ?m-2 :z)))
   :offset-z (fn [?m-1 ?m-2] (declare (ignore ?m-1 ?m-2)))
   :offset-x (fn [?m-1 ?m-2] (declare (ignore ?m-1))
                                        (add-invariant ?m-2 :z))
   :helical (fn [?m-1 ?m-2] (declare (ignore ?m-1 ?m-2))) } )


(defn precondition-satisfied?
  [?constraint ?m-1 ?m-2]
  (funcall (cdr (assoc ?constraint *constraint-precondition)) ?m-1 ?m-2))

(defn assert-preconditions
  [?constraint ?m-1 ?m-2]
  (funcall (cdr (assoc ?constraint *constraint-postcondition)) ?m-1 ?m-2))

(defn c-type "The constraint type." [constraint] (first constraint))
(defn c-m1 "The first marker of the constraint." [constraint] (second constraint))
(defn c-m2 "The second marker of the constraint." [constraint] (third constraint))

(defn find-geom-for-marker
  "If marker is not one of m13-1 m13-3 m23-3, consider it a grounded marker."
  [?m] (case ?m
         (m13-1 m13-3) 'l13
         (m23-3 m23-3) 'l23
         else 'l12 ))

(defun tdof "The TDOF component of the geom ?status." [status] (first status))
(defun rdof "The RDOF component of the geom ?status." [status] (second status))
