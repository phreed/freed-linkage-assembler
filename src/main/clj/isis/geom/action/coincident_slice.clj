(ns isis.geom.action.coincident-slice
  "The table of rules."
  (:require [isis.geom.action-dispatch :as master]
            [isis.geom.model.invariant
             :refer [marker->invariant?
                     marker->add-invariant!]]
            [isis.geom.machine [functions
                                :refer [translate
                                        vec-diff
                                        gmp]]]))


(defn- coincident->precondition?
  "Associated with each constraint type is a function which
  checks the preconditions and returns the marker which
  is underconstrained followed by the marker that is constrained."
  [m1 m2 invs]
  (cond (marker->invariant? invs m2 :p) [m1 m2]
        (marker->invariant? invs m1 :p) [m2 m1]
        :else false))

(defn- coincident->postcondition!
  "Associated with each constraint type is a function which
  checks/sets the postconditions for after the constraint has been satisfied."
  [_ m2 inv] (marker->add-invariant! inv m2 :p))


(defn coincident->transform-dispatch
  "Examine the underconstrained marker to determine the dispatch key.
  The key is the [#tdof #rdof]."
  [m1 m2 ikb]
  (let [links (:l ikb)
        [[link-name _] _] m1
        link @(link-name links)]
    {:tdof (:# (:tdof link)) :rdof (:# (:rdof link))}))

(defmulti coincident->transform!
  "Transform the links and ikb so that the constraint is met."
  #'coincident->transform-dispatch
  :default nil)



(defmethod master/precondition?->transform!
  :coincident
  [constraint ikb]
  (let [{m1 :m1 m2 :m2} constraint]
    (when-let [ [m1 m2] (coincident->precondition? m1 m2 ikb) ]
      (coincident->transform! m1 m2 ikb)))
  true)



(defn transform!->3-3-coincident
  "
  PFT entry: (3,3,coincident)

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
  2-RDOF(?geom)

  Explanation:
  Geom ?geom is free to translate, so the translation
  vector is measured and the ?geom is moved.
  No checks are required.
  "
  [m1 m2 ikb]
  (let [lkb (:l ikb)
        [[lm1-name _] _] m1
        lm1 (lm1-name lkb)]
    (dosync
     (alter lm1 assoc
            :p (translate @lm1 (vec-diff (gmp m1 ikb) (gmp m2 ikb)))
            :tdof {:# 0, :p (gmp m2 ikb)}
            :rdof {:# 2}) )))

(defmethod coincident->transform!
  {:tdof 3 :rdof 3}
  [m1 m2 ikb]
  (transform!->3-3-coincident m1 m2 ikb))
