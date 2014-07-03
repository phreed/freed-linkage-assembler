(ns isis.geom.action.coincident-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
            [isis.geom.model.invariant
             :refer [marker->invariant?
                     marker->add-invariant!]]
            [isis.geom.machine
             [functions :refer [translate
                                vec-diff
                                gmp]]
             [auxiliary :refer [dof-3r:p->p]] ]))


(defn- coincident->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained followed by the marker that is constrained."
  [ikb m1 m2]
  (cond (marker->invariant? ikb m2 :p) [m1 m2]
        (marker->invariant? ikb m1 :p) [m2 m1]
        :else false))

(defn- coincident->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [ikb _ m2] (marker->add-invariant! ikb m2 :p))


(defn coincident->transform-dispatch
  "Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof]."
  [ikb m1 m2]
  (let [[[link-name _] _] m1
        link @(link-name (:l ikb))]
    {:tdof (:# (:tdof link)) :rdof (:# (:rdof link))}))

(defmulti coincident->transform!
  "Transform the links and ikb so that the constraint is met."
  #'coincident->transform-dispatch
  :default nil)



(defmethod master/precondition?->transform!
  :coincident
  [ikb constraint]
  (let [{m1 :m1 m2 :m2} constraint]
    (when-let [ [m1 m2] (coincident->precondition? ikb m1 m2) ]
      (coincident->transform! ikb m1 m2)))
  true)


(defn- transform!->0-3-coincident
  "PFT entry: (0,3,coincident)

Initial status:
  0-TDOF(?geom, ?point)
  3-RDOF(?geom)

Plan fragment:
  begin
  3r/p-p(?geom, ?point,
    gmp(?m-2), gmp(?m-1));
  R[0] = vec-diff(gmp(?m-2), ?point);
  end;

New status:
  0-TDOF(?geom, ?point)
  1-RDOF(?geom, R[0], nil, nil)

Explanation:
  Geom ?geom cannot translate, so the coincident
  constraint is satisfied by a rotation.
  After the constraint is satisfied, ?geom can still rotate
  about the line connecting ?m-2 and ?point.
  "
  [ikb m1 m2]
  (let [ [[m1-link-name _] _] m1
        m1-link (get-in ikb [:l m1-link-name])
        point (get-in @m1-link [:p :e])
        gmp1 (gmp m1 ikb)
        gmp2 (gmp m2 ikb)
        link-place (dof-3r:p->p @m1-link point gmp2 gmp1)
        r0 (vec-diff gmp2 point)]
    (dosync
     (alter m1-link assoc
            :p link-place
            :rdof {:# 1, :p r0}) )))

(defmethod coincident->transform!
  {:tdof 0 :rdof 3}
  [ikb m1 m2]
  (transform!->0-3-coincident ikb m1 m2))



(defn- transform!->3-3-coincident
  "PFT entry: (3,3,coincident)

  Initial status:
  3-TDOF(?geom)
  3-RDOF(?geom)

  Plan fragment:
  begin
  translate(?geom,
  vec-diff(gmp(?m-1), gmp(?m-2));
  R[0] = gmp(?m-2);
  end;

  New status:
  0-TDOF(?geom, R[0])
  3-RDOF(?geom)  <no change>

  Explanation:
  Geom ?geom is free to translate, so the translation
  vector is measured and the ?geom is moved.
  No checks are required.
  "
  [ikb m1 m2]
  (let [[[m1-link-name _] _] m1
        m1-link (get-in ikb [:l m1-link-name])
        gmp1 (gmp m1 ikb)
        gmp2 (gmp m2 ikb)]
    (dosync
     (alter m1-link assoc
            :p (translate (:p @m1-link) (vec-diff gmp1 gmp2))
            :tdof {:# 0, :p gmp2} ) )))

(defmethod coincident->transform!
  {:tdof 3 :rdof 3}
  [ikb m1 m2]
  (transform!->3-3-coincident ikb m1 m2))
