(ns isis.geom.design.graph-state
  (:require (isis.geom.design
             [all-loops :refer [asymetric-joint-primitives
                                asymetric-joint-primitive?]])
            (isis.geom.analysis
             [utilities :refer [c-type clear-invariants! add-invariant!]])))

(def constraint-graph
  "A map that stores the constraint graph.
  The sytem progresses by adding and removing
  constraints from this graph.
  When the graph is empty all constraints have been
  satisfied and the assembly is completed.

  An entry in this map has the form:
  <element-name> <element-value>
  Where the element-name is a string and the element-value
  varies based on the element type."
  (atom {}))

(defn get-graph-element
  "Allow access to the graph by name of component."
  [?element-name]
  (@constraint-graph ?element-name))

(defn update-graph-element!
  "Set the graph element by name and value."
  [?element-name ?new-value]
  (swap! constraint-graph assoc ?element-name ?new-value ))

(defn remove-graph-element!
  [?element-name]
  (swap! constraint-graph dissoc constraint-graph ?element-name))


(defn setup-joint
  "Converts from simple specifier :constraint to the form
  [:constraint marker1 marker2], where the markers are
  repeated for each constraint on a given joint.

  This also adds the symetric form of each constraint.
  ?joint : [<:constraint-1>...<:constraint-n>].

  in-line and :in-plane are symetric in conjunction with :parallel-x"
  [?joint ?m-1 ?m-2]
  (let [reformed-joint (map #([% ?m-1 ?m-2]) ?joint)

        symetric-joints
        (for [x ?joint
              :when (not (asymetric-joint-primitive? x))]
               [x ?m-2 ?m-1])

        conj-symetric-joints
        (for [x asymetric-joint-primitives
              :when (and (some #{:parallel-x} ?joint)
                         (some #(= (c-type x) %) ?joint))]
               [x ?m-2 ?m-1]) ]
    (into [] (concat reformed-joint symetric-joints conj-symetric-joints))))

(defn setup-graph
  "Given a loop description, initialize the constraint graph.
  A loop description is of the form:
  [[[<:constraint-1>...<:constraint-n>] <dof>, ...] <dof-total>]"
  [?joint-loop]
  (let [joint-list (first ?joint-loop)
        j1 (setup-joint (first (nth joint-list 0)) 'm13-1 'm12-l)
        j2 (setup-joint (first (nth joint-list 1)) 'm23-2 'm12-2)
        j3 (setup-joint (first (nth joint-list 2)) 'm13-3 'm23-3)]
    (update-graph-element! 'j1 j1)
    (update-graph-element! 'j2 j2)
    (update-graph-element! 'j3 j3)

    (update-graph-element! 'l13 [3 3])
    (update-graph-element! 'l23 [3 3])
    (update-graph-element! 'l12 [0 0])

    (clear-invariants!)
    (doseq [av  ['m12-1 'm12-2] ]
      (add-invariant! av :position)
      (add-invariant! av :z)
      (add-invariant! av :x) )) )

(defn geom-markers
  "Return the marker vector for a particular geom."
  [?geom-name]
  (case ?geom-name
    l12 ['m12-1 'm12-2]
    l13 ['m13-1 'm13-2]
    l23 ['m23-1 'm23-2]
    ))

(defn get-geom-status
  "Get the status of the named geom."
  [?geom-name ?new-status]
  (get-graph-element ?geom-name))

(defn update-geom-status!
  "Set the status of the named geom."
  [?geom-name ?new-status]
  (update-graph-element! ?geom-name ?new-status))

(defn joint-constraints
  "Return the joint constraints by joint name."
  [?joint-name]
  (get-graph-element ?joint-name))

(defn update-joint-constraints!
  "Set the constraints belonging to the named joint."
  [?joint-name ?new-constraints]
  (update-graph-element! ?joint-name ?new-constraints))

(defn remove-constraint!
  "Remove the constraint from the named joint."
  [?joint-name ?constraint]
  (remove-graph-element! ?joint-name)
  ;; remove symetric form if applicable
  #_(if-not (asymetric-joint-primitive? (c-type ?constraint))
    (update-joint-constraints! ?joint-name
          (remove [(c-type ?constraint) (c-m2 ?constraint) (c-m1 ?constraint)]
                  (joint-constraints ?joint-name) :test #'equal))))

(defn add-constraint!
  "Add a constraint to a named joint."
  [?joint-name ?constraint]
  (if-not (some ?constraint (joint-constraints ?joint-name))
    (update-joint-constraints! ?joint-name
                               (cons ?constraint (joint-constraints ?joint-name)))
    ;; add symentric form if applicable
    #_(if-not (asymetric-joint-primitive? (c-type ?constraint))
        (setf (joint-constraints ?joint-name)
              (cons [(c-type ?constraint) (c-m2 ?constraint) (c-m1 ?constraint)]
                    (joint-constraints ?joint-name)))) ))

(defn joint-has-constraint-type?
  "Returns 'true' if the the named joint has a
  primitive constraint of the specified type."
  [?joint-name ?constraint-type]
  (some (joint-constraints ?joint-name) ?constraint-type))

(defn find-constraint-of-type
  "Return 'true' if the joint contains a constraint of the specified type."
  [?joint-name ?constraint-type]
  (some #(= (first %) ?constraint-type)
        (joint-constraints ?joint-name)))

