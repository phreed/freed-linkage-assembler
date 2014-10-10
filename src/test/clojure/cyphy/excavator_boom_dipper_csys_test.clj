(ns cyphy.excavator-boom-dipper-csys-test
  (:require [midje.sweet :as tt]
            [isis.geom.cyphy
             [cyphy-zip :as cyphy]
             [cad-stax :as stax]]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-con]
            [isis.geom.model.lower-joint :as lower-con]
            [isis.geom.machine.misc :as misc]
            [isis.geom.algebra [geobj :as ga]]

            [isis.geom.analysis
             [position-analysis :refer [position-analysis]]]

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


(with-open [fis (-> "excavator/excavator_boom_dipper_csys.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/knowledge-via-input-stream fis)
        raw-constraints (:constraint kb)
        meta-constraints (meta-con/expand-collection raw-constraints)
        lower-constraints (lower-con/expand-collection meta-constraints)]

    ;;  _ (pp/pprint ["exp-con:" exp-constraints])

    (tt/facts
     "about the parsed cad-assembly file with :csys"

     (tt/fact
      "about the constraints" raw-constraints =>
      [{:m1 [["{CARRIAGE}" "FRONT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_FRONT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :planar}
       {:m1 [["{CARRIAGE}" "TOP"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_TOP"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :planar}
       {:m1 [["{CARRIAGE}" "RIGHT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_RIGHT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :planar}
       {:m1 [["{BOOM}" "ARM_CSYS"]  {:e [-8625.71 4720.65 600.0], :pi 1.3, :q [0.0 0.0 1.0]}],
        :m2 [["{CARRIAGE}" "BOOM_CSYS"]  {:e [3455.57 5.0 302.5], :pi 1.0, :q [1.0 0.0 0.0]}],
        :type :csys}] )


     (tt/fact
      "about the expanded :csys constraint"  meta-constraints =>
      [{:m1 [["{CARRIAGE}" "FRONT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_FRONT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :planar}
       {:m1 [["{CARRIAGE}" "TOP"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_TOP"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :planar}
       {:m1 [["{CARRIAGE}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :planar}
       {:m1 [["{BOOM}" "ARM_CSYS-origin"] {:e [-8625.71 4720.65 600.0]}],
        :m2 [["{CARRIAGE}" "BOOM_CSYS-origin"] {:e [3455.57 5.0 302.5]}],
        :type :coincident}
       {:m1 [["{BOOM}" "ARM_CSYS-3x"] {:e [-8802.045575687742 4477.944901687515 600.0]}],
        :m2 [["{CARRIAGE}" "BOOM_CSYS-3x"] {:e [3755.57 5.0 302.5]}],
        :type :coincident}
       {:m1 [["{BOOM}" "ARM_CSYS-4y"] {:e [-8302.10320225002 4485.53589908301 600.0]}],
        :m2 [["{CARRIAGE}" "BOOM_CSYS-4y"] {:e [3455.57 -395.0 302.5]}],
        :type :coincident}] )

     (tt/fact
      "about the expanded :planar constraints"  lower-constraints =>
      (tt/contains
      [{:m1 [["{CARRIAGE}" "FRONT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_FRONT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :in-plane}
       {:m1 [["{CARRIAGE}" "FRONT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_FRONT"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :parallel-z}
       {:m1 [["{CARRIAGE}" "TOP"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_TOP"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :in-plane}
       {:m1 [["{CARRIAGE}" "TOP"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_TOP"]  {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :parallel-z}
       {:m1 [["{CARRIAGE}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :in-plane}
       {:m1 [["{CARRIAGE}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :m2 [["{ASSY}|1" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
        :type :parallel-z} ]))

     (tt/fact
      "about retaining the expanded :cys constraints"  lower-constraints =>
      (tt/contains
       [{:m1 [["{BOOM}" "ARM_CSYS-origin"] {:e [-8625.71 4720.65 600.0]}],
        :m2 [["{CARRIAGE}" "BOOM_CSYS-origin"] {:e [3455.57 5.0 302.5]}],
        :type :coincident}
       {:m1 [["{BOOM}" "ARM_CSYS-3x"] {:e [-8802.045575687742 4477.944901687515 600.0]}],
        :m2 [["{CARRIAGE}" "BOOM_CSYS-3x"] {:e [3755.57 5.0 302.5]}],
        :type :coincident}
       {:m1 [["{BOOM}" "ARM_CSYS-4y"] {:e [-8302.10320225002 4485.53589908301 600.0]}],
        :m2 [["{CARRIAGE}" "BOOM_CSYS-4y"] {:e [3455.57 -395.0 302.5]}],
        :type :coincident}] ))

     (tt/facts
      "about the initial link settings"
      (tt/fact "components" (-> kb :link keys set) =>
               #{"{ASSY}|1" "{CARRIAGE}" "{BOOM}"})

      (tt/fact "boom" (-> kb :link (get "{BOOM}") deref) =>
               {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                :tdof {:# 3},
                :rdof {:# 3}})
      (tt/fact "boom" (-> kb :link (get "{CARRIAGE}") deref) =>
               {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                :tdof {:# 3},
                :rdof {:# 3}})
      (tt/fact "boom" (-> kb :link (get "{ASSY}|1") deref) =>
               {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                :tdof {:# 0},
                :rdof {:# 0}}))

     (tt/fact  "about the base link id" (:base kb) => "{ASSY}|1")

     (tt/facts
        "about the initial invariant markings"
        (tt/fact "types:" (-> kb :invar keys set) =>
                 #{:loc :dir :twist})
        (tt/fact "loc(ation)" (-> kb :invar :loc deref) => #{["{ASSY}|1"]} )
        (tt/fact "dir(ection)" (-> kb :invar :dir deref) => #{["{ASSY}|1"]} )
        (tt/fact "twist" (-> kb :invar :twist deref) => #{["{ASSY}|1"]} ))


     (let [result (position-analysis kb lower-constraints)
           [success? result-kb result-success result-failure] result
           {result-mark :invar result-link :link} result-kb ]

       ;; (pp/pprint result-success)
       ;; (pp/pprint result-link)

       (tt/facts
        "about the final link settings"
        (tt/fact "components" (-> result-link keys set) =>
                 #{"{ASSY}|1" "{CARRIAGE}" "{BOOM}"})

        (tt/fact "boom" (-> result-link (get "{BOOM}") deref) =>
                 {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                  :tdof {:# 3},
                  :rdof {:# 3}})
        (tt/fact "carriage" (-> result-link (get "{CARRIAGE}") deref) =>
                 {:rdof {:# 3},
                  :tdof {:# 2, :lf [0.0 0.0 0.0],
                         :plane (ga/plane [0.0 0.0 0.0] [0.0 0.0 1.0]),
                         :point [0.0 0.0 0.0]},
                  :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}})
        (tt/fact "assembly" (-> result-link (get "{ASSY}|1") deref) =>
                 {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                  :tdof {:# 0},
                  :rdof {:# 0}}))

       (tt/facts
        "about the invariant markings"
        (tt/fact "types:" (-> result-mark keys set) =>
                 #{:loc :dir :twist})
        (tt/fact "loc(ation)" (-> result-mark :loc deref) =>
                 #{["{ASSY}|1"]} )
        (tt/fact "dir(ection)" (-> result-mark :dir deref) =>
                 #{["{ASSY}|1"]} )
        (tt/fact "twist" (-> result-mark :twist deref) =>
                 #{["{ASSY}|1"]} ))

       (tt/fact
        "about the success result" result-success =>
        [{:type :coincident,
          :m1 [["{ASSY}|1" "TOP"] {:e [1.0 0.0 0.0]}],
          :m2 [["{CARRIAGE}" "TOP"] {:e [1.0 0.0 0.0]}]}
         {:type :coincident,
          :m1 [["{ASSY}|1" "RIGHT"] {:e [0.0 1.0 0.0]}],
          :m2 [["{CARRIAGE}" "RIGHT"] {:e [0.0 1.0 0.0]}]}
         {:type :coincident,
          :m1 [["{ASSY}|1" "FRONT"] {:e [0.0 0.0 1.0]}],
          :m2 [["{CARRIAGE}" "FRONT"] {:e [0.0 0.0 1.0]}]}
         {:type :coincident,
          :m1 [["{BOOM}" "ARM_CSYS-origin"] {:e [-8625.71 4720.65 600.0]}],
          :m2 [["{CARRIAGE}" "BOOM_CSYS-origin"] {:e [3455.57 5.0 302.5]}]}
         {:type :coincident,
          :m1 [["{BOOM}" "ARM_CSYS-3x"]
           {:e [-8802.045575687742 4477.944901687515 600.0]}],
          :m2 [["{CARRIAGE}" "BOOM_CSYS-3x"] {:e [3755.57 5.0 302.5]}]}
         {:type :coincident,
          :m1 [["{BOOM}" "ARM_CSYS-4y"]
           {:e [-8302.10320225002 4485.53589908301 600.0]}],
          :m2 [["{CARRIAGE}" "BOOM_CSYS-4y"] {:e [3455.57 -395.0 302.5]}]}])


       (tt/fact "about the failure result" result-failure => '[])



       #_(with-open [fis (-> "excavator/excavator_boom_dipper_csys.xml"
                             jio/resource jio/input-stream)
                     fos (-> "/tmp/excavator_boom_dipper_csys_aug.xml"
                             jio/output-stream)]

           (cyphy/update-cad-assembly-using-knowledge fis fos kb) ) ) )) )


