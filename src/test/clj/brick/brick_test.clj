(ns brick.brick-test
  "Sample assembly for brick and ground."
  (:require [expectations :refer :all]
            [isis.geom.machine.misc :as misc]
            [isis.geom.model
             [joint :refer [joint-primitive-map]]
             [graph :refer [make->link make->marker
                            port->expand
                            port-pair->make-constraint
                            graph->init-invariants
                            joint-pair->constraint
                            joints->constraints]]
             [invariant :refer [init-marker-invariant-s
                                init-link-invariant-s
                                init-link-invariant
                                marker->add-invariant!
                                make->invariant]]]
            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-slice]
             [helical-slice]
             [in-line-slice]
             [in-plane-slice]
             [offset-x-slice]
             [offset-z-slice]
             [parallel-z-slice]]))


(defn ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) [:ref @%] %) form))



(def brick-graph
  "From section 3.4 : Example 1: the brick
  This object is composed of the prime data for an assembly.
  The derived data should not be stored in this object directly,
  but be stored in other objects with simliar keys."
  (ref
   '{ :links
      {ground {
               :markers {g1 {:e1 2.0 :e2 0.0 :e3 0.0}
                         g2 {:e1 5.0 :e2 0.0 :e3 0.0}
                         g3 {:e1 2.0 :e2 4.0 :e3 0.0} }
               :ports {j1 {:type :spherical :marker g1}
                       j2 {:type :spherical :marker g2}
                       j3 {:type :spherical :marker g3}} }
       brick {
              :markers {b1 {:e1 0.0 :e2 0.0 :e3 0.0}
                        b2 {:e1 0.0 :e2 3.0 :e3 0.0}
                        b3 {:e1 0.0 :e2 0.0 :e3 4.0}
                        b4 {:e1 1.0 :e2 0.0 :e3 0.0} }
              :ports {jg1 {:type :spherical :marker b1}
                      jg2 {:type :spherical :marker b2}
                      jg3 {:type :spherical :marker b3}

                      jc1 {:type :spherical :marker b4}
                      jc2 {:type :spherical :marker b2}
                      jc3 {:type :spherical :marker b3}} }
       cap {
            :markers {c2 {:e1 0.0 :e2 3.0 :e3 0.0}
                      c3 {:e1 0.0 :e2 0.0 :e3 4.0}
                      c4 {:e1 -1.0 :e2 0.0 :e3 0.0} }
            :ports {j1 {:type :spherical :marker c4}
                    j2 {:type :spherical :marker c2}
                    j3 {:type :spherical :marker c3}} } }
      :base ground
      :joints
      #{#{[ground j1] [brick jg1]}
        #{[ground j2] [brick jg2]}
        #{[ground j3] [brick jg3]}
       ;; #{[brick jc1] [cap j1]}
        #{[brick jc2] [cap j2]}
        #{[cap j3] [brick jc3]}
        } }))

(def brick-graph-goal
  "This shows the ultimate goal for the position-analysis.
  Notice that the ground has no properties, this idicates the
  use of the default values, it also indicates the 'base' object.
  In this example there is no rotation so the {:z3 :z1 :z2} values
  could be anything."
  '{:link-motors
    {ground {}
     brick {:e [8.0 -5.0 -1.0] :z []}}})


(declare graph->position-analysis)

(defn make-graph-watcher
  "This function is suitable to use as a watch function.
  When a graph changes it will compute the placement of
  each link in its assembly.  The augmented graph will
  be produced by this function."
  []
  (defn graph->assemble
    [key reference old-state new-state]
    (joints->constraints new-state))

  (add-watch brick-graph :assembly-key graph->assemble) )


