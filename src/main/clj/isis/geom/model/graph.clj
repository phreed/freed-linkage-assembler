(ns isis.geom.model.graph
 (:require [isis.geom.model
             [joint :refer [joint-primitive-map]]
             [invariant :refer [init-marker-invariant-s
                                init-link-invariant-s
                                init-link-invariant
                                marker->add-invariant!
                                make->invariant]]]))


;; there are a set of named links.
;; each link has a set of joints.
;; each joint has a marker.
;; each joint has a set of constraints.
;; each link has a set of invariants.
;; each marker has a set of invariants.

(defn validate-graph
  "A function to validate the linkage graph."
  [g]
  true )

(defn make->link
  "Construct a geometric component, i.e. a link"
  [name & {:as opts}]
  (merge {:name name :markers {} :ports {} :invariant {}} opts))


(defn get-dof-key
  "Get the DoF key for the geometry."
  [link]

  )

(defn make->marker
  "Creates a marker in the appropriate coordinate frame."
  [& {:as opts}]
  (merge #_{:e1 0.0 :e2 0.0 :e3 0.0 :z1 0.0 :z2 0.0 :z3 0.0 :x1 0.0 :x2 0.0 :x3 0.0}
         {} opts))

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


(defn port-pair->make-constraint
  "Using a graph's joint definitions,
  generate a list of constraints for the joint primitives.
  Each constraint primitive is generated by merging
  corresponding ports."
  [graph port-pair]
  (let [ [p1 p2] (seq port-pair)
         [p1-link p1-name p1-type p1-mark] p1
         [p2-link p2-name p2-type p2-mark] p2]
    (when (not= p1-type p2-type)
      (throw (IllegalStateException.
              (str "the connected joints have mismatched types : "
                   p1-type " != " p2-type)) ))
    {:type p1-type
     :m1 [[p1-link p1-name]
          {:e (into [] (map p1-mark [:e1 :e2 :e3] ))
           :z (into [] (map p1-mark [:z1 :z2 :z3] ))
           :x (into [] (map p1-mark [:x1 :x2 :x3] ))}]
     :m2 [[p2-link p2-name]
          {:e (into [] (map p2-mark [:e1 :e2 :e3] ))
           :z (into [] (map p1-mark [:z1 :z2 :z3] ))
           :x (into [] (map p2-mark [:x1 :x2 :x3] ))}]}))


(defn joint-pair->constraint
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


(defn graph->init-invariants
  "There are two types of invariants, link and marker.
  To start with there is only the link invariant which
  specifies the base (grounded/fixed) link.
  The marker invariants for that link are also invariant."
  [graph]
  (dosync
   (let [mis (init-marker-invariant-s)
         mis-l (:loc mis), mis-z (:z mis), mis-x (:x mis)
         base-link-name (:base graph)
         links (:links graph)
         free-links (remove #{base-link-name} (keys links))
         base-link (base-link-name links)
         markers (:markers base-link)]
     (doseq [ marker-name (keys markers) ]
       (let [marker-key [base-link-name marker-name]]
         (alter mis-l conj marker-key)
         (alter mis-z conj marker-key)
         (alter mis-x conj marker-key) ))
     {:mark mis
      :link (into
          {}
          (map #(hash-map % (if (= % base-link-name)
                              (init-link-invariant :fixed)
                              (init-link-invariant :free)
                              )) (keys links)))}) ))


(defn joints->constraints
  "Using a graph's joint definitions,
  generate a list of constraints for the joint primitives.
  Each constraint primitive is generated by merging
  corresponding ports."
  [graph]
  (mapcat identity
          (for [joint-pair (:joints graph)]
            (joint-pair->constraint graph joint-pair))))

