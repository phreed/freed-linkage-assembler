
(def arm-component
     {:datums
      { "csys-1"
        { :type :csys
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "axis-1"
        { :type :axis
          :marker {:x 0.0 :y 0.0 :z 0.0 :i 0.0 :j 0.0 :k 1.0} }
        "point-1"
        { :type :point
          :marker {:x 0.0 :y 1.0 :z 0.0} }
        "axis-2"
        { :type :axis
          :marker {:x 10.0 :y 0.0 :z 0.0 :i 0.0 :j 0.0 :k 1.0} }
        "point-2"
        { :type :point
          :marker {:x 10.0 :y 1.0 :z 0.0} }
        "plane-1"
        { :type :plane
          :marker {:x 0.0 :y 0.0 :z 0.0 :i 0.0 :j 0.0 :k 1.0} } }

      :joints
      { "joint-fixed-0"
        { :type :o2p :specified false
          :constraint #{"csys-1"}
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "joint-revolute-1"
        { :type :revolute :specified true
          :constraint #{"plane-1" "axis-1"}
          :limit ["point_1" [:pi 0.0 0.5] ] ; :limit must conform
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "joint-revolute-2"
        { :type :revolute :specified true
          :constraint #{"plane-1" "axis-2"}
          :guide ["point_2" [:pi 0.0 -0.5] ] ; :guide implies best effort
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} } } } )

(def ground-component arm-component)
(def right-arm-component arm-component)
(def left-arm-component arm-component)


(def triple-linkage-assembly
     {:connections
      { :ground [ [ground-component "joint-fixed-0"] ]
       "left-rocker"
        [ [left-arm-component "joint-revolute-1"]
          [ground-component "joint-revolute-2"] ]
       "right-rocker"
        [ [ground-component "joint-revolute-1"]
          [right-arm-component "joint-revolute-2"]]
       "top-rocker"
        [ [right-arm-component "joint-revolute-1"]
          [left-arm-component "joint-revolute-2"]] } } )



(pprint triple-linkage-assembly)


(defn merge-constraint-pair
  "This function takes two constraints and merges
  them into one."
  [lhs rhs]
  (cond ))

(let [ {datums :datums joints :joints} ground-component]
  (for [[joint-name joint-data] joints]
    (reduce merge-constraint-pair
            (get datums (get joint-data :constraint)))))

(assoc-in triple-linkage-assembly [:connections "right-rocker"] "junk")
(group-by :datums triple-linkage-assembly)


(match [ground-component]
       )
