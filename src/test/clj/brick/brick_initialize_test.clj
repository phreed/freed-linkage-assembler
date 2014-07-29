(ns brick.brick-initialize-test
  "Sample assembly for brick and ground."
  (:require [expectations :refer :all]
            [isis.geom.machine.misc :as misc]
            [isis.geom.model
             [graph :refer [ port->expand
                             port-pair->make-constraint
                             graph->init-invariants
                             joint-pair->constraint
                             joints->constraints]] ] ))


(defn- ref->str
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
 '{:mark {:loc [:ref #{[ground]}],
          :z [:ref #{[ground]}],
          :x [:ref #{[ground]}]},
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


