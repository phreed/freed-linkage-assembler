
(ns cyphy.excavator-plane-test
  (:require [midje.sweet :as tt]
            [isis.geom.cyphy
             [cyphy-zip :as cyphy]
             [cad-stax :as stax]]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-constraint]
            [isis.geom.model.lower-joint :as lower-joint]
            [isis.geom.machine.misc :as misc]
            [isis.geom.algebra [geobj :as ga]]

            [isis.geom.analysis
             [position-analysis :as pa]]

            [isis.geom.machine.misc :as misc]
            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-dispatch]
             [helical-dispatch]
             [in-line-dispatch]
             [in-plane-dispatch]
             [offset-x-dispatch]
             [offset-z-dispatch]
             [parallel-z-dispatch]]))


(with-open [fis (-> "excavator/excavator_total_plane.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/knowledge-via-input-stream fis)
        constraints-orig (:constraint kb)
        constraints-nil-patch (lower-joint/nil-patch-collection constraints-orig)
        constraints-meta (meta-constraint/expand-collection constraints-nil-patch)
        constraints-lower (lower-joint/expand-collection constraints-meta)

        choose-pair (fn [x c1 c2]
                      (let [l1 (-> x :m1 first first)
                            l2 (-> x :m2 first first) ]
                        (or (and (= l1 c1) (= l2 c2))
                            (and (= l1 c2) (= l2 c1)) ))) ]

    ;; _ (pp/pprint ["constraints-orig:" constraints-orig])

    ;; The arm2 {a93..51f} is connected to
    ;; the boom {99c..264} via a revolute joint.
    (tt/facts
     "about the parsed cad-assembly file ground (assembly)-to-carriage"
     (tt/fact "about the base link id" (:base kb) => "{ASSY}|1")

     (tt/fact
      "original the carriage is connected to the base assembly via three planes"
      (filterv #(choose-pair % "{ASSY}|1" "{CARRIAGE}")
               constraints-orig) =>
      [{:m1 [["{CARRIAGE}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ASSY}|1" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :planar}
       {:m1 [["{CARRIAGE}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :type :planar}
       {:m1 [["{CARRIAGE}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :type :planar}] )

     (tt/fact
      "meta expanded "
      (filterv #(choose-pair % "{ASSY}|1" "{CARRIAGE}")
               constraints-meta) =>
      [{:m1 [["{CARRIAGE}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ASSY}|1" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :planar}
       {:m1 [["{CARRIAGE}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :type :planar}
       {:m1 [["{CARRIAGE}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :type :planar}] )

     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}|1" "{CARRIAGE}")
                     (= :parallel-z (:type %)) )
               constraints-lower) =>
      [{:m1 [["{CARRIAGE}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ASSY}|1" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :parallel-z}
       {:m1 [["{CARRIAGE}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :type :parallel-z}
       {:m1 [["{CARRIAGE}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :type :parallel-z}] )

     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}|1" "{CARRIAGE}")
                     (= :in-plane (:type %)) )
               constraints-lower) =>
      [{:m1 [["{CARRIAGE}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ASSY}|1" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :in-plane}
       {:m1 [["{CARRIAGE}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :type :in-plane}
       {:m1 [["{CARRIAGE}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :type :in-plane}] )


     (tt/fact
      "fully expanded to in-line primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}|1" "{CARRIAGE}")
                     (= :in-line (:type %)) )
               constraints-lower) =>
      []) )


    (tt/facts
     "about the parsed cad-assembly file arm-to-boom"

     (tt/fact
      "original "
      (filterv #(choose-pair % "{BOOM}" "{ARM}")
               constraints-orig) =>
      [{:m1
        [["{BOOM}" "CENTER_PLANE"]
         {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2
        [["{ARM}" "BOOM_CENTER_PLANE"]
         {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :planar}
       {:m1
        [["{BOOM}" "ARM_GUIDE"]
         {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
        :m2
        [["{ARM}" "BOOM_GUIDE"]
         {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
        :type :planar}
       {:m1
        [["{BOOM}" "ARM_AXIS"]
         {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2
        [["{ARM}" "BOOM_AXIS"]
         {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :type :linear}] )

     (tt/fact
      "meta expanded "
      (filterv #(choose-pair % "{BOOM}" "{ARM}")
               constraints-meta) =>
      [{:m1
        [["{BOOM}" "CENTER_PLANE"]
         {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2
        [["{ARM}" "BOOM_CENTER_PLANE"]
         {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :planar}

       {:m1
        [["{BOOM}" "ARM_GUIDE"]
         {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
        :m2
        [["{ARM}" "BOOM_GUIDE"]
         {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
        :type :planar}

       {:m1
        [["{BOOM}" "ARM_AXIS"]
         {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2
        [["{ARM}" "BOOM_AXIS"]
         {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :type :linear}] )

     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{BOOM}" "{ARM}")
                     (= :parallel-z (:type %)) )
               constraints-lower) =>
      ;; for each of the :planar joints
      [{:m1 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2 [["{ARM}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :parallel-z}

       {:m1 [["{BOOM}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
        :m2 [["{ARM}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
        :type :parallel-z}

       ;; for the :linear joint
       {:m1 [["{BOOM}" "ARM_AXIS"]
             {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ARM}" "BOOM_AXIS"]
             {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :type :parallel-z}] )

     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{BOOM}" "{ARM}")
                     (= :in-plane (:type %)) )
               constraints-lower) =>
      [{:m1 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2 [["{ARM}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :in-plane}

       {:m1 [["{BOOM}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
        :m2 [["{ARM}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
        :type :in-plane}] )


     (tt/fact
      "fully expanded to in-line primitive-joints "
      (filterv #(and (choose-pair % "{BOOM}" "{ARM}")
                     (= :in-line (:type %)) )
               constraints-lower) =>
      [{:m1 [["{BOOM}" "ARM_AXIS"]
             {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ARM}" "BOOM_AXIS"]
             {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :type :in-line}] ) )


    ;; The boom {99c..264} is connected to
    ;; a hydraulic-jack {7d2..491} via a revolute joint.
    (tt/facts
     "about the parsed cad-assembly file boom-to-arm-cyl"

     (tt/fact
      "lower-joint original"
      (filterv #(choose-pair % "{BOOM}" "{ARM-CYL}")
               constraints-orig) =>
      [{:m1
        [["{BOOM}" "CENTER_PLANE"]
         {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2
        [["{ARM-CYL}" "CYLINDER_PLANE"]
         {:e [-266.844 7143.04 -427.1],
          :pi 0.0,
          :q [-44.021 88.35 104.392]}],
        :type :planar}
       {:m1
        [["{BOOM}" "UPPER_CYLINDER_AXIS"]
         {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2
        [["{ARM-CYL}" "CYLINDER_AXIS"]
         {:e [-1703.15 6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}],
        :type :linear} ] )

     ;; The hydaulic-jack {7d2..491} is connected to
     ;; the arm {a93..51f} via a revolute joint.
     (tt/fact
      "upper-joint original"
      (filterv #(choose-pair % "{BOOM}" "{ARM-CYL}")
               constraints-orig) =>
      [{:m1 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2 [["{ARM-CYL}" "CYLINDER_PLANE"]
             {:e [-266.844 7143.04 -427.1], :pi 0.0, :q [-44.021 88.35 104.392]}],
        :type :planar}

       {:m1 [["{BOOM}" "UPPER_CYLINDER_AXIS"]
             {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ARM-CYL}" "CYLINDER_AXIS"]
             {:e [-1703.15 6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}],
        :type :linear}] ) )


    (tt/facts
     "about the parsed cad-assembly file invariants"

     (tt/fact
      "about the initial ASSY link settings"
      (-> kb :link (get "{ASSY}|1") deref) =>
      {:name nil :rdof {:# 0}, :tdof {:# 0},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}} )
     (tt/fact
      "about the initial CARRIAGE link settings"
      (-> kb :link (get "{CARRIAGE}") deref) =>
      {:name "UPPER_BODY"
       :rdof {:# 3}, :tdof {:# 3},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}} )
     (tt/fact
      "about the initial BOOM link settings"
      (-> kb :link (get "{BOOM}") deref) =>
      {:name "BOOM"
       :rdof {:# 3},
       :tdof {:# 3},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}} )
     (tt/fact
      "about the initial ARM link settings"
      (-> kb :link (get "{ARM}") deref) =>
      {:name "ARM_ASSEMBLY_2"
       :rdof {:# 3},
       :tdof {:# 3},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}} )
     (tt/fact
      "about the initial ARM-CYL link settings"
      (-> kb :link (get "{ARM-CYL}") deref) =>
      {:name "HYDRAULICJACK"
       :rdof {:# 3}, :tdof {:# 3},
       :versor {:rotate [1.0 0.0 0.0 0.0],
                :xlate [0.0 0.0 0.0]}} )


     (tt/fact
      "about the initial marker invariants"
      (into {} (map (fn [[k v]] [k @v]) (:invar kb))) =>
      {:loc #{["{ASSY}|1"]}
       :dir #{["{ASSY}|1"]}
       :twist #{["{ASSY}|1"]}} )  )


    (let [result (pa/position-analysis kb constraints-lower)
          [success? result-kb result-success result-failure] result
          {result-mark :invar result-link :link} result-kb ]

      ;; (pp/pprint result-success)
      ;; (pp/pprint result-link)
      (tt/facts
       "about results of linkage-assembly"

       (tt/fact
        "about the mark result" result-mark =>
        (tt/contains
         []) ))


      (tt/fact
       "about the final ASSY link settings"
       (-> result-link (get "{ASSY}|1") deref) =>
       {:name nil,
        :rdof {:# 0}, :tdof {:# 0},
        :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}} )

      (tt/fact
       "about the final CARRIAGE link settings"
       (-> result-link (get "{CARRIAGE}") deref) =>
       {:name "UPPER_BODY",
        :rdof {:# 0}, :tdof {:# 0, :point [0.0 0.0 0.0]},
        :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}} )
      (tt/fact
       "about the final BOOM link settings"
       (-> result-link (get "{BOOM}") deref) =>
       {:name "BOOM",
        :rdof {:# 0},
        :tdof {:# 1, :lf [112.29574170018896 2733.2689134252414 -1636.3200000000002],
               :line (ga/line [-1260.096573606902 994.6897241093869 -1636.32]
                              [-0.6195969498011189 -0.7849201359355931 0.0]),
               :point [112.29574170018896 2733.2689134252414 -1636.3200000000002]},
        :versor {:rotate [0.9536849771679109 0.0 0.0 0.3008071879530829],
                 :xlate [5002.344584571946 700.9972814014632 -3006.3100000000004]}} )
      (tt/fact
       "about the final ARM-CYL link settings"
       (-> result-link (get "{ARM-CYL}") deref) =>
       {:name "HYDRAULICJACK",
        :rdof {:# 3}, :tdof {:# 3},
        :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}})
      (tt/fact
       "about the final ARM link settings"
       (-> result-link (get "{ARM}") deref) =>
       {:name "ARM_ASSEMBLY_2",
        :rdof {:# 3}, :tdof {:# 3},
        :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}})


      (tt/incipient-fact
       "about the success result" result-success => [])

      (tt/incipient-fact
       "about the failure result" result-failure =>
       [] )

      #_(with-open [fis (-> "excavator/excavator_total_plane.xml"
                            jio/resource jio/input-stream)
                    fos (-> "/tmp/excavator_total_plane_aug.xml"
                            jio/output-stream)]

          (cyphy/update-cad-assembly-using-knowledge fis fos kb) ) ) ))


