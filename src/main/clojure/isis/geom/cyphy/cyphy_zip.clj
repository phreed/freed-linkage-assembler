(ns isis.geom.cyphy.cyphy-zip
  "Manipulating the CyPhy2CAD produced CADAssembly.xml file."
  (:require [isis.geom.machine.misc :as misc]

            [clojure.java.io :as jio]
            [clojure.zip :as zip]
            [clojure.data.xml :as xml]
            [clojure.data.zip.xml :as zx]  ))

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

(def ^{:private true} constraint-type-map
  "A mapping between the types specified in the xml and the type required."
  {"SURFACE" :planar
   "POINT" :coincident
   "PLANE" :planar
   "AXIS" :linear
   "LINE" :linear
   "CSYS" :csys })

(defn- extract-constraints-for-single-link
  ""
  [constraint]
  (let [pair-list (zx/xml-> constraint :Pair)]
    (for [pair pair-list]
      (let [
            feature-pair (zx/xml-> pair :ConstraintFeature)
            c-type (zx/attr pair :FeatureGeometryType)

            a-feat (first feature-pair)
            a-link-name (zx/attr a-feat :ComponentID)
            a-proper-name (zx/attr a-feat :FeatureName)
            a-marker (reform-marker (zx/xml1-> a-feat :GeometryMarker))

            b-feat (second feature-pair)
            b-link-name (zx/attr b-feat :ComponentID)
            b-proper-name (zx/attr b-feat :FeatureName)
            b-marker (reform-marker (zx/xml1-> b-feat :GeometryMarker))
            ]
        {:type (get constraint-type-map c-type "UNKNOWN")
         :m1 [[a-link-name a-proper-name] a-marker]
         :m2 [[b-link-name b-proper-name] b-marker]} ))))


(defn- extract-constraints-for-all-links
  "Extract constraints for all children of the top link."
  [asm-link]
  (loop [result [], component-links (zx/xml-> asm-link :CADComponent :Constraint)]
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
         (for [ link (zx/xml-> asm-link :CADComponent) ]
           [(zx/attr link :ComponentID)
            (ref {:tdof {:# 3} :rdof {:# 3}
                  :versor {:xlate [0.0 0.0 0.0]
                           :rotate [1.0 0.0 0.0 0.0]}})])
         [base-link-name
          (ref {:tdof {:# 0} :rdof {:# 0}
                :versor {:xlate [0.0 0.0 0.0]
                         :rotate [1.0 0.0 0.0 0.0]}})] )))

(defn cad-assembly->knowledge
  "Extract the constraints, links and others from a Cyph2Cad cad-assembly.xml input."
  [zip-root]
   (let [asm-link (zx/xml1-> zip-root :Assembly :CADComponent)
        base-link-id (zx/attr asm-link :ComponentID)
        link-map (extract-link-map base-link-id asm-link)
        constraint-list (extract-constraints-for-all-links asm-link)]
     {:constraint constraint-list
      :link link-map
      :base base-link-id
      :invar {:loc (ref #{[base-link-id]})
             :dir (ref #{[base-link-id]})
             :twist (ref #{[base-link-id]})} } ))


(defn kb-from-cyphy-input-stream
  "Given an input-stream to a cyphy-assembly file
  extract the knowledge about the assembly."
  [is]
    (cad-assembly->knowledge
     (-> is xml/parse zip/xml-zip)))

(defn kb-from-cyphy-file
  "Given an input-file-path to a cyphy-assembly file
  extract the knowledge about the assembly."
  [file-path]
  (with-open [is (-> file-path jio/input-stream)]
      (kb-from-cyphy-input-stream is)))
