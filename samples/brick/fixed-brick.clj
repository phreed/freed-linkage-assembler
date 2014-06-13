;; there are a set of named geoms.
;; each geom has a set of joints.
;; each joint has a marker.
;; each joint has a set of constraints.
;; each geom has a set of invarients.
;; each marker has a set of invarients.

(defn make-geom
  [& {:as opts}]
  (ref (merge {:name name :constraints {} :markers {} } opts)))

(defn make-marker
  "Creates a marker in the appropriate coordinate frame."
  [& {:as opts}]
  (merge {:position [0.0 0.0 0.0] :z-axis [0.0 0.0 1.0] :x-axis [1.0 0.0 0.0]} opts))

(def brick-graph
  {:geoms (ref {}) })


(let [geoms (brick-graph :geoms)]
  (dosync
   (let [markers {'m1 (make-marker)
                  'm2 (make-marker :position [1.0 0.0 0.0])
                  'm3 (make-marker :position [0.0 1.0 0.0]) }
        joints {'j1 {:type :ball-joint :marker (get markers 'm1)}
                'j2 {:type :ball-joint :marker (get markers 'm2)}
                'j3 {:type :ball-joint :marker (get markers 'm2)}} ]
   (alter geoms assoc 'ground (make-geom :markers markers :joints joints)) ) )

  (dosync
   (let [markers {'m1 (make-marker)
                  'm2 (make-marker :position [1.0 0.0 0.0])
                  'm3 (make-marker :position [0.0 1.0 0.0]) }
        joints {'j1 {:type :ball-joint :marker (get markers 'm1)}
                'j2 {:type :ball-joint :marker (get markers 'm2)}
                'j3 {:type :ball-joint :marker (get markers 'm2)}} ]
   (alter geoms assoc 'brick (make-geom :markers markers :joints joints)) ) ))

brick-graph


