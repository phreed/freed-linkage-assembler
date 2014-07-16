(ns isis.geom.action.coincident-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as master]
            [isis.geom.model.invariant
             :refer [marker->invariant?
                     set-marker-invariant!
                     set-link-invariant!]]
            [isis.geom.machine
             [geobj :refer [translate
                           vec-diff
                           gmp normalize]]]
            [isis.geom.action
             [auxiliary :refer [dof-1r:p->p
                                dof-3r:p->p]] ]))


(defn- coincident->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained followed by the marker that is constrained."
  [kb m1 m2]
  (cond (marker->invariant? kb m2 :loc) [m2 m1]
        (marker->invariant? kb m1 :loc) [m1 m2]
        :else false))



(defn coincident->transform-dispatch
  "Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof] of the m2 link."
  [kb m1 m2]
  (let [[[link-name _] _] m2
        link @(get (:link kb) link-name)]
    {:tdof (:# (:tdof link)) :rdof (:# (:rdof link))}))

(defmulti coincident->transform!
  "Transform the links and kb so that the constraint is met."
  #'coincident->transform-dispatch
  :default nil)



(defmethod master/constraint-attempt?
  :coincident
  [kb constraint]
  (let [{m1 :m1 m2 :m2} constraint
        result (coincident->precondition? kb m1 m2) ]
    (if (false? result)
      false
      (let [[ma1 ma2] result]
        (coincident->transform! kb ma1 ma2)
        true))))

;;;;==================================

(defn- transform!->0-1-coincident
  "PFT entry: (0,1,coincident)

Initial status:
  0-TDOF(?m2-link, ?m2-point)
  1-RDOF(?m2-link, ?m2-axis, ?m2-axis-1, ?m2-axis-2)

Plan fragment:
  begin
  1r/p-p(?m2-link, ?m2-point, gmp(?m2), gmp(?m1),
    ?m2-axis, ?m2-axis-1, ?m2-axis-2);
  end;

New status:
  0-TDOF(?m2-link, ?m2-point)  <unchanged>
  0-RDOF(?m2-link) <no need to forward information>

Explanation:
  Geom ?link has only one rotational degree of freedom.
  Therefore it must be rotated about its known point and known axis.
  The two markers must be equidistant from the line defined by
  ?point and ?axis, and must lie in a common plaine perpendicular
  to ?axis. "
  [kb m1 m2]
  (let [[[m2-link-name m2-proper-name] _] m2
        m2-link (get-in kb [:link m2-link-name])
        m2-point (get-in @m2-link [:tdof :point])
        m2-axis (get-in @m2-link [:rdof :axis])
        m2-axis-1 (get-in @m2-link [:rdof :axis-1])
        m2-axis-2 (get-in @m2-link [:rdof :axis-2])]
    (dosync
     (alter m2-link merge
            (dof-1r:p->p @m2-link m2-point
                         (gmp m2 kb) (gmp m1 kb)
                         m2-axis m2-axis-1 m2-axis-2))
     (set-link-invariant! kb m2-link-name)
     (alter m2-link assoc
            :rdof {:# 0} ) )))

(defmethod coincident->transform!
  {:tdof 0 :rdof 1}
  [kb m1 m2]
  (transform!->0-1-coincident kb m1 m2))



(defn- transform!->0-3-coincident
  "PFT entry: (0,3,coincident)

Initial status:
  0-TDOF(?m2-link, ?m2-point)
  3-RDOF(?m2-link)

Plan fragment:
  3r/p-p(?m2-link, ?m2-point, gmp(?m2), gmp(?m1));
  R[0] = vec-diff(gmp(?m2), ?m2-point);

New status:
  0-TDOF(?m2-link, ?m2-point) <unchanged>
  1-RDOF(?m2-link, R[0], nil, nil)

Explanation:
  Geom ?link cannot translate, so the coincident
  constraint is satisfied by a rotation.
  After the constraint is satisfied, ?link can still rotate
  about the line connecting ?m-2 and ?point.
  "
  [kb m1 m2]
  (let [[[m2-link-name m2-proper-name] _] m2
        m2-link (get-in kb [:link m2-link-name])
        m2-point (get-in @m2-link [:tdof :point])]
    (dosync
     (set-marker-invariant! kb m2-link-name m2-proper-name :loc)
     (set-marker-invariant! kb m2-link-name m2-proper-name :z)
     (alter m2-link merge
            (dof-3r:p->p @m2-link m2-point
                         (gmp m2 kb) (gmp m1 kb)))
     (alter m2-link assoc
            :rdof {:# 1
                   :axis (normalize (vec-diff (gmp m2 kb) m2-point))} ) )))

(defmethod coincident->transform!
  {:tdof 0 :rdof 3}
  [kb m1 m2]
  (transform!->0-3-coincident kb m1 m2))



(defn- transform!->3-3-coincident
  "PFT entry: (3,3,coincident)

  Initial status:
  3-TDOF(?m2-link)
  3-RDOF(?m2-link)

  Plan fragment:
  translate(?m2-link, vec-diff(gmp(?m1), gmp(?m2));

  New status:
  0-TDOF(?m2-link, gmp(?m2))
  3-RDOF(?m2-link)  <no change>

  Explanation:
  Link ?m2-link is free to translate, so the translation
  vector is measured and the ?m2-link is moved.
  No checks are required.
  "
  [kb m1 m2]
  (let [[[m2-link-name m2-proper-name] _] m2
        m2-link (get-in kb [:link m2-link-name])]
    (dosync
     (set-marker-invariant! kb m2-link-name m2-proper-name :loc)
     (alter m2-link merge
            (translate @m2-link
                       (vec-diff (gmp m1 kb) (gmp m2 kb))))
     (alter m2-link assoc
            :tdof {:# 0
                   :point (gmp m2 kb)} ) )))

(defmethod coincident->transform!
  {:tdof 3 :rdof 3}
  [kb m1 m2]
  (transform!->3-3-coincident kb m1 m2))
