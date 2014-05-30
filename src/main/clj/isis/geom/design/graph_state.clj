(ns isis.geom.design.graph-state )

(def ^:dynamic *graph*
  "An array that stores the graph structure."
  (make-array Integer/TYPE 6))

(defn graph-element
  "Allow access to the graph by name of component."
  [?element-name]
  (get @*graph*
       (case ?element-name
         j1 0
         j2 1
         j3 2
         l13 3
         j23 4
         l12 5)))

(defn graph-element!
  ""
  [?element-name ?new-value]
  '(setf (aref *graph* (case ?element-name
                         j1 0
                         j2 1
                         j3 2
                         l13 3
                         l23 4
                         l12 5))
         ?new-value))

(defn setup-graph
  "Given a loop description, initialize the constraint graph"
  [?loop]
  (let [joint-list (first ?loop)
        j1 (setup-joint (first (nth joint-list 0)) 'm13-1 'm12-l)
        j2 (setup-joint (first (nth joint-list 1)) 'm23-2 'm12-2)
        j3 (setup-joint (first (nth joint-list 2)) 'm13-3 'm23-3)]
    (setf (graph-element 'j1) j1 (graph-element 'j2) j2 (graph-element 'j3) j3
          (graph-element 'l13) [3 3]
          (graph-element 'l23) [3 3]
          (graph-element 'l12) [0 0])
    (clear-invariants)
    (map (fn [?i]
           (add-invariant ?i :position)
           (add-invariant ?i :z)
           (add-invariant ?i :x) )
         [m12-1 m12-2])))

(defn geom-markers
  "Return the marker vector for a particular geom."
  [?geom-name]
  (case ?geom-name
    l12 [m12-1 m12-2]
    l13 [m13-1 m13-2]
    l23 [m23-1 m23-2]
    ))

(defn update-geom-status!
  "Set the status of the named geom."
  [?geom-name ?new-status]
  '(setf (graph-element ?geom-name) ?new-status))

(defn joint-constraints
  "Return the joint constraints by joint name."
  [?joint-name]
  (graph-element ?joint-name))

(defn update-joint-constraints!
  "Set the constraints belonging to the named joint."
  [?joint-name ?new-constraints]
  '(setf (graph-element ?joint-name) ?new-constraints))

(defn remove-constraint!
  "Remove the constraint from the named joint."
  [?joint-name ?constraint]
  (setf (joint-constraints ?joint-name)
        (remove ?constraint (joint-constraints ?joint-name) :test #'equal))
  ;; remove symentric form if applicable
  (if-not (find (c-type ?constraint) @*asymetric-jprims*)
    (setf (joint-constraints ?joint-name)
          (remove [(c-type ?constraint) (c-m2 ?constraint) (c-m1 ?constraint)]
                  (joint-constraints ?joint-name) :test #'equal))))

(defn add-constraint!
  "Add a constraint to a named joint."
  (if-not (find ?constraint (joint-constraints ?joint-name) :test #'equal)
  (setf (joint-constraints ?joint-name)
        (cons ?constraint (joint-constraints ?joint-name)))
  ;; add symentric form if applicable
  (if-not (find (c-type ?constraint) @*asymetric-jprims*)
    (setf (joint-constraints ?joint-name)
          (cons [(c-type ?constraint) (c-m2 ?constraint) (c-m1 ?constraint)]
                (joint-constraints ?joint-name))))))

(defn joint-has-constraint-type?
  "Returns 'true' if the the named joint has a
  primitive constraint of the specified type."
  [?joint-name ?constraint-type]
  (find ?constraint-type (joint-constraints ?joint-name)
        :key #'car :test #'equal))

(defn find-constraint-of-type
  "Return 'true' if "
  [?joint-name ?constraint-name]
  (find (fn [?constraint] (= (first ?constraint) ?constraint-type))
        (joint-constraints ?joint-name)))

