(ns xml.cyphy-cad-test
  (:require [isis.geom.lang.cyphy-cad-stax :as cyphy]
            [expectations :refer :all]
            [clojure.java.io :as jio]
            [clojure.pprint :as pp]
            [isis.geom.machine.misc :as misc]

            [isis.geom.analysis
             [position-analysis :refer [position-analysis]]]

            [isis.geom.machine.misc :as misc]
            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-slice]
             [helical-slice]
             [in-line-slice]
             [in-plane-slice]
             [offset-x-slice]
             [offset-z-slice]
             [parallel-z-slice]]))

(defn- ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) [:ref @%] %) form))

(with-open [fis (-> "excavator/cad_assembly_boom_dipper_csys.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/extract-knowledge-from-cad-assembly fis)
        constraints (:constraint kb)]
    (expect
     '[{:type :coincident,
        :m1 [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "TOP"] {:e [1.0 0.0 0.0]}],
        :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "TOP"] {:e [1.0 0.0 0.0]}]}
       {:type :coincident,
        :m1 [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "RIGHT"] {:e [0.0 1.0 0.0]}],
        :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "RIGHT"] {:e [0.0 1.0 0.0]}]}
       {:type :coincident,
        :m1 [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "FRONT"] {:e [0.0 0.0 1.0]}],
        :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "FRONT"] {:e [0.0 0.0 1.0]}]}
       {:type :coincident,
        :m1 [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-origin"]
             {:e [-8625.71 4720.65 600.0]}],
        :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-origin"]
             {:e [3455.57 5.0 302.5]}]}
       {:type :coincident,
        :m1 [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-3x"]
             {:e [-8447.17815357595 4961.744130605494 600.0]}],
        :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-3x"]
             {:e [3363.474630524797 -280.51434801289025 302.5]}]}
       {:type :coincident,
        :m1 [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-4y"]
             {:e [-8947.168840807326 4958.692461898733 600.0]}],
        :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-4y"]
             {:e [3836.2557973505204 -117.7938259669377 302.5]}]}],
     constraints)

    (expect "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" (:base kb))

    #_(expect
     '{"{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"
       [:ref
        {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
         :tdof {:# 3} :rdof {:# 3}}],
       "{51f63ec8-cde2-4ac0-886f-7f9389faad04}"
       [:ref
        {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
         :tdof {:# 3} :rdof {:# 3}}],
       "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"
       [:ref
        {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
         :tdof {:# 0} :rdof {:# 0}}]}
     (ref->str (:link kb)))


    (expect
     '{:loc [:ref  #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]
       :z [:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]
       :x [:ref  #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]}
     (ref->str (:mark kb)))

    (let [mark-pattern
          '{:loc [:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]
            :z [:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]
            :x [:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}]}


          link-pattern
          '{"{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"
            [:ref
             {:versor
              {:xlate [-6226.578982993215 1720.6341386564595 -297.5000000000001],
               :rotate [0.16197067860374603 0.0 0.0 -0.9867955711659037]},
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
              :rdof {:# 0}}]}

          success-pattern
          '[{:type :coincident,
             :m1 [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "TOP"] {:e [1.0 0.0 0.0]}],
             :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "TOP"] {:e [1.0 0.0 0.0]}]}
            {:type :coincident,
             :m1 [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "RIGHT"] {:e [0.0 1.0 0.0]}],
             :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "RIGHT"] {:e [0.0 1.0 0.0]}]}
            {:type :coincident,
             :m1 [["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1" "FRONT"] {:e [0.0 0.0 1.0]}],
             :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "FRONT"] {:e [0.0 0.0 1.0]}]}
            {:type :coincident,
             :m1 [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-origin"]
                  {:e [-8625.71 4720.65 600.0]}],
             :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-origin"]
                  {:e [3455.57 5.0 302.5]}]}
            {:type :coincident,
             :m1 [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-3x"]
                  {:e [-8447.17815357595 4961.744130605494 600.0]}],
             :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-3x"]
                  {:e [3363.474630524797 -280.51434801289025 302.5]}]}
            {:type :coincident,
             :m1 [["{c1fb29d9-0a81-423c-bc8f-459735cb4db3}" "ARM_CSYS-4y"]
                  {:e [-8947.168840807326 4958.692461898733 600.0]}],
             :m2 [["{51f63ec8-cde2-4ac0-886f-7f9389faad04}" "BOOM_CSYS-4y"]
                  {:e [3836.2557973505204 -117.7938259669377 302.5]}]}]

          failure-pattern []
          ]
      (let [[success? result-kb result-success result-failure] (position-analysis kb constraints)
            {result-mark :mark result-link :link} (ref->str result-kb)]
        (expect mark-pattern result-mark)
        (expect link-pattern result-link)
        (expect success-pattern result-success)
        (expect failure-pattern result-failure)) )))

;;    (pp/pprint (ref->str result-link))

(with-open [fos (-> "cad_assembly_boom_dipper_csys_aug.xml"
                    jio/output-stream)]
  ;; (let [kb (cyphy/extract-knowledge-from-cad-assembly fis)
  ;;      constraints (:constraint kb)]

(cyphy/write-cad-assembly-using-knowledge fos nil)
(with-open [fis (-> "cad_assembly_boom_dipper_csys_aug.xml"
                    jio/input-stream)]
(cyphy/read-cad-assembly-using-knowledge fis nil)))
