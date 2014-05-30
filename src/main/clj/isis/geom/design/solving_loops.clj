(ns isis.geom.design.solving-loops
  "Solving Loops : E.4
  Algorithm 7 (Solve Loop) is implemented."
  (:require (isis.geom.machine functions)
            (isis.geom.analysis action-anlysis)
            (isis.geom.design graph-state) ) )

(defn solve-3-loop
  "Returns 'true' if loop is solved
  - i.e. all loops have status [0 0]E.
  Returns nil otherwise."
  [?loop]
  (setq ?loop (set-kinematic-inversion-loop))
  (setup-graph ?loop)
  (action-analysis)
  (loop  [loop-status [(geom-status 'l13) (geom-status 'l23)]]
    (locus-analysis)
    (action-analysis)
    (cond (and (= (geom-status 'l13) [0 0])
                (= (geom-status 'l23) [0 0]))
           ;; all geom configurations have been found, so this is finshed.
           (return-from solve-3-loop true)
          (= loop-status [(geom-status 'l13) (geom-status 'l23)])
           (return-from solve-3-loop nil)
          :else (recur loop-status))))


(def *failures*
  "Accumulator for failed loop solutions."
  (ref nil))

(def *successes*
  "Accumulator for successful loop solutions."
  (ref nil))

(def *degenerate*
  "Accumulator for degenerate loop solutions."
  (ref nil))

(def *passive-tdof*
  "Accumulator for loop solutions with passive TDOFs."
  (ref nil))

(def *passive-rdof*
  "Accumulator for loop solutions with passive RDOFs."
  (ref nil))

(defn reset-accumulators
  "Accumulator for loop solutions with passive TDOFs."
  []
  (swap! @*failures* nil)
  (swap! @*successes* nil)
  (swap! @*degenerate* nil)
  (swap! @*passive-tdof* nil)
  (swap! @*passive-rdof* nil) )

(defn classify-failure
  "The first three conditionals deal with degeneracies
  where the 'wrong' degrees of freedom are available to
  solve the remaining constraints.
  In such cases, there will always be an excessive
  number of translational degrees of freedom."
  [?loop ?solved]
  (cond (and (joint-has-constraint-type? 'j3 :in-line)
             (> (+ (tdof (geom-status 'l13))
                   (tdof (geom-status 'l23))) 2))
        (do (alter @*degenerate* conj ?loop) (inc ?solved))

        (and (joint-has-constraint-type? 'j3 :in-plane)
             (> (+ (tdof (geom-status 'l13))
                   (tdof (geom-status 'l23))) 1))
        (do (alter @*degenerate* conj ?loop) (inc ?solved))

        (and (joint-has-constraint-type? 'j3 :coincident)
             (> (+ (tdof (geom-status 'l13))
                   (tdof (geom-status 'l23))) 3))
        (do (alter @*degenerate* conj ?loop) (inc ?solved))

        ;; these cases cover passive degrees of freedom
        (and (nil? (joint-constraints 'j1))
             (nil? (joint-constraints 'j2))
             (nil? (joint-constraints 'j3))
             (zero? (tdof (geom-status 'l13)))
             (zero? (tdof (geom-status 'l23))))
        (do (alter @*passive-tdof* conj ?loop) (inc ?solved))

        (and (nil? (joint-constraints 'j1))
             (nil? (joint-constraints 'j2))
             (nil? (joint-constraints 'j3))
             (zero? (rdof (geom-status 'l13)))
             (zero? (rdof (geom-status 'l23))))
        (dosync (alter @*passive-rdof* conj ?loop) (inc ?solved))

        :else
        (do (alter @*failures* conj ?loop) ?solved) ) )

(defn loop-test
  "Procedure to test all three-geom-loops to see
  if they can be solved by degrees of analysis."
  []
  (reset-accumulators)
  (let [tried 0
        solved 0]
    (doseq [triloop (all-3loops)]
      (inc tried)
      (if (solve-3-loop triloop)
        (dosync (inc solved)
                (alter @*successes* conj triloop)
                (setq solved (classify-failure triloop solved)))))
    (format true "~2&Solved ~D out of ~D" solved tried)
    true))

(def *kinematic-inversion-map*
  "The inversion map."
  (ref {}))

(defn setup-kinematic-inversion-map
  "Checks failures, and finds a kinematic inversion for
  that failure that was solved.
  It then addes the pair of failed and successful inversion
  to the kinematic inversion map, so that the kinematic
  inversion substitution can be performed on subsequent
  attempts at solving that loop."
  []
  (dosync (swap! @*kinematic-inversion-map* {}))
  (doseq [failure *failures*]
    (let [[a b c] (first failure)
          v1 [[b c a] (rest failure)]
          v2 [[c a b] (rest failure)]
          v3 [[c b a] (rest failure)]
          v4 [[b a c] (rest failure)]
          v5 [[a c b] (rest failure)]]
      (cond (find @*successes* v1)
            (dosync (alter @*kinematic-inversion-map* conj [failure v1]))

            (find @*successes* v2)
            (dosync (alter @*kinematic-inversion-map* conj [failure v2]))

            (find @*successes* v3)
            (dosync (alter @*kinematic-inversion-map* conj [failure v3]))

            (find @*successes* v4)
            (dosync (alter @*kinematic-inversion-map* conj [failure v4]))

            (find @*successes* v5)
            (dosync (alter @*kinematic-inversion-map* conj [failure v5])) ))))

