(ns isis.geom.action.in-plane-slice-fixed
  "The table of rules for the in-plane constraint where
  the point marker, ?M_1, is FIXED and the plane marker, ?M_2, is mobile."
  (:require [isis.geom.machine [geobj :as geo]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))

(def slicer *ns*)

(defn transform!->t0-r0 [kb m1 m2] (println slicer) )
(defn transform!->t0-r1 [kb m1 m2] (println slicer) )
(defn transform!->t0-r2 [kb m1 m2] (println slicer) )
(defn transform!->t0-r3 [kb m1 m2] (println slicer)  )

(defn transform!->t1-r0 [kb m1 m2] (println slicer) )
(defn transform!->t1-r1 [kb m1 m2]  (println slicer) )
(defn transform!->t1-r2 [kb m1 m2]  (println slicer) )
(defn transform!->t1-r3 [kb m1 m2]  (println slicer) )

(defn transform!->t2-r0 [kb m1 m2]  (println slicer) )
(defn transform!->t2-r1 [kb m1 m2]  (println slicer) )
(defn transform!->t2-r2 [kb m1 m2]  (println slicer) )
(defn transform!->t2-r3 [kb m1 m2]  (println slicer) )

(defn transform!->t3-r0 [kb m1 m2]  (println slicer) )
(defn transform!->t3-r1 [kb m1 m2]  (println slicer) )
(defn transform!->t3-r2 [kb m1 m2]  (println slicer) )

(defn transform!->t3-r3
  "PFT entry: (3,3,in-plane)

Initial status:
  0-TDOF(?m1-link)
  0-RDOF(?m1-link)

  3-TDOF(?m2-link)
  3-RDOF(?m2-link)

Plan fragment:
  begin
  R[0] = plane(gmp(?M_2), gmz(?M_2));
  R[1] = perp-dist(gmp(?M_1), R[0]);
  R[2] = vec-diff(gmp(?M_1), R[1]);
  translate(?link, R[2]);
  R[3] = gmp(?M_1);
  end;

New status:
  2-TDOF(?m2-link, R[3], R[0], R[0])
  3-RDOF(?m2-link)

Explanation:
  Geom ?m2-link is free to translate, so the translation
  vector is measured and the ?m2-link is moved.
  No checks are required.
  "
  [kb m1 m2]
  (let [[[m2-link-name m2-proper-name] _] m2
        m2-link (get-in kb [:link m2-link-name])
        m1-gmp (geo/gmp m1 kb)
        m2-gmp (geo/gmp m2 kb)
        m2-gmz (geo/gmz m2 kb)
        m2-plane (geo/plane m2-gmp m2-gmz)
        separation (geo/norm (geo/rejection m1-gmp m2-plane)) ]
    (dosync
     (invariant/set-marker! kb m2-link-name m2-proper-name :loc)
     (alter m2-link merge
            (geo/translate @m2-link separation))
     (alter m2-link assoc
            :tdof {:# 2
                   :point m1-gmp
                   :plane (geo/gmp m2 kb)}
            :rdof {:# 3 } ) )))

