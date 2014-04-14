(def triple-linkage
  {
   :component
   { "ground"
     {:datums
      { "csys-1"
        { :type :csys
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "axis-1"
        { :type :axis
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} }
        "axis-2"
        { :type :axis
          :marker {:x 10.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} }
        "plane-1"
        { :type :plane
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} } }

      :joints
      { "joint-0"
        { :type :fixed :specified true
          :constraint ["csys-1"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "joint-1"
        { :type :revolute :specified true
          :constraint ["plane-1" "axis-1"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "joint-2"
        { :type :revolute :specified true
          :constraint ["plane-1" "axis-2"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} } }
      }

     "right-arm"
     {:datums
      { "csys-1"
        { :type :csys
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "axis-1"
        { :type :axis
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} }
        "axis-2"
        { :type :axis
          :marker {:x 10.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} }
        "plane-1"
        { :type :plane
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} } }
      :joints
      { "joint-0"
        { :type :fixed :specified true
          :constraint ["csys-1"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "joint-1"
        { :type :revolute :specified true
          :constraint ["plane-1" "axis-1"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "joint-2"
        { :type :revolute :specified true
          :constraint ["plane-1" "axis-2"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        }
      }

     "left-arm"
     {:datums
      { "csys-1"
        { :type :csys
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "axis-1"
        { :type :axis
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} }
        "axis-2"
        { :type :axis
          :marker {:x 10.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} }
        "plane-1"
        { :type :plane
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 0.0 :j 0.0 :k 1.0} } }
      :joints
      { "joint-0"
        { :type :fixed :specified true
          :constraint ["csys-1"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "joint-1"
        { :type :revolute :specified true
          :constraint ["plane-1" "axis-1"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} }
        "joint-2"
        { :type :revolute :specified true
          :constraint ["plane-1" "axis-2"]
          :marker {:x 0.0 :y 0.0 :z 0.0 :pi 0.0 :i 1.0 :j 1.0 :k 1.0} } }
      }
     }
   :assembly
   { "main"
     {:connections
      {"right-rocker" [ ["ground" "joint-1"] ["right-arm" "joint-2"]]
       "left-rocker"  [ ["ground" "joint-2"] ["left-arm" "joint-1"]]
       "top-rocker" [ ["right-arm" "joint-1"] ["left-arm" "joint-2"]] } }
     }
   }
  )
