(ns cyphy.excavator-rigid-test
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
             [offset-angle-dispatch]
             [offset-z-dispatch]
             [parallel-axis-dispatch]]))


(with-open [fis (-> "excavator/excavator_total_rigid.xml"
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
      [{:m1 [["{BOOM}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
        :m2 [["{ARM}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
        :type :planar}
       {:m1 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2 [["{ARM}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :planar}
       {:m1 [["{BOOM}" "ARM_AXIS"]
             {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ARM}" "BOOM_AXIS"]
             {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :type :linear}] )

     (tt/fact
      "meta expanded "
      (filterv #(choose-pair % "{BOOM}" "{ARM}")
               constraints-meta) =>
      [{:m1 [["{BOOM}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
        :m2 [["{ARM}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
        :type :planar}
       {:m1 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2 [["{ARM}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :planar}
       {:m1 [["{BOOM}" "ARM_AXIS"]
             {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ARM}" "BOOM_AXIS"]
             {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :type :linear}] )

     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{BOOM}" "{ARM}")
                     (= :parallel-z (:type %)) )
               constraints-lower) =>
      [{:m1 [["{BOOM}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
        :m2 [["{ARM}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
        :type :parallel-z}
       {:m1 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2 [["{ARM}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :parallel-z}
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
      [{:m1 [["{BOOM}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
        :m2 [["{ARM}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
        :type :in-plane}
       {:m1 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2 [["{ARM}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
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


    (tt/facts
     "about the parsed cad-assembly file arm-to-bucket"

     (tt/fact
      "lower-joint original"
      (filterv #(choose-pair % "{ARM}" "{BUCKET}")
               constraints-orig) =>
      [{:m1 [["{ARM}" "BUCKET_ATTACHMENT_PLANE"]
             {:e [-1480.92 0.0 0.0], :pi 0.0, :q [-1.0 0.0 0.0]}],
        :m2 [["{BUCKET}" "ARM_GUIDE"]
             {:e [-1480.92 0.0 0.0], :pi 0.0, :q [-1.0 0.0 0.0]}],
        :type :planar}
       {:m1 [["{ARM}" "BUCKET_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{BUCKET}" "CENTER_PLANE"]
             {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :planar}
       {:m1 [["{ARM}" "BUCKET_AXIS"]
             {:e [-1480.92 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :m2 [["{BUCKET}" "LOWER_HOLE"]
             {:e [-1480.92 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
        :type :linear}]))



    (tt/facts
     "about the parsed cad-assembly file invariants"

      (tt/fact
      "about the final ASSY link settings"
      (-> kb :link (get "{ASSY}|1") deref) =>
       {:name nil
                 :rdof {:# 0}, :tdof {:# 0},
                     :versor {:rotate [1.0 0.0 0.0 0.0],
                              :xlate [0.0 0.0 0.0]}})

      (tt/fact
      "about the final CARRIAGE link settings"
      (-> kb :link (get "{CARRIAGE}") deref) =>
        {:name "UPPER_BODY"
                 :rdof {:# 3}, :tdof {:# 3},
                      :versor {:rotate [1.0 0.0 0.0 0.0],
                               :xlate [0.0 0.0 0.0]}})

      (tt/fact
      "about the final BOOM link settings"
      (-> kb :link (get "{BOOM}") deref) =>
        {:name "BOOM"
                 :rdof {:# 3},
                  :tdof {:# 3},
                  :versor {:rotate [1.0 0.0 0.0 0.0],
                           :xlate [0.0 0.0 0.0]}})
      (tt/fact
      "about the final ARM link settings"
      (-> kb :link (get "{ARM}") deref) =>
        {:name "ARM_ASSEMBLY_2"
                 :rdof {:# 3},
                 :tdof {:# 3},
                 :versor {:rotate [1.0 0.0 0.0 0.0],
                          :xlate [0.0 0.0 0.0]}} )
      (tt/fact
      "about the final BUCKET link settings"
      (-> kb :link (get "{BUCKET}") deref) =>
        {:name "panel_bucket_assembly"
                 :rdof {:# 3},
                    :tdof {:# 3},
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
       "about the final link settings"
       (->> result-link
            (filter #(contains? #{ "{ASSY}|1" "{CARRIAGE}"
                                   "{BOOM}" "{ARM}"
                                   "{BUCKET}"} (first %)))
            (mapv #(vector (first %) @(second %)))
            (into {})) =>
       {"{ASSY}|1" {:name nil,
                    :rdof {:# 0},
                    :tdof {:# 0},
                    :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}},

        "{CARRIAGE}" {:name "UPPER_BODY",
                      :rdof {:# 0}, :tdof {:# 0, :point [0.0 0.0 0.0]},
                      :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}},

        "{BOOM}" {:name "BOOM",
                  :rdof {:# 0},
                  :tdof {:# 1, :lf [300.68126469514755 2971.920169479622 -1636.3200000000002],
                         :line (ga/line [0.0 2591.01 -1636.32]
                                        [0.6195969498011189 0.7849201359355931 -0.0]),
                         :point [300.68126469514755 2971.920169479622 -1636.3200000000002]},
                  :versor {:rotate [5.979528364703837E-5 0.43823076369396 0.5550646082780856 -0.70700571059492],
                           :xlate [-4408.6225178875175 7453.869697754217 -1993.6805772209982]}},
       "{ARM}" {:name "ARM_ASSEMBLY_2",
                 :rdof {:# 1,
                        :axis [-3250.2573521235577 6540.758298656562 3119.1696376160976]},
                 :tdof {:# 3},
                 :versor {:rotate [0.8344893664574996 0.49345661392998297 0.2452102514947461 0.0],
                          :xlate [-1745.4318404411147 3512.4750314450243 1013.0785487003541]}},
        "{BUCKET}" {:name "panel_bucket_assembly",
                    :rdof {:# 3}, :tdof {:# 3},
                    :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}} } )


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
