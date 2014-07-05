(ns isis.geom.action.coincident-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
            [isis.geom.model.invariant
             :refer [marker->invariant?
                     marker->add-invariant!]]
            [isis.geom.machine
             [geobj :refer [translate
                           vec-diff
                           gmp]]
             [auxiliary :refer [dof-3rp->p]] ]))


(defn- coincident->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained followed by the marker that is constrained."
  [ikb m1 m2]
  (cond (marker->invariant? ikb m2 :p) [m2 m1]
        (marker->invariant? ikb m1 :p) [m1 m2]
        :else false))

(defn- coincident->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [ikb _ m2] (marker->add-invariant! ikb m2 :p))


(defn coincident->transform-dispatch
  "Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  [ikb m1 m2]
  (let [[[link-name _] _] m2
        link @(link-name (:l ikb))]
    {:tdof (:# (:tdof link)) :rdof (:# (:rdof link))}))

(defmulti coincident->transform!
  "Transform the links and ikb so that the constraint is met."
  #'coincident->transform-dispatch
  :default nil)



(defmethod master/constraint-attempt?
  :coincident
  [ikb constraint]
  (let [{m1 :m1 m2 :m2} constraint]
    (when-let [ [ma1 ma2] (coincident->precondition? ikb m1 m2) ]
      (coincident->transform! ikb ma1 ma2)))
  true)


(defn- transform!->0-3-coincident
  "PFT entry: (0,3,coincident)

Initial status:
  0-TDOF(?m2-link, ?m2-point)
  3-RDOF(?m2-link)

Plan fragment:
  begin
  3r/p-p(?m2-link, ?m2-point,
    gmp(?m2), gmp(?m1));
  R[0] = vec-diff(gmp(?m2), ?m2-point);
  end;

New status:
  0-TDOF(?m2-link, ?m2-point)
  1-RDOF(?m2-link, R[0], nil, nil)

Explanation:
  Geom ?link cannot translate, so the coincident
  constraint is satisfied by a rotation.
  After the constraint is satisfied, ?link can still rotate
  about the line connecting ?m-2 and ?point.
  "
  [ikb m1 m2]
  (let [ [[m2-link-name m2-proper-name] _] m2
        m2-link (get-in ikb [:l m2-link-name])
        m2-point (get-in @m2-link [:p :e])
        gmp1 (gmp m1 ikb)
        gmp2 (gmp m2 ikb)]
    (dosync
     (alter (get-in ikb [:m :p]) disj [m2-link-name m2-proper-name])
     (alter m2-link merge
            ; (dof-3rp->p @m2-link m2-point gmp2 gmp1)
            {:rdof {:# 1, :a (vec-diff gmp2 m2-point)}} ) )))

(defmethod coincident->transform!
  {:tdof 0 :rdof 3}
  [ikb m1 m2]
  (transform!->0-3-coincident ikb m1 m2))



(defn- transform!->3-3-coincident
  "PFT entry: (3,3,coincident)

  Initial status:
  3-TDOF(?m2-link)
  3-RDOF(?m2-link)

  Plan fragment:
  begin
  translate(?m2-link, vec-diff(gmp(?m1), gmp(?m2));
  R[0] = gmp(?m2);
  end;

  New status:
  0-TDOF(?m2-link, R[0])
  3-RDOF(?m2-link)  <no change>

  Explanation:
  Link ?m2-link is free to translate, so the translation
  vector is measured and the ?m2-link is moved.
  No checks are required.
  "
  [ikb m1 m2]
  (let [[[m2-link-name m2-proper-name] _] m2
        m2-link (get-in ikb [:l m2-link-name])
        gmp1 (gmp m1 ikb)
        gmp2 (gmp m2 ikb)]
    (dosync
     (alter (get-in ikb [:m :p]) conj [m2-link-name m2-proper-name])
     (alter m2-link merge
            (translate @m2-link (vec-diff gmp1 gmp2))
            {:tdof {:# 0, :p gmp1}} ) )))

(defmethod coincident->transform!
  {:tdof 3 :rdof 3}
  [ikb m1 m2]
  (transform!->3-3-coincident ikb m1 m2))
