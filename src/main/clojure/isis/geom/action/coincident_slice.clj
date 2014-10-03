(ns isis.geom.action.coincident-slice
  "The table of rules."
  (:require [isis.geom.position-dispatch :as ms]
            [isis.geom.machine
             [geobj :as ga]
             [tolerance :as tol]]
            [isis.geom.action [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]] ))


(def slicer "coincident-slice")

(defn assemble!->t0-r0
"PFT entry: (0,0,coincident)

Initial status:
  0-TDOF(?m1-link, ?m1-point)
  0-RDOF(?m1-link)

  0-TDOF(?m2-link, ?m2-point)
  0-RDOF(?m2-link)

Plan fragment:
  begin
  R[0] = vec-diff(gmp(?M_1), gmp(?M_2));
  unless zero?(R[0])
    error(R[0], estring[9]);
  end;

New status: <unchanged>

Explanation:
  Geom links are fixed, so the coincident constraint
  can only be checked for consistency. "
  [kb m1 m2]
  (if (tol/near-zero? (ga/vec-diff (ga/gmp m2 kb) (ga/gmp m1 kb)) :tiny)
    :consistent
    (do
      (println "inconsistently overconstrained" m1 m2)
      :inconsistent ) ))



(defn assemble!->t0-r1
  "PFT entry: (0,1,coincident)

Initial status:
  0-TDOF(?m1-link, ?m1-point)
  0-RDOF(?m1-link, ?m1-axis)

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
            (dof/r1:p->p @m2-link m2-point
                         (ga/gmp m2 kb) (ga/gmp m1 kb)
                         m2-axis m2-axis-1 m2-axis-2))
     (invariant/set-link! kb m2-link-name)
     (alter m2-link assoc
            :rdof {:# 0} ) ))
  :progress-made)

(defn assemble!->t0-r2 [kb m1 m2] (ms/unimpl :t0-r2 slicer kb m1 m2))

(defn assemble!->t0-r3
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
     (invariant/set-marker! kb [m2-link-name m2-proper-name] :loc)
     (invariant/set-marker! kb [m2-link-name m2-proper-name] :dir)
     (alter m2-link merge
            (dof/r3:p->p @m2-link m2-point
                         (ga/gmp m2 kb) (ga/gmp m1 kb)))
     (alter m2-link assoc
            :rdof {:# 1
                   :axis (ga/normalize (ga/vec-diff (ga/gmp m2 kb) m2-point))} ) ))
  :progress-made)

(defn assemble!->t1-r0 [kb m1 m2]  (ms/unimpl :t1-r0 slicer kb m1 m2))
(defn assemble!->t1-r1 [kb m1 m2]  (ms/unimpl :t1-r1 slicer kb m1 m2))
(defn assemble!->t1-r2 [kb m1 m2]  (ms/unreal :t1-r2 slicer kb m1 m2))
(defn assemble!->t1-r3 [kb m1 m2]  (ms/unimpl :t1-r3 slicer kb m1 m2))

(defn assemble!->t2-r0 [kb m1 m2]  (ms/unimpl :t2-r0 slicer kb m1 m2))
(defn assemble!->t2-r1 [kb m1 m2]  (ms/unimpl :t2-r1 slicer kb m1 m2))
(defn assemble!->t2-r2 [kb m1 m2]  (ms/unreal :t2-r2 slicer kb m1 m2))
(defn assemble!->t2-r3 [kb m1 m2]  (ms/unimpl :t2-r3 slicer kb m1 m2))

(defn assemble!->t3-r0 [kb m1 m2]  (ms/unimpl :t3-r0 slicer kb m1 m2))
(defn assemble!->t3-r1 [kb m1 m2]  (ms/unimpl :t3-r2 slicer kb m1 m2))
(defn assemble!->t3-r2 [kb m1 m2]  (ms/unreal :t3-r2 slicer kb m1 m2))


(defn assemble!->t3-r3
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
     (alter m2-link merge
            (ga/translate @m2-link ga/vec-sum
                       (ga/vec-diff (ga/gmp m1 kb) (ga/gmp m2 kb))))

     (invariant/set-marker! kb [m2-link-name m2-proper-name] :loc)

     (alter m2-link assoc
            :tdof {:# 0
                   :point (ga/gmp m2 kb)} ) ))
  :progress-made)

