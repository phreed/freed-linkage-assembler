(ns excavator.excavator-init-test
  "Sample assembly consisting of a boom and a dipper."
  (:require [expectations :refer :all]
            [isis.geom.machine.misc :as misc]
            [isis.geom.lang.cyphy-cad :as cyphy]

            [clojure.java.io :as jio]
            [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip.xml :as zx]

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


(def excavator-graph
  (with-open [is (-> "excavator/cad_assembly_boom_dipper.xml"
                     jio/resource jio/input-stream)]
    (cyphy/graph-from-cyphy-input-stream is)))



(defn ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) [:ref @%] %) form))


(expect
 '[
   {:type :coincident
    :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "FRONT"] {:e [1.0 0.0 0.0]}]
    :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_FRONT"] {:e [1.0 0.0 0.0]}]}
   {:type :coincident
    :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "TOP"] {:e [0.0 1.0 0.0]}]
    :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_TOP"] {:e [0.0 1.0 0.0]}]}
   {:type :coincident
    :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "RIGHT"] {:e [0.0 0.0 1.0]}]
    :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_RIGHT"] {:e [0.0 0.0 1.0]}]}
   {:type :coincident
    :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_2"] {:e [-8649.51 4688.51 0.0]}]
    :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "APNT_2"] {:e [4557.58 679.734 0.0]}]}
   {:type :coincident
    :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_0"] {:e [-8625.71 4720.65 0.0]}]
    :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "APNT_0"] {:e [4545.3 641.665 0.0]}]}
   {:type :coincident
    :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_1"] {:e [-8625.71 4720.65 0.0]}]
    :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "APNT_1"] {:e [4545.3 641.665 0.0]}]}]
 (:constraint excavator-graph))


:base "{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"

(let [graph excavator-graph
      mark-pattern
      '{:loc [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
                     ["{62243423-b7fd-4a10-8a98-86209a6620a4}"]
                     ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}]
        :z [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
                   ["{62243423-b7fd-4a10-8a98-86209a6620a4}"]
                   ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}]
        :x [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
                   ["{62243423-b7fd-4a10-8a98-86209a6620a4}"]
                   ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}]}

      link-pattern
      '{"{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"
        [:ref {:versor {:xlate [0.0 0.0 0.0]
                        :rotate [1.0 0.0 0.0 0.0]}
               :tdof {:# 0}
               :rdof {:# 0}}]

        "{bb160c79-5ba3-4379-a6c1-8603f29079f2}"
        [:ref {:versor {:xlate [0.0 0.0 0.0]
                        :rotate [1.0 0.0 0.0 0.0]}
               :tdof {:# 0, :point [1.0 0.0 0.0]}
               :rdof {:# 0}}]

        "{62243423-b7fd-4a10-8a98-86209a6620a4}"
        [:ref {:versor {:xlate [13207.09 -4008.7760000000003 0.0]
                        :rotate [1.0 0.0 0.0 0.0]}
               :tdof {:# 0 :point [4557.58 679.7339999999999 0.0]}
               :rdof {:# 0}}]}


      success-pattern
      '[
        {:type :coincident
         :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "FRONT"] {:e [1.0 0.0 0.0]}]
         :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_FRONT"] {:e [1.0 0.0 0.0]}]}
        {:type :coincident
         :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "TOP"] {:e [0.0 1.0 0.0]}]
         :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_TOP"] {:e [0.0 1.0 0.0]}]}
        {:type :coincident
         :m1 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "RIGHT"] {:e [0.0 0.0 1.0]}]
         :m2 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "ASM_RIGHT"] {:e [0.0 0.0 1.0]}]}
        {:type :coincident
         :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_2"] {:e [-8649.51 4688.51 0.0]}]
         :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "APNT_2"] {:e [4557.58 679.734 0.0]}]}
        {:type :coincident
         :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_0"] {:e [-8625.71 4720.65 0.0]}]
         :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "APNT_0"] {:e [4545.3 641.665 0.0]}]}
        {:type :coincident
         :m1 [["{62243423-b7fd-4a10-8a98-86209a6620a4}" "APNT_1"] {:e [-8625.71 4720.65 0.0]}]
         :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "APNT_1"] {:e [4545.3 641.665 0.0]}]}]

      failure-pattern
      []

      augmented-pattern
      '#clojure.data.xml.Element
      {:tag :versor
        :attrs {:x 13207.09 :y -4008.7760000000003 :z 0.0
                :i 0.0, :j 0.0, :k 0.0, :pi 0.0}
        :content ()}

      ]
  (let [constraints (:constraint graph)
        kb graph
        [success? result-kb result-success result-failure] (position-analysis kb constraints)
        {result-mark :mark result-link :link} (ref->str result-kb)
        augmented-zipper (zip/xml-zip (:augmented (cyphy/graph-to-cyphy-zipper graph)))
        augmented-sample (zip/node (zx/xml1-> augmented-zipper :Assembly :CADComponent :CADComponent
                     (zx/attr= :Name "BOOM") :versor )) ]
    (expect mark-pattern result-mark)
    (expect link-pattern result-link)
    (expect success-pattern result-success)
    (expect failure-pattern result-failure)
    (expect augmented-pattern augmented-sample)) )



