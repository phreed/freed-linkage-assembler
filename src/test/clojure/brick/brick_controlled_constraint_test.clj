(ns brick.brick-controlled-constraint-test
  "Sample assembly for brick and ground."
  (:require [midje.sweet :refer [facts fact]]

            [isis.geom.machine.misc :as misc]
            [isis.geom.model
             [graph :refer [ graph->init-invariants
                             joint-pair->constraint
                             joints->constraints]]]
            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-slice]
             [helical-slice]
             [in-line-slice]
             [in-plane-slice]
             [offset-x-slice]
             [offset-z-slice]
             [parallel-z-slice]] ))


(defn unref
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) @% %) form))



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



;; simulate the first point being repositioned, by 3-3-coincident
(let [kb (graph->init-invariants @brick-graph)

      input
      '{:type :coincident
        :m1 [[ground g1] {}]
        :m2 [[brick b1] {:e [-100.0 50.0 10.0]}]}

      pattern
      '{:mark {:loc #{[ground] [brick b1]}
               :z #{[ground]}
               :x #{[ground]}}
        :link {ground {:tdof {:# 0} :rdof {:# 0}
                       :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}
               brick {:tdof {:# 0 :point [0.0 0.0 0.0]}, :rdof {:# 3}
                      :versor {:xlate [100.0 -50.0 -10.0] :rotate [1.0 0.0 0.0 0.0]}}
               cap {:tdof {:# 3} :rdof {:# 3}
                    :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]}}}} ]
  (facts "simulate a brick being repositioned"
         (fact "by 0-3-coincident"
               (do (constraint-attempt? kb input) (unref kb)) => pattern)))

(let [kb (graph->init-invariants @brick-graph)

      input
      '{:type :coincident
        :m1 [[ground g3] {:e [5.0 4.0 0.0]}]
        :m2 [[brick b3] {:e [0.0 0.0 4.0]}]}

      mark-pattern
      '{:loc #{[ground] [brick b3]}
        :z #{[ground]}
        :x #{[ground]}}
      link-pattern
      '{ground {:tdof {:# 0}, :rdof {:# 0}
                :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}
        brick {:tdof {:# 0, :point [5.0 4.0 0.0]}, :rdof {:# 3}
               :versor {:xlate [5.0 4.0 -4.0] :rotate [1.0 0.0 0.0 0.0]}}
        cap {:tdof {:# 3}, :rdof {:# 3},
             :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}} }]
  (let [_ (constraint-attempt? kb input)
        {result-mark :mark result-link :link} (unref kb)]
    (facts "simulate the brick being repositioned"
           (fact "about marker invariant by 0-3-coincident"
                 result-mark => mark-pattern)
           (fact "about link placement by 0-3-coincident"
                 result-link => link-pattern))))



(let [kb (graph->init-invariants @brick-graph)
      constraints (joints->constraints @brick-graph)

      first-input
      '{:type :coincident
        :m1 [[ground g1] {:e [2.0 0.0 0.0]}]
        :m2 [[brick b1] {:e [0.0 0.0 0.0]}]}

      first-mark-pattern
      '{:loc #{[ground] [brick b1]}
        :z #{[ground]}
        :x #{[ground]}}
      first-link-pattern
      '{ground {:tdof {:# 0}, :rdof {:# 0}
                :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}
        brick {:tdof {:# 0, :point [2.0 0.0 0.0]}
               :rdof {:# 3}
               :versor {:xlate [2.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}
        cap {:tdof {:# 3}, :rdof {:# 3},
             :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}}}

      second-input
      '{:type :coincident
        :m1 [[ground g2] {:e [5.0 0.0 0.0]}]
        :m2 [[brick b2] {:e [0.0 3.0 0.0]}]}

      second-mark-pattern
      '{:loc #{[ground] [brick b1] [brick b2]}
        :z #{[ground] [brick b2]}
        :x #{[ground]}}

      second-link-pattern
      (assoc-in first-link-pattern ['brick]
                {:tdof {:# 0 :point [2.0 0.0 0.0]}
                 :rdof {:# 1 :axis [1.0 0.0 0.0]}
                 :versor {:xlate [2.0 0.0 0.0]
                          :rotate [0.7071067811865476 0.0 0.0 -0.7071067811865475]}})

      third-input
      '{:type :coincident
        :m1 [[ground g3] {:e [2.0 4.0 0.0]}]
        :m2 [[brick b3] {:e [0.0 0.0 4.0]}]}

      third-mark-pattern
      '{:loc #{[ground] [brick]}
        :z #{[ground] [brick]}
        :x #{[ground] [brick]}}

      third-link-pattern
      (assoc-in second-link-pattern ['brick]
                {:tdof {:# 0 :point [2.0 0.0 0.0]}
                 :rdof {:# 0}
                 :versor {:xlate [2.0 0.0 0.0]
                          :rotate [0.5000000000000001
                                   -0.5 -0.4999999999999999 -0.5]}})
      ]

  (let [_ (constraint-attempt? kb first-input)
        {result-mark :mark result-link :link} (unref kb)]
    (facts "simulate the second point being repositioned, by 3-3-coincident"
           (fact "about marker invariant by 0-3-coincident"
                 result-mark => first-mark-pattern)
           (fact "about link placement by 0-3-coincident"
                 result-link => first-link-pattern)))
  (let [_ (constraint-attempt? kb second-input)
        {result-mark :mark result-link :link} (unref kb)]
    (facts "simulate the second point being repositioned, by 0-3-coincident"
           (fact "about marker invariant by 0-3-coincident"
                 result-mark => second-mark-pattern)
           (fact "about link placement by 0-3-coincident"
                 result-link => second-link-pattern)))
  (let [_ (constraint-attempt? kb third-input)
        {result-mark :mark result-link :link} (unref kb)]
    (facts "simulate the second point being repositioned, by 0-1-coincident"
           (fact "about marker invariant by 0-3-coincident"
                 result-mark => third-mark-pattern)
           (fact "about link placement by 0-3-coincident"
                 result-link => third-link-pattern))))

