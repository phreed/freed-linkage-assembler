(ns isis.geom.analysis.position-analysis)

(defn- model->graph [model])
(defn- model->connectivity [model])
(defn- graph->constrained [graph input])
(defn- graph->non-rigid [graph degen])
(defn- identify-loops [conn amount])
(defn- number-of-geoms [graph])
(defn- classify-all-loops [all-loops redundant])
(defn- pick-loop [all-loops redundant])
(defn- solve-loop [graph conn constrained non-rigid aloop])

(defn- found-parallel-z-sets? [])
(defn- found-coincident-markers-on-links? [])
(defn- found-spherical-sub-chains? [])


(defn position-analysis
  "Algorithm for finding a closed-form assembly
  procedure for a given constraint system, if one exists.
  Otherwise, the algorithm finds an iterative solution
  with a minimal number for redundant generalized
  coordinates.

  Inputs:
  model : the constraint system model
  input : the user specified driving input
  degen : any known degeneracies

  Outputs:
  success? : a boolean indicating success (or failure)

  Variables:
  graph : the constraint graph
  conn : the constraint graph connectivity array
  plan : the metaphorical assembly plan
  redundant : the list of redundant generalized coordinates
  aloop : a loop that is to be solved

  "
  [model input degen]
  (let [init-graph (model->graph model)
        init-conn (model->connectivity model)
        init-constrained (graph->constrained init-graph input)
        init-non-rigid (graph->non-rigid init-graph degen)]
    (cond (found-parallel-z-sets?) false
          (found-coincident-markers-on-links?) false
          (found-spherical-sub-chains?) false
          :else
          (loop [graph init-graph
                 conn init-conn
                 constrained init-constrained
                 non-rigid init-non-rigid
                 plan {} redundant {}]
            (if (< 2 (count graph))
              true
              (let [all-loops (identify-loops conn (number-of-geoms graph))]
                (if (empty? all-loops) false
                  (let [classified-loops (classify-all-loops all-loops)
                        [aloop new-redundant] (pick-loop classified-loops redundant)
                        [new-graph new-conn new-constrained new-non-rigid new-plan]  (solve-loop graph conn constrained non-rigid aloop)]
                    (recur new-graph new-conn new-constrained new-non-rigid new-plan new-redundant))) ))))))



