(ns xml.stax-test
  "test the xml stax java interoperability."

  (:require [expectations :refer :all]
            [clojure.java.io :as jio]
            [isis.geom.machine.tolerance :as tol])
  (:import (javax.xml.stream  XMLInputFactory events.XMLEvent XMLStreamConstants)
           (javax.xml.namespace QName)
           (org.codehaus.stax2 XMLInputFactory2 XMLStreamReader2)
           (com.fasterxml.aalto.stax.InputFactoryImpl)))


(defn- parse-numeric
  "xml represents numeric values as strings.
  This converts them into numbers."
  [string]
  (cond (nil? string) 0.0
        :else (Double/parseDouble string)))

(def ^{:private true} constraint-type-map
  "A mapping between the types specified in the xml and the type required."
  {'"SURFACE" :planar, '"POINT" :coincident, '"PLANE" :planar})


(with-open [fis (-> "excavator/cad_assembly_boom_dipper.xml"
                    jio/resource jio/input-stream)]
  (let [factory (XMLInputFactory/newInstance)
        ;(XMLInputFactory/newFactory "com.fasterxml.aalto.stax.InputFactoryImpl" nil)
        _ (.setProperty factory XMLInputFactory/IS_COALESCING true)
        reader (.createXMLEventReader factory fis)]
    (loop [reader reader
           kb nil
           state :start
           link-id ""
           constraint nil]
      (if-not (.hasNext reader)
        (do
          kb )

        (let [event (.nextEvent reader)
              event-type (.getEventType event)]
          (condp = event-type
            XMLStreamConstants/START_ELEMENT
            (do
              (println "start element " (.getName event))
              (case (.toString (.getName event))
                "Assemblies" (recur reader kb :root link-id constraint)
                "Assembly" (recur reader kb :assy link-id constraint)
                "CADComponent"
                (case state
                  :assy
                  (let [base-link-id (.getAttributeByName event (QName. "ComponentID"))]
                    (recur reader
                           {:constraint {}
                            :base base-link-id
                            :link {base-link-id
                                   (ref {:tdof {:# 0} :rdof {:# 0}
                                         :versor {:xlate [0.0 0.0 0.0]
                                                  :rotate [1.0 0.0 0.0 0.0]}})}
                            :mark {:loc (ref #{[base-link-id]})
                                   :z (ref #{[base-link-id]})
                                   :x (ref #{[base-link-id]})}}
                           :base base-link-id constraint))

                  :base
                  (let [comp-link-id (.getAttributeByName event (QName. "ComponentID"))]
                    (recur reader
                           (assoc-in kb [:link comp-link-id]
                                     (ref {:tdof {:# 3} :rdof {:# 3}
                                           :versor {:xlate [0.0 0.0 0.0]
                                                    :rotate [1.0 0.0 0.0 0.0]}}))
                           :comp comp-link-id constraint)))

                "Constraint" (recur reader kb :constraint link-id constraint)
                "Pair" (let [c-type (.getAttributeByName event (QName. "FeatureGeometryType"))]
                         (recur reader kb :pair link-id
                                {:type (get constraint-type-map c-type "UNKNOWN")}))

                "ConstraintFeature"
                (let [link-name (.getAttributeByName event (QName. "ComponentID"))
                      proper-name (.getAttributeByName event (QName. "FeatureName"))]
                  (case state
                    :pair
                    (recur reader kb :feat-m1 link-id
                           (assoc constraint :m1 [[link-name proper-name] nil]))
                    :feat-m1
                    (recur reader kb :feat-m2 link-id
                           (assoc constraint :m2 [[link-name proper-name] nil]))))

                "GeometryMarker"
                (let [
                      x (.getAttributeByName event (QName. "x"))
                      y (.getAttributeByName event (QName. "y"))
                      z (.getAttributeByName event (QName. "z"))

                      e [(parse-numeric (.getValue x))
                         (parse-numeric (.getValue y))
                         (parse-numeric (.getValue z))]

                      i (.getAttributeByName event (QName. "i"))
                      j (.getAttributeByName event (QName. "j"))
                      k (.getAttributeByName event (QName. "k"))
                      pi (.getAttributeByName event (QName. "pi"))

                      q [(parse-numeric (.getValue i))
                         (parse-numeric (.getValue j))
                         (parse-numeric (.getValue k))] ]
                  (case state
                    :feat-m1
                    (recur reader kb :feat-m1 link-id
                           (assoc-in constraint [:m1 1] {:e e}))
                    :feat-m2
                    (recur reader kb :feat-m2 link-id
                           (assoc-in constraint [:m2 1] {:e e}))))

                (recur reader kb :default link-id constraint) ))


            XMLStreamConstants/END_ELEMENT
            (do
              (println "end element " (.toString (.getName event)))
              (recur reader kb state link-id constraint))

            XMLStreamConstants/ATTRIBUTE
            (do
              (println "attribute " (.toString (.getName event)))
              (recur reader kb state link-id constraint))

            XMLStreamConstants/CDATA
            (do
              (println "cdata " (.toString (.getName event)))
              (recur reader kb state link-id constraint))

            XMLStreamConstants/CHARACTERS
            (do
              (println "chars " (.toString (.getData event)))
              (recur reader kb state link-id constraint))

            XMLStreamConstants/DTD
            (do
              (println "dtd " (.toString (.getName event)))
              (recur reader kb state link-id constraint))

            XMLStreamConstants/START_DOCUMENT
            (do
              (println "start doc " (.toString event))
              (recur reader kb :doc link-id constraint))

            XMLStreamConstants/END_DOCUMENT
            (do
              (println "end doc " (.toString event))
              kb)

            XMLStreamConstants/ENTITY_DECLARATION
            (do
              (println "entity " (.toString (.getLocalName event)))
              (recur reader kb state link-id constraint))

            XMLStreamConstants/NAMESPACE
            (do
              (println "namespace " (.toString (.getName event)))
              (recur reader kb state link-id constraint))

            XMLStreamConstants/NOTATION_DECLARATION
            (do
              (println "notation " (.toString (.getName event)))
              (recur reader kb state link-id constraint))

            XMLStreamConstants/PROCESSING_INSTRUCTION
            (do
              (println "processing " (.toString (.getName event)))
              (recur reader kb state link-id constraint))

            (do
              (println "default " (.toString event))
              (recur reader kb state link-id constraint)) ))))))

