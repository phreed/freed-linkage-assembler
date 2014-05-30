(ns isis.geom.analysis.action-analysis
  "Action analysis : E.3.2"
  ( :require
    (isis.geom.analysis [analysis-utilities
                         :refer [find-geom-for-marker]])
    (isis.geom.design [graph-state
                       :refer [geom-status]])))


(defn action-analyze
  "Return 'true' if the constraint can be satisfied.
  Applies the constraint and alters geom status in the constraint problem graph."
  [?constraint]
  (let [marker (apply 'preconditions-satisfied? ?constraint)]
    (when marker
      (let [geom (find-geom-for-marker marker)
            old-status (geom-status geom)
            pfte (pft-entry (tdof old-status) (rdof old-status) (c-type ?constraint))
            new-status (and pfte (pft-entry-new-status pfte))]
        (when new-status
          (setf (geom-status geom) new-status)
          (apply 'assert-postconditions ?constraint)
          true)))))

(defn update-invariants
  "When a geom loses all rotational dofs, or has status (0,0),
  update the invariance information for every marker on that geom.
  Only l13 and l23 need be checked. as l12 is necessarily grounded."
  []
  (doseq (geom '(l13 l23))
    (when (zero? (rdof (geom-status geom)))
      (map (fn [?m] (add-invariant ?m :z) (add-invariant ?m :x))
              (geom-markers geom)))
    (when (equal? (geom-status geom) [0 0])
      (map (fn [?m] (add-invariant ?m :position))
              (geom-markers geom)))) )

(defn action-analysis
  "Repeatedly apply action-analyze for all joints."
  []
  (doseq (joint [j1 j2 j3])
    (prog () LOOP (doseq [c (copy-list (joint-constraints joint))]
                    (when (action-analyze c)
                      (remove-constraint joint c)
                      (go LOOP))))))