(expect
 '[[ground g1 :coincident {:e1 2.0, :e2 0.0, :e3 0.0}]]
 (port->expand @brick-graph '[ground j1]))

(expect
 '{:type :coincident,
   :m1 [[ground g2] {:e [ 1.0 0.0 0.0] :z [nil nil nil] :x [nil nil nil]}],
   :m2 [[brick b2] {:e [101.0 0.0 0.0] :z [nil nil nil] :x [nil nil nil]}]}
 (port-pair->make-constraint
  @brick-graph
  '[[ground g2 :coincident {:e1 1.0, :e2 0.0, :e3 0.0}]
    [brick b2 :coincident {:e1 101.0, :e2 0.0, :e3 0.0}]] ))



(expect
 '{:mark {:loc [:ref #{[ground g1] [ground g3] [ground g2]}],
          :z [:ref #{[ground g1] [ground g3] [ground g2]}],
          :x [:ref #{[ground g1] [ground g3] [ground g2]}]},
   :link {brick [:ref {:tdof {:# 3}, :rdof {:# 3},
                       :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}]
          ground [:ref {:tdof {:# 0}, :rdof {:# 0},
                        :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}]
          cap [:ref {:tdof {:# 3}, :rdof {:# 3},
                     :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]}}]}}
 (ref->str (graph->init-invariants @brick-graph)))

(expect
 '({:type :coincident
    :m1 [[ground g1] {:e [2.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
    :m2 [[brick b1] {:e [0.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]})
 (joint-pair->constraint @brick-graph '#{[ground j1] [brick jg1]}))


(expect
 '(
   {:type :coincident
    :m1 [[ground g2] {:e [5.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
    :m2 [[brick b2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
   {:type :coincident
    :m1 [[brick b4] {:e [1.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
    :m2 [[cap c4] {:e [-1.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
   {:type :coincident
    :m1 [[ground g1] {:e [2.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
    :m2 [[brick b1] {:e [0.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
   {:type :coincident
    :m1 [[brick b3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]
    :m2 [[cap c3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]}
   {:type :coincident
    :m1 [[brick b3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]
    :m2 [[ground g3] {:e [2.0 4.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
   {:type :coincident
    :m1 [[cap c2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
    :m2 [[brick b2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]})
 (joints->constraints @brick-graph))


;; simulate the first point being repositioned, by 3-3-coincident
(let [kb (graph->init-invariants @brick-graph)

      input
      '{:type :coincident
        :m1 [[ground g1] {}]
        :m2 [[brick b1] {:e [-100.0 50.0 10.0]}]}

      pattern
      '{:mark {:loc [:ref #{[ground g1] [ground g3] [ground g2] [brick b1]}]
               :z [:ref #{[ground g1] [ground g3] [ground g2]}]
               :x [:ref #{[ground g1] [ground g3] [ground g2]}]}
        :link {ground [:ref {:tdof {:# 0} :rdof {:# 0}
                             :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}]
               brick [:ref {:tdof {:# 0 :point [0.0 0.0 0.0]}, :rdof {:# 3}
                            :versor {:xlate [100.0 -50.0 -10.0] :rotate [1.0 0.0 0.0 0.0]}}]
               cap [:ref {:tdof {:# 3} :rdof {:# 3}
                          :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]}}]}} ]
  (expect pattern (do (constraint-attempt? kb input) (ref->str kb))))

(let [kb (graph->init-invariants @brick-graph)

      input
      '{:type :coincident
        :m1 [[ground g3] {:e [5.0 4.0 0.0]}]
        :m2 [[brick b3] {:e [0.0 0.0 4.0]}]}

      mark-pattern
      '{:loc [:ref #{[ground g1] [ground g3] [ground g2] [brick b3]}]
        :z [:ref #{[ground g1] [ground g3] [ground g2]}]
        :x [:ref #{[ground g1] [ground g3] [ground g2]}]}
      link-pattern
      '{ground [:ref {:tdof {:# 0}, :rdof {:# 0}
                      :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}]
        brick [:ref {:tdof {:# 0, :point [5.0 4.0 0.0]}, :rdof {:# 3}
                     :versor {:xlate [5.0 4.0 -4.0] :rotate [1.0 0.0 0.0 0.0]}}]
        cap [:ref {:tdof {:# 3}, :rdof {:# 3},
                   :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}] }]
  (let [_ (constraint-attempt? kb input)
        {result-mark :mark result-link :link} (ref->str kb)]
    (expect mark-pattern result-mark)
    (= link-pattern result-link)) )


;; simulate the second point being repositioned,

(let [kb (graph->init-invariants @brick-graph)
      constraints (joints->constraints @brick-graph)

      first-input
      '{:type :coincident
        :m1 [[ground g1] {:e [2.0 0.0 0.0]}]
        :m2 [[brick b1] {:e [0.0 0.0 0.0]}]}

      first-mark-pattern
      '{:loc [:ref #{[ground g1] [ground g3] [ground g2] [brick b1]}]
        :z [:ref #{[ground g1] [ground g3] [ground g2]}]
        :x [:ref #{[ground g1] [ground g3] [ground g2]}]}
      first-link-pattern
      '{ground [:ref {:tdof {:# 0}, :rdof {:# 0}
                      :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}]
        brick [:ref {:tdof {:# 0, :point [2.0 0.0 0.0]}
                     :rdof {:# 3}
                     :versor {:xlate [2.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}]
        cap [:ref {:tdof {:# 3}, :rdof {:# 3},
                   :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}]}

      second-input
      '{:type :coincident
        :m1 [[ground g2] {:e [5.0 0.0 0.0]}]
        :m2 [[brick b2] {:e [0.0 3.0 0.0]}]}

      second-mark-pattern
      '{:loc [:ref #{[ground g1] [ground g3] [ground g2] [brick b1] [brick b2]}]
        :z [:ref #{[ground g1] [ground g3] [ground g2] [brick b2]}]
        :x [:ref #{[ground g1] [ground g3] [ground g2]}]}

      second-link-pattern
      (assoc-in first-link-pattern ['brick]
                [:ref {:tdof {:# 0 :point [2.0 0.0 0.0]}
                       :rdof {:# 1 :axis [1.0 0.0 0.0]}
                       :versor {:xlate [2.0 0.0 0.0]
                                :rotate [0.7071067811865476 0.0 0.0 -0.7071067811865475]}}])

      third-input
      '{:type :coincident
        :m1 [[ground g3] {:e [2.0 4.0 0.0]}]
        :m2 [[brick b3] {:e [0.0 0.0 4.0]}]}

      third-mark-pattern
      '{:loc [:ref #{[ground g1] [ground g3] [ground g2] [brick b1] [brick b2] [brick b3]}]
        :z [:ref #{[ground g1] [ground g3] [ground g2] [brick b2] [brick b3]}]
        :x [:ref #{[ground g1] [ground g3] [ground g2] [brick b3]}]}

      third-link-pattern
      (assoc-in second-link-pattern ['brick]
                [:ref {:tdof {:# 0 :point [2.0 0.0 0.0]}
                       :rdof {:# 0}
                       :versor {:xlate [2.0 0.0 0.0]
                                :rotate [0.5000000000000001
                                   -0.5 -0.4999999999999999 -0.5]}}])
      ]

  ;; first by 3-3-coincident
  (let [_ (constraint-attempt? kb first-input)
        {result-mark :mark result-link :link} (ref->str kb)]
    (expect first-mark-pattern result-mark)
    (expect first-link-pattern result-link))
  ;; then by 0-3-coincident
  (let [_ (constraint-attempt? kb second-input)
        {result-mark :mark result-link :link} (ref->str kb)]
    (expect second-mark-pattern result-mark)
    (expect second-link-pattern result-link))
  ;; finally by 0-1-coincident
  (let [_ (constraint-attempt? kb third-input)
          {result-mark :mark result-link :link} (ref->str kb)]
    (expect third-mark-pattern result-mark)
    (expect third-link-pattern result-link)) )

(defn position-analysis
  "Algorithm for using the plan fragment table to perform position analysis.
  We update a link map and a marker map with invariants.
  The link map of invariants indicates just how well placed the link is.
  The marker map does a similar thing.
  - We will repeatedly evaluate all constraints, making marker properties
  invariant and producing link versors, until no more constraints can be satisfied.
  The link versors are then returned along with the constraint plan.

  constraints : the list of constraints needing to be satisfied.
  kb : invariant properties of markers and links.

  progress? : is the current round making progress?
  xs : active constraints which are being tried.
  plan : constraints which have been successfully applied.
  ys : constraints which have been tried and failed. "

  [kb constraints]
  (loop [progress? false
         [x & xs] constraints
         plan [] ys[]]
    (if x
      ;; working through the active constraint list.
      (if (constraint-attempt? kb x)
        (recur true xs (conj plan x) ys)
        (recur progress? xs plan (conj ys x)))
      ;; active constraint list is exhausted.
      (if (empty? ys)
        ;; all the constraints have been satisfied
        [true kb plan []]
        ;; not all constraints have been satisfied
        (if progress?
          ;; still making progress, go again.
          (recur false ys plan [])
          ;; no progress is possible.
          [false kb plan ys])))))


(let [graph @brick-graph
      mark-pattern
      '{:loc [:ref #{[cap c2] [ground g1] [brick b2] [brick b1] [cap c3] [brick b3] [ground g3] [ground g2]}]
        :z [:ref #{[ground g1] [brick b1] [cap c3] [brick b3] [ground g3] [ground g2]}]
        :x [:ref #{[ground g1] [brick b3] [ground g3] [ground g2]} ]}

      link-pattern
      '{ground [:ref {:versor {:xlate [0.0 0.0 0.0]
                               :rotate [1.0 0.0 0.0 0.0]}
                      :tdof {:# 0} :rdof {:# 0}}]
        brick [:ref {:versor {:xlate [2.0 0.0 0.0]
                              :rotate [0.5000000000000001 -0.5 -0.4999999999999999 -0.5]}
                     :tdof {:# 0 :point [5.0 0.0 0.0]} :rdof {:# 0}}]
        cap
        #_[:ref {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]} :tdof {:# 3} :rdof {:# 3}}]
        [:ref {:versor {:xlate [5.0 -3.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
               :tdof {:# 0 :point [5.0 0.0 0.0]}, :rdof {:# 3}}]

        #_[:ref {:versor {:xlate [3.4895427658483675 -1.1870270270270273 1.22796000786116]
                            :rotate [0.7071067811865476 -0.37199244398022174 -0.2789943329851663 -0.20924574973887472]}
                   :tdof {:# 0, :point [5.0 0.0 0.0]}
                   :rdof {:# 1, :axis [-0.6489433709421998 0.3642913256601508 0.6679553363503764]}}]
        }


      success-pattern
      '[
        {:type :coincident
         :m1 [[ground g2] {:e [5.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
         :m2 [[brick b2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
        {:type :coincident
         :m1 [[ground g1] {:e [2.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
         :m2 [[brick b1] {:e [0.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
        {:type :coincident
         :m1 [[brick b3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]
         :m2 [[ground g3] {:e [2.0 4.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
        {:type :coincident
         :m1 [[cap c2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
         :m2 [[brick b2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
        {:type :coincident
         :m1 [[brick b3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]
         :m2 [[cap c3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]}]

      failure-pattern
      '[
        {:type :coincident
         :m1 [[brick b4] {:e [1.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
         :m2 [[cap c4] {:e [-1.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}]
      ]

  (let [constraints (joints->constraints graph)
        kb (graph->init-invariants graph)
        [success? result-kb result-success result-failure] (position-analysis kb constraints)
        {result-mark :mark result-link :link} (ref->str result-kb)]
    (expect mark-pattern result-mark)
    (expect link-pattern result-link)
    (expect success-pattern result-success)
    (expect failure-pattern result-failure)) )

