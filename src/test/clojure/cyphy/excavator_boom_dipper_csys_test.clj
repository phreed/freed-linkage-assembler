
(ns cyphy.excavator-boom-dipper-csys-test
  (:require [midje.sweet :as t]]
            [isis.geom.cyphy
             [cyphy-zip :as cyphy]
             [cad-stax :as stax]]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-con]
            [isis.geom.machine.misc :as misc]

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


(t/defchecker ref->checker
  "A checker that allows the names of references to be ignored."
  [expected]
  (t/checker [actual]
             (let [actual-deref (clojure.walk/postwalk
                                 #(if (misc/reference? %) [:ref @%] %) actual)]
               (= actual-deref expected)
               #_(if (= actual-deref expected) true
                   (do
                     (clojure.pprint/pprint ["Actual result:" actual-deref])
                     (clojure.pprint/pprint ["Expected result:" expected])
                     )))))

(with-open [fis (-> "excavator/excavator_boom_dipper_csys.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/knowledge-via-input-stream fis)
        constraints (:constraint kb)
        exp-constraints (meta-con/expand-collection constraints)

        ;;  _ (pp/pprint ["exp-con:" exp-constraints])

        constraint-checker
        (ref->checker
         ' [{:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "TOP"]
              {:e [1.0 0.0 0.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "TOP"]
              {:e [1.0 0.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "RIGHT"]
              {:e [0.0 1.0 0.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "RIGHT"]
              {:e [0.0 1.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "FRONT"]
              {:e [0.0 0.0 1.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "FRONT"]
              {:e [0.0 0.0 1.0]}]}
            {:type :csys,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS"]
              {:e [-8625.71 4720.65 600.0], :q [0.0 0.0 1.0], :pi 1.3}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS"]
              {:e [3455.57 5.0 302.5], :q [1.0 0.0 0.0], :pi 1.0}]}])


        expanded-constraint-checker
        (ref->checker
         ' [{:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "TOP"]
              {:e [1.0 0.0 0.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "TOP"]
              {:e [1.0 0.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "RIGHT"]
              {:e [0.0 1.0 0.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "RIGHT"]
              {:e [0.0 1.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "FRONT"]
              {:e [0.0 0.0 1.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "FRONT"]
              {:e [0.0 0.0 1.0]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-origin"]
              {:e [-8625.71 4720.65 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-origin"]
              {:e [3455.57 5.0 302.5]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-3x"]
              {:e [-8802.045575687742 4477.944901687515 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-3x"]
              {:e [3755.57 5.0 302.5]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-4y"]
              {:e [-8302.10320225002 4485.53589908301 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-4y"]
              {:e [3455.57 -395.0 302.5]}]}])


        link-checker
        (ref->checker
         ' {"{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"
            [:ref
             {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
              :tdof {:# 3},
              :rdof {:# 3}}],
            "{51f63ec8-cde2-4ac0-886f-7f9389faad04}"
            [:ref
             {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
              :tdof {:# 3},
              :rdof {:# 3}}],
            "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"
            [:ref
             {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
              :tdof {:# 0},
              :rdof {:# 0}}]})


        link-checker-2
        (ref->checker
         ' {"{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"
            [:ref
             {:versor
              {:xlate [2204.590945944376 -9748.074429784388 902.4999999999989],
               :rotate
               [2.779890061746713E-17
                -0.45399049973954625
                0.8910065241883681
                5.455841439333469E-17]},
              :tdof {:# 0, :point [3455.5699999999997 5.0 302.5]},
              :rdof {:# 0}}],
            "{51f63ec8-cde2-4ac0-886f-7f9389faad04}"
            [:ref
             {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
              :tdof {:# 0, :point [1.0 0.0 0.0]},
              :rdof {:# 0}}],
            "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"
            [:ref
             {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
              :tdof {:# 0},
              :rdof {:# 0}}]} )


        invar-checker
        (ref->checker
         ' {:loc [:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}],
            :dir [:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}],
            :twist [:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]})


        invar-checker-2
        (ref->checker
         ' {:loc
            [:ref
             #{["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"]
               ["{51f63ec8-cde2-4ac0-886f-7f9389faad04}"]
               ["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}],
            :dir
            [:ref
             #{["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"]
               ["{51f63ec8-cde2-4ac0-886f-7f9389faad04}"]
               ["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}],
            :twist
            [:ref
             #{["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"]
               ["{51f63ec8-cde2-4ac0-886f-7f9389faad04}"]
               ["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]})


        success-checker
        (ref->checker
         ' [{:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "TOP"]
              {:e [1.0 0.0 0.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "TOP"]
              {:e [1.0 0.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "RIGHT"]
              {:e [0.0 1.0 0.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "RIGHT"]
              {:e [0.0 1.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "FRONT"]
              {:e [0.0 0.0 1.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "FRONT"]
              {:e [0.0 0.0 1.0]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-origin"]
              {:e [-8625.71 4720.65 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-origin"]
              {:e [3455.57 5.0 302.5]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-3x"]
              {:e [-8802.045575687742 4477.944901687515 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-3x"]
              {:e [3755.57 5.0 302.5]}]}
            {:type :coincident,
             :m1
             [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-4y"]
              {:e [-8302.10320225002 4485.53589908301 600.0]}],
             :m2
             [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-4y"]
              {:e [3455.57 -395.0 302.5]}]}])


        failure-checker (ref->checker '[])
        ]


    (t/facts "about the parsed cad-assembly file with :csys"
             (t/fact "about the constraints" constraints => constraint-checker)
             (t/fact "about the initial link settings" (:link kb) => link-checker)
             (t/fact "about the base link id" (:base kb) => "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1")
             (t/fact "about the initial marker invariants" (:invar kb) => invar-checker)


             (t/fact "about the expanded constraints" exp-constraints => expanded-constraint-checker))


    (let [result (position-analysis kb exp-constraints)
          [success? result-kb result-success result-failure] result
          {result-mark :invar result-link :link} result-kb ]

      ;; (pp/pprint result-success)
      ;; (pp/pprint result-link)
      (t/facts "about results of linkage-assembly"
               (t/fact "about the mark result" result-mark => invar-checker-2)
               (t/fact "about the link result" result-link => link-checker-2)
               (t/fact "about the success result" result-success => success-checker)
               (t/fact "about the failure result" result-failure => failure-checker) )

      #_(with-open [fis (-> "excavator/excavator_boom_dipper_csys.xml"
                            jio/resource jio/input-stream)
                    fos (-> "/tmp/excavator_boom_dipper_csys_aug.xml"
                            jio/output-stream)]

          (cyphy/update-cad-assembly-using-knowledge fis fos kb) ) ) ))


