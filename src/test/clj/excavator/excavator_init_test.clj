(ns excavator.excavator-init-test
  "Sample assembly consisting of a boom and a dipper."
  (:require [expectations :refer :all]
            [isis.geom.machine.misc :as misc]
            [isis.geom.model
             [graph :refer [ port->expand
                             port-pair->make-constraint
                             graph->init-invariants
                             joint-pair->constraint
                             joints->constraints]] ]
            [clojure.java.io :as jio]
            [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip.xml :as zml]

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

(defn- parse-numeric
  "xml represents numeric values as strings.
  This converts them into numbers."
  [string]
  (cond (nil? string) 0.0
        :else (Double/parseDouble string)))

(defn- reform-marker
  "The xml file forms the marker hash a bit differently."
  [input]
  (let [orig (:attrs (first input))]
    {:e [(parse-numeric (:x orig))
         (parse-numeric (:y orig))
         (parse-numeric (:z orig))] }))

(def constraint-type-map
  "A mapping between the types specified in the xml and the type required."
  {'"SURFACE" :planar, '"POINT" :coincident, '"PLANE" :planar})

(defn- extract-constraints-for-single-link
  ""
  [constraint]
  (let [pair-list (zml/xml-> constraint :Pair)]
      (for [pair pair-list]
        (let [
              feature-pair (zml/xml-> pair :ConstraintFeature)
            c-type (zml/attr pair :FeatureGeometryType)

            a-feat (first feature-pair)
            a-link-name (zml/attr a-feat :ComponentID)
            a-proper-name (zml/attr a-feat :FeatureName)
            a-marker (reform-marker (zml/xml1-> a-feat :GeometryMarker))

            b-feat (second feature-pair)
            b-link-name (zml/attr b-feat :ComponentID)
            b-proper-name (zml/attr b-feat :FeatureName)
            b-marker (reform-marker (zml/xml1-> b-feat :GeometryMarker))
            ]
        {:type (get constraint-type-map c-type "UNKNOWN")
         :m1 [[a-link-name a-proper-name] a-marker]
         :m2 [[b-link-name b-proper-name] b-marker]} ))))


(defn- extract-constraints-for-all-links
  "Extract constraints for all children of the top link."
  [asm-link]
  (loop [result [], component-links (zml/xml-> asm-link :CADComponent :Constraint)]
    (cond (empty? component-links)
          (into [] result)

          :else
          (let [link-constraints (first component-links)
                spec-constraints (extract-constraints-for-single-link link-constraints)]
            (recur (concat result spec-constraints) (rest component-links))))))


(defn- extract-link-map
  "Build a map with keys being the link names."
  [base-link-name asm-link]
  (into {}
        (conj
         (for [ link (zml/xml-> asm-link :CADComponent) ]
             [(zml/attr link :ComponentID)
              (ref {:tdof {:# 3} :rdof {:# 3}
                    :versor {:xlate [0.0 0.0 0.0]
                             :rotate [1.0 0.0 0.0 0.0]}})])
         [base-link-name
              (ref {:tdof {:# 0} :rdof {:# 0}
                    :versor {:xlate [0.0 0.0 0.0]
                             :rotate [1.0 0.0 0.0 0.0]}})] )))

(def excavator-graph
  (ref
   (let [root (->
               "excavator/cad_assembly_boom_dipper.xml"
               jio/resource jio/input-stream xml/parse zip/xml-zip)]
     (let [asm-link (zml/xml1-> root :Assembly :CADComponent)]
       (let [base-link-id (zml/attr asm-link :ComponentID)
             link-map (extract-link-map base-link-id asm-link)
             constraint-list (extract-constraints-for-all-links asm-link)]
         {:constraint constraint-list
          :link link-map
          :base base-link-id
          :mark {:loc (ref #{[base-link-id]})
                 :z (ref #{[base-link-id]})
                 :x (ref #{[base-link-id]})} } )))))




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
   (:constraint @excavator-graph))


   :base "{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"

(let [graph @excavator-graph
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
      ]
  (let [constraints (:constraint graph)
        kb graph
        [success? result-kb result-success result-failure] (position-analysis kb constraints)
        {result-mark :mark result-link :link} (ref->str result-kb)]
    (expect mark-pattern result-mark)
    (expect link-pattern result-link)
    (expect success-pattern result-success)
    (expect failure-pattern result-failure)) )


