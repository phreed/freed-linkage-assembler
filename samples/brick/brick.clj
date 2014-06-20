(ns brick
  "Sample assembly for brick and ground."
  (:require [isis.geom.model.joint :refer [joint-primitive-map]]
            [isis.geom.action-dispatch :refer [transform!]]))

;; there are a set of named links.
;; each link has a set of joints.
;; each joint has a marker.
;; each joint has a set of constraints.
;; each link has a set of invarients.
;; each marker has a set of invarients.


(defn make->link
  "Construct a geometric component, i.e. a link"
  [name & {:as opts}]
  (merge {:name name :markers {} :ports {} :invariant {}} opts))

(defn make->invariant
  "Create a invariant in the link as outlined in C.3
  points-inv : list of points having invariant position
  points-1d  : list of (point locus) pairs, where point is restricted to 1d-locus
  points-2d  : list of (point locus) pairs, where point is restricted to 2d-locus
  vec-inv    : list of vectors with invariant orientation
  vec-1d     : list of (vector locus) pairs, where the
               orientation of the vector is restricted to the 1d-locus"
  [& {:as opts}]
  {:point-inv [], :point-1d [], :point-2d [],
   :vec-inv [], :vec-1d []})

(defn get-dof-key
  "Get the DoF key for the geometry."
  [link]

  )

(defn make->marker
  "Creates a marker in the appropriate coordinate frame."
  [& {:as opts}]
  (merge #_{:p1 0.0 :p2 0.0 :p3 0.0 :z1 0.0 :z2 0.0 :z3 0.0 :x1 0.0 :x2 0.0 :x3 0.0}
         {} opts))


(def brick-graph
  "From section 3.4 : Example 1: the brick
  This object is composed of the prime data for an assembly.
  The derived data should not be stored in this object directly,
  but be stored in other objects with simliar keys."
  (ref
   { :links
     { 'ground (make->link
                'ground
                :markers {'g1 (make->marker )
                          'g2 (make->marker :p1 1.0 :p2 0.0 :p3 0.0)
                          'g3 (make->marker :p1 0.0 :p2 1.0 :p3 0.0) }
                :ports {'j1 {:type :prismatic :marker 'g1}
                        'j2 {:type :spherical :marker 'g2}
                        'j3 {:type :spherical :marker 'g3}}
                :invariant (make->invariant :points-inv ['g1 'g2 'g3] ))

       'brick  (make->link
                'brick
                :markers {'b1 (make->marker :p1 -100.0 :p2 50.0 :p3 10.0)
                          'b2 (make->marker :p1 -99.0 :p2 50.0 :p3 10.0)
                          'b3 (make->marker :p1 -100.0 :p2 51.0 :p3 10.0) }
                :ports {'j1 {:type :prismatic :marker 'b1}
                        'j2 {:type :spherical :marker 'b2}
                        'j3 {:type :spherical :marker 'b3}} ) }
     :base 'ground
     :joints
     #{#{['ground 'j1] ['brick 'j1]}
       #{['ground 'j2] ['brick 'j2]}
       #{['ground 'j3] ['brick 'j3]}} }))

(def brick-graph-goal
  "This shows the ultimate goal for the action-analysis.
  Notice that the ground has no properties, this idicates the
  use of the default values, it also indicates the 'base' object.
  In this example there is no rotation so the {:i1 :i2 :i3} values
  could be anything."
  {:link-motors
   {'ground {}
    'brick {:p1 100.0 :p2 -50.0 :p3 -10.0}}})


(declare graph->expand-joints graph->action-analysis)

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


(defn port->expand
  "Expand a single port into a vector of port-primitives."
  [graph [link-name port-name]]
  (let [links (:links graph)
        link (get links link-name)
        ports (:ports link)
        port (get ports port-name)
        primitive-types ((:type port) joint-primitive-map)
        port-marker-name (:marker port)
        markers (:markers link)
        port-marker (get markers port-marker-name)]
    (into [] (for [primitive-type primitive-types]
               [link-name port-marker-name primitive-type port-marker])) ) )

(port->expand @brick-graph ['ground 'j1])

(defn port-pair->make-constraint
  "Using a graph's joint definitions,
  generate a list of constraints for the joint primitives.
  Each constraint primitive is generate by merging
  corresponding ports."
  [graph port-pair]
  (let [ [p1 p2] (seq port-pair)
         [p1-link p1-name p1-type p1-mark] p1
         [p2-link p2-name p2-type p2-mark] p2]
    (when (not= p1-type p2-type)
      (throw (IllegalStateException.
              (str "the connected joints have mismatched types : "
                   p1-type " != " p2-type)) ))
    {:type p1-type :m1 [[p1-link p1-name] p1-mark] :m2 [[p2-link p2-name] p2-mark]} ) )


(port-pair->make-constraint @brick-graph
                            [['ground 'g2 :coincident {:p1 1.0, :p3 0.0, :p2 0.0}]
                             ['brick 'b2 :coincident {:p1 101.0, :p3 0.0, :p2 0.0}]] )

(defn graph->expand-joint-pair
  "Using a joint definitions, generate a list of constraints for the joint's primitives.
  Each constraint primitive is generate by merging corresponding ports."
  [graph joint-pair]
  (let [ link (:links graph)
         constr-lists (for [[port-link port-joint] (seq joint-pair)]
                        (port->expand graph [port-link port-joint]))
         [c1-list c2-list] constr-lists
         con-pair-list (map vector c1-list c2-list)]
    (for [con-pair con-pair-list]
      (port-pair->make-constraint graph con-pair))))


(graph->expand-joint-pair @brick-graph #{['ground 'j1] ['brick 'j1]})

(defn graph->extract-invariants
  "Extract all the invariants."
  [graph]
  (let [ link (:links graph) ]
    (for [con-pair con-pair-list]
      (port-pair->make-constraint graph con-pair))))

(graph->extract-invariants @brick-graph)


(defn graph->expand-joints
  "Using a graph's joint definitions,
  generate a list of constraints for the joint primitives.
  Each constraint primitive is generated by merging
  corresponding ports."
  [graph]
  (mapcat identity
          (for [joint-pair (:joints graph)]
            (graph->expand-joint-pair graph joint-pair))))

(graph->expand-joints @brick-graph)


(def brick-constraints
  [
    ] )

;; (clojure.pprint/pprint
(try
  (for [constraint (graph->expand-joints @brick-graph)
        :let [ {c-type :type m1 :m1 m2 :m2} constraint ] ]
    (str c-type m1 m2) )

  (catch IllegalStateException ex
    (println ex)))



(defn action-analysis
  "Algorithm for using the plan fragment table to perform action alalysis.
  We update a link map and a marker map with invariants.
  The link map of invariants indicates just how well placed the link is.
  The marker map does a similar thing."
  [constraints invariants]
  (let [ [m0 c0] (for-split get-plan-fragment constraints)
         [m1 c1] (for-split get-plan-fragment c0) ] ) )

(try
  (action-analysis
   (graph->expand-joints @brick-graph)
   (graph->extract-invariants @brick-graph))

  (catch IllegalStateException ex
    (println ex)))


