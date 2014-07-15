(ns brick.brick-controlled-constraint-test
  "Sample assembly for brick and ground."
  (:require [expectations :refer :all]
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



;; simulate the first point being repositioned, by 3-3-coincident
(let [kb (graph->init-invariants @brick-graph)

      input
      '{:type :coincident
        :m1 [[ground g1] {}]
        :m2 [[brick b1] {:e [-100.0 50.0 10.0]}]}

      pattern
      '{:mark {:loc [:ref #{[ground] [brick b1]}]
               :z [:ref #{[ground]}]
               :x [:ref #{[ground]}]}
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
      '{:loc [:ref #{[ground] [brick b3]}]
        :z [:ref #{[ground]}]
        :x [:ref #{[ground]}]}
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
    (expect link-pattern result-link)) )


;; simulate the second point being repositioned,

(let [kb (graph->init-invariants @brick-graph)
      constraints (joints->constraints @brick-graph)

      first-input
      '{:type :coincident
        :m1 [[ground g1] {:e [2.0 0.0 0.0]}]
        :m2 [[brick b1] {:e [0.0 0.0 0.0]}]}

      first-mark-pattern
      '{:loc [:ref #{[ground] [brick b1]}]
        :z [:ref #{[ground]}]
        :x [:ref #{[ground]}]}
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
      '{:loc [:ref #{[ground] [brick b1] [brick b2]}]
        :z [:ref #{[ground] [brick b2]}]
        :x [:ref #{[ground]}]}

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
      '{:loc [:ref #{[ground] [brick]}]
        :z [:ref #{[ground] [brick]}]
        :x [:ref #{[ground] [brick]}]}

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

