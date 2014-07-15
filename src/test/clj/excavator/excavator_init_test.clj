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
            [clojure.data.zip.xml :as zml]))

(defn- parse-numeric
  "xml represents numeric values as strings.
  This converts them into numbers."
  [string]
  (Double/parseDouble string))

(defn- reform-marker
  "The xml file forms the marker hash a bit differently."
  [input]
  (let [orig (:attrs (first input))]
    {:e [(parse-numeric (:x orig))
         (parse-numeric (:y orig))
         (parse-numeric (:z orig))] }))


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
        {:type c-type
         :m1 [[a-link-name a-proper-name] a-marker]
         :m2 [[b-link-name b-proper-name] b-marker]} ))))


(defn- extract-constraints-for-all-links
  "Extract constraints for all children of the top link."
  [asm-link]
  (into []
        (for [ link-constraints (zml/xml-> asm-link  :CADComponent :Constraint) ]
          (extract-constraints-for-single-link link-constraints)) ))


(defn- extract-link-map
  "Build a map with keys being the link names."
  [link-list]
  (into {} (for [ link (zml/xml-> link-list :CADComponent) ]
             [(keyword (zml/attr link :ComponentID)) (zml/text link)])))

(def excavator-graph
  (ref
   (let [root (->
               "excavator/cad_assembly_boom_dipper.xml"
               jio/resource jio/input-stream xml/parse zip/xml-zip)]
     (let [asm-link (zml/xml1-> root :Assembly :CADComponent)]
       (let [base-link-id (zml/attr asm-link :ComponentID)
             link-map (extract-link-map asm-link)
             constraint-list (extract-constraints-for-all-links asm-link)]
         {:constraints constraint-list
          :links link-map
          :base base-link-id
          :mark {} } )))))

excavator-graph



(defn ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) [:ref @%] %) form))


#_(expect
   '(
     {:type :coincident
      :m1 [[ground g2] {:e [5.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
      :m2 [[brick b2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
     {:type :coincident
      :m1 [[brick b4] {:e [1.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
      :m2 [[cap c4] {:e [-1.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
     {:type :coincident
      :m1 [[ground g1] {:e [2.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
      :m2 [[brick b1] {:e [0.0 0.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
     {:type :coincident
      :m1 [[brick b3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]
      :m2 [[cap c3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]}
     {:type :coincident
      :m1 [[brick b3] {:e [0.0 0.0 4.0], :z [nil nil nil], :x [nil nil nil]}]
      :m2 [[ground g3] {:e [2.0 4.0 0.0], :z [nil nil nil], :x [nil nil nil]}]}
     {:type :coincident
      :m1 [[cap c2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]
      :m2 [[brick b2] {:e [0.0 3.0 0.0], :z [nil nil nil], :x [nil nil nil]}]})
   @excavator-graph)


