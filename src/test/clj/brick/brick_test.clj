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
                            graph->expand-joint-pair
                            graph->expand-joints]]
             [invariant :refer [init-marker-invariant-s
                                init-link-invariant-s
                                init-link-invariant
                                marker->add-invariant!
                                make->invariant]]]
            [isis.geom.action-dispatch
             :refer [precondition?->transform!]]
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
  (clojure.walk/postwalk #(if (misc/reference? %) (list 'ref @%) %) form))



(def brick-graph
  "From section 3.4 : Example 1: the brick
  This object is composed of the prime data for an assembly.
  The derived data should not be stored in this object directly,
  but be stored in other objects with simliar keys."
  (ref
   '{ :links
      {ground {
                :markers {g1 {:e1 5.0 :e2 0.0 :e3 0.0}
                          g2 {:e1 8.0 :e2 0.0 :e3 0.0}
                          g3 {:e1 5.0 :e2 4.0 :e3 0.0} }
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
        #{[cap j3] [brick jc3]}} }))

(def brick-graph-goal
  "This shows the ultimate goal for the action-analysis.
  Notice that the ground has no properties, this idicates the
  use of the default values, it also indicates the 'base' object.
  In this example there is no rotation so the {:z3 :z1 :z2} values
  could be anything."
  '{:link-motors
    {ground {}
     brick {:e [8.0 -5.0 -1.0] :z []}}})


(declare graph->action-analysis)

(defn make-graph-watcher
  "This function is suitable to use as a watch function.
  When a graph changes it will compute the placement of
  each link in its assembly.  The augmented graph will
  be produced by this function."
  []
  (defn graph->assemble
    [key reference old-state new-state]
    (graph->expand-joints new-state))

  (add-watch brick-graph :assembly-key graph->assemble) )


(expect
 '[[ground g1 :coincident {:e1 5.0, :e2 0.0, :e3 0.0}]]
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
 '{:m {:p (ref #{[ground g1] [ground g3] [ground g2]}),
       :z (ref #{[ground g1] [ground g3] [ground g2]}),
       :x (ref #{[ground g1] [ground g3] [ground g2]})},
   :l {brick (ref {:tdof {:# 3}, :rdof {:# 3},
                   :p {:e [0.0 0.0 0.0] :z [0.0 0.0 1.0] :x [1.0 0.0 0.0]}})
       ground (ref {:tdof {:# 0}, :rdof {:# 0},
                    :p {:e [0.0 0.0 0.0] :z [0.0 0.0 1.0] :x [1.0 0.0 0.0]}})
       cap (ref {:tdof {:# 3}, :rdof {:# 3},
                 :p {:e [0.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})}}
 (ref->str (graph->init-invariants @brick-graph)))

(expect
 '({:type :coincident
    :m1 [[ground g1] {:e [5.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
    :m2 [[brick b1] {:e [0.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]})
 (graph->expand-joint-pair @brick-graph '#{[ground j1] [brick jg1]}))


(expect
  '({:type :coincident
     :m1 [[ground g2] {:e [8.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
     :m2 [[brick b2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
    {:type :coincident
     :m1 [[brick b4] {:e [1.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
     :m2 [[cap c4] {:e [-1.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
    {:type :coincident
     :m1 [[ground g1] {:e [5.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
     :m2 [[brick b1] {:e [0.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
    {:type :coincident
     :m1 [[brick b3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]
     :m2 [[cap c3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]}
    {:type :coincident
     :m1 [[brick b3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]
     :m2 [[ground g3] {:e [5.0 4.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
    {:type :coincident
     :m1 [[cap c2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
     :m2 [[brick b2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]})
 (graph->expand-joints @brick-graph))


;; simulate the first point being repositioned, by 3-3-coincident
(expect
 '{:m {:p (ref #{[ground g1] [ground g3] [ground g2] [brick b1]})
       :z (ref #{[ground g1] [ground g3] [ground g2]})
       :x (ref #{[ground g1] [ground g3] [ground g2]})}
   :l {ground (ref {:tdof {:# 0} :rdof {:# 0}
                    :p {:e [0.0 0.0 0.0] :z [0.0 0.0 1.0] :x [1.0 0.0 0.0]}})
       brick (ref {:tdof {:# 0 :p [0.0 0.0 0.0]}, :rdof {:# 3}
                   :p {:e [100.0 -50.0 -10.0] :z [0.0 0.0 1.0] :x [1.0 0.0 0.0]}})
       cap (ref {:tdof {:# 3} :rdof {:# 3}
                 :p {:e [0.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})}}
 (let [ikb (graph->init-invariants @brick-graph)]
   (precondition?->transform! ikb
    '{:type :coincident
      :m1 [[ground g1] {}]
      :m2 [[brick b1] {:e [-100.0 50.0 10.0]}]} )
   (ref->str ikb)))

(expect
 '{:m {:p (ref #{[ground g1] [ground g3] [ground g2] [brick b3]})
       :z (ref #{[ground g1] [ground g3] [ground g2]})
       :x (ref #{[ground g1] [ground g3] [ground g2]})}
   :l {ground (ref {:tdof {:# 0}, :rdof {:# 0}
                    :p {:e [0.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})
       brick (ref {:tdof {:# 0, :p [5.0 4.0 0.0]}, :rdof {:# 3}
                   :p {:e [5.0 4.0 -4.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})
       cap (ref {:tdof {:# 3}, :rdof {:# 3},
                 :p {:e [0.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})}}
 (let [ikb (graph->init-invariants @brick-graph)]
   (precondition?->transform! ikb
    '{:type :coincident
      :m1 [[ground g3] {:e [5.0 4.0 0.0]}]
      :m2 [[brick b3] {:e [0.0 0.0 4.0]}]} )
   (ref->str ikb)))

;; simulate the second point being repositioned, by 0-3-coincident
(expect
 '{:m {:p (ref #{[ground g1] [ground g3] [ground g2] [brick b1]})
       :z (ref #{[ground g1] [ground g3] [ground g2]})
       :x (ref #{[ground g1] [ground g3] [ground g2]})}
   :l {ground (ref {:tdof {:# 0}, :rdof {:# 0}
                    :p {:e [0.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})
       brick (ref {:tdof {:# 0, :p [5.0 0.0 0.0]}, :rdof {:# 3}
                   :p {:e [5.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})
       cap (ref {:tdof {:# 3}, :rdof {:# 3},
                 :p {:e [0.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})}}
 (let [ikb (graph->init-invariants @brick-graph)]
   ;; repeat from previous test
   (precondition?->transform! ikb
    '{:type :coincident
      :m1 [[ground g1] {:e [5.0 0.0 0.0]}]
      :m2 [[brick b1] {:e [0.0 0.0 0.0]}]} )
   ;; apply the second point constraint
   (precondition?->transform! ikb
    '{:type :coincident
      :m1 [[ground g2] {:e [8.0 0.0 0.0]}]
      :m2 [[brick b2] {:e [0.0 3.0 0.0]}]})
   (ref->str ikb)))

(defn action-analysis
  "Algorithm for using the plan fragment table to perform action alalysis.
  We update a link map and a marker map with invariants.
  The link map of invariants indicates just how well placed the link is.
  The marker map does a similar thing.
  - We will repeatedly evaluate all constraints, making marker properties
  invariant and producing link motors, until no more constraints can be satisfied.
  The link motors are then returned.
  constraints : the list of constraints needing to be satisfied.
  invariants : invariant properties of markers and links.

  ys : constraints which have been tried and failed.
  progress? : is the current round making progress?"
  [ikb constraints]
  (loop [ [x & xs] constraints,
          ys [], progress? true]
    (if-not x
      (if (empty? ys)
        ;; all the constraints have been satisfied
        ikb
        (if progress?
          ;; still making progress, try again.
          (recur ys [] true)
          ;; no progress is possible.
          ikb))
      ;; working through the constraint list.
      (if (precondition?->transform! ikb x)
        (recur xs ys true)
        (recur xs ys progress?) ))))


(expect
 '{:m {:p (ref #{[ground g1] [ground g3] [ground g2] [brick b1] [brick b2] [brick b3] [cap c1] [cap c2] [cap c3]})
       :z (ref #{[ground g1] [ground g3] [ground g2] [brick b1] [brick b2] [brick b3] [cap c1] [cap c2] [cap c3]})
       :x (ref #{[ground g1] [ground g3] [ground g2] [brick b1] [brick b2] [brick b3] [cap c1] [cap c2] [cap c3]})}
   :l {ground (ref {:tdof {:# 0}, :rdof {:# 0}
                    :p {:e [0.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})
       brick (ref {:tdof {:# 0, :p [0.0 0.0 0.0]}, :rdof {:# 0}
                   :p {:e [-5.0 0.0 -4.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})
       cap (ref {:tdof {:# 0}, :rdof {:# 0},
                 :p {:e [0.0 0.0 0.0], :z [0.0 0.0 1.0], :x [1.0 0.0 0.0]}})}}
 '(let [graph @brick-graph]
    (action-analysis
     (graph->expand-joints graph)
     (graph->init-invariants graph))))

