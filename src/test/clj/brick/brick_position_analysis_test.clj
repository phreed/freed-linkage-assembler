(ns brick.brick-position-analysis-test
  "Sample assembly for brick and ground."
  (:require [expectations :refer :all]
            [isis.geom.model
             [graph :refer [ graph->init-invariants
                             joints->constraints]]]
            [isis.geom.analysis
             [position-analysis :refer [position-analysis]]]

            [isis.geom.machine.misc :as misc]
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
        #{[brick jc1] [cap j1]}
        #{[brick jc2] [cap j2]}
        #{[cap j3] [brick jc3]}
        } }))



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


(let [graph @brick-graph
      mark-pattern
      '{:loc [:ref #{[cap] [ground] [brick] }]
        :z [:ref #{[ground] [brick] [cap]}]
        :x [:ref #{[ground] [brick] [cap]} ]}

      link-pattern
      '{ground [:ref {:versor {:xlate [0.0 0.0 0.0]
                               :rotate [1.0 0.0 0.0 0.0]}
                      :tdof {:# 0} :rdof {:# 0}}]
        brick [:ref {:versor {:xlate [2.0 0.0 0.0]
                              :rotate [0.5000000000000001
                                       -0.5
                                       -0.4999999999999999
                                       -0.5]}
                     :tdof {:# 0 :point [5.0 0.0 0.0]}
                     :rdof {:# 0}}]
        cap [:ref {:versor {:xlate [1.5615384615384618
                                    0.34615384615384615
                                    1.3846153846153855]
                            :rotate [0.5
                                     -0.7307692307692308
                                     -0.4230769230769231
                                     -0.1923076923076923]}
                   :tdof {:# 0 :point [5.0 0.0 0.0]}
                   :rdof {:# 0}}]}


      success-pattern
      '[
        {:type :coincident
         :m1 [[ground g2] {:e [5.0 0.0 0.0] :z [nil nil nil ] :x [nil nil nil]}]
         :m2 [[brick b2] {:e [0.0 3.0 0.0] :z [nil nil nil] :x [nil nil nil]} ]}
        {:type :coincident
         :m1 [[ground g1] {:e [2.0 0.0 0.0] :z [nil nil nil] :x [nil nil nil] }]
         :m2 [[brick b1] {:e [0.0 0.0 0.0] :z [nil nil nil] :x [nil nil nil]}]}
        {:type :coincident
         :m1 [[brick b3] {:e [0.0 0.0 4.0] :z [nil nil nil] :x [nil nil nil]}]
         :m2 [[ground g3] {:e [2.0 4.0 0.0] :z [nil nil nil] :x [nil nil nil]}]}
        {:type :coincident
         :m1 [[cap c2] { :e [0.0 3.0 0.0] :z [nil nil nil] :x [nil nil nil]}]
         :m2 [[brick b2] {:e [0.0 3.0 0.0] :z [nil nil nil] :x [nil nil nil]}]}
        {:type :coincident
         :m1 [[brick b4] {:e [1.0 0.0 0.0] :z [nil nil nil] :x [nil nil nil]}]
         :m2 [[cap c4] {:e [-1.0 0.0 0.0] :z [nil nil nil] :x [nil nil nil]}]}
        {:type :coincident
         :m1 [[brick b3] {:e [0.0 0.0 4.0] :z [nil nil nil] :x [nil nil nil]}]
         :m2 [[cap c3] {:e [0.0 0.0 4.0] :z [nil nil nil] :x [nil nil nil]}]} ]

      failure-pattern []
      ]


  (let [constraints (joints->constraints graph)
        kb (graph->init-invariants graph)
        [success? result-kb result-success result-failure] (position-analysis kb constraints)
        {result-mark :mark result-link :link} (ref->str result-kb)]
    (expect mark-pattern result-mark)
    (expect link-pattern result-link)
    (expect success-pattern result-success)
    (expect failure-pattern result-failure)) )

