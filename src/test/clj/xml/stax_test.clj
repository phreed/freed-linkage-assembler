(ns xml.stax-test
  "test the xml stax java interoperability."

  (:require [expectations :refer :all]
            [clojure.java.io :as jio]
            [clojure.pprint :as pp]
            [isis.geom.machine.geobj :as ga]
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

(defn- parse-numeric-attribute
  "Extract a numeric attribute from an event."
  [event proper-name]
  (parse-numeric (.getValue (.getAttributeByName event (QName. proper-name)))))

(defn- parse-string-attribute
  "Extract a texual attribute from an event."
  [event proper-name]
  (.getValue (.getAttributeByName event (QName. proper-name))))


(defn- inc-marker
  "Select the keyword for the next marker."
  [marker-key]
  (case marker-key
    nil :m1, :m1 :m2, :m2 :m3, :m3 :m4, :m4 :m5))


(def constraint-type-map
  "A mapping between the types specified in the xml and the type required."
  '{"SURFACE" :planar,
    "POINT" :coincident,
    "PLANE" :planar,
    "CSYS" :csys})

(println (get constraint-type-map "CSYS"))

(defn- update-kb-grounded
  "When a component is fixed to the ground it gets
  a standard set of constraints."
  [kb wip]
  (let [base-link-id (:base-link-id wip)
        fixed-link-id (:link wip)]
    (update-in
     kb
     [:constraint]
     conj
     { :type :coincident
       :m1 [[base-link-id "TOP"] {:e [1.0 0.0 0.0]}]
       :m2 [[fixed-link-id "TOP"] {:e [1.0 0.0 0.0]}]}
     { :type :coincident
       :m1 [[base-link-id "RIGHT"] {:e [0.0 1.0 0.0]}]
       :m2 [[fixed-link-id "RIGHT"] {:e [0.0 1.0 0.0]}]}
     { :type :coincident
       :m1 [[base-link-id "FRONT"] {:e [0.0 0.0 1.0]}]
       :m2 [[fixed-link-id "FRONT"] {:e [0.0 0.0 1.0]}]} )))

(defn- expand-point-constraint
  [kb-constraint wip-constraint]
  (conj kb-constraint
     (assoc-in wip-constraint [:type] :coincident)))


(defn- expand-csys-constraint
  "When a component is placed with a csys it
  can be implemented with three
  standardized coincident constraints.
  These constraints are: origin, point on rotated
  x-axis and rotated z-axis."
  [kb-constraint wip-constraint]
  (let [{c-type :type, m1 :m1, m2 :m2} wip-constraint
        [[m1-link-name m1-proper-name] m1-values] m1
        [[m2-link-name m2-proper-name] m2-values] m2
        {m1-e :e, m1-q :q, m1-pi :pi} m1-values
        {m2-e :e, m2-q :q, m2-pi :pi} m2-values
        m1-quat (ga/axis-pi-angle->quaternion m1-q m1-pi)
        m2-quat (ga/axis-pi-angle->quaternion m2-q m2-pi)

        m1-3x (ga/vec-sum m1-e (ga/quat-sandwich m1-quat [300.0 0.0 0.0]))
        m2-3x (ga/vec-sum m2-e (ga/quat-sandwich m2-quat [300.0 0.0 0.0]))

        m1-4y (ga/vec-sum m1-e (ga/quat-sandwich m1-quat [0.0 400.0 0.0]))
        m2-4y (ga/vec-sum m2-e (ga/quat-sandwich m2-quat [0.0 400.0 0.0]))
        ]

    (conj kb-constraint
          { :type :coincident
            :m1 [[m1-link-name (str m1-proper-name "-origin")] {:e m1-e}]
            :m2 [[m2-link-name (str m2-proper-name "-origin")] {:e m2-e}]}
          { :type :coincident
            :m1 [[m1-link-name (str m1-proper-name "-3x")] {:e m1-3x}]
            :m2 [[m2-link-name (str m2-proper-name "-3x")] {:e m2-3x}]}
          { :type :coincident
            :m1 [[m1-link-name (str m1-proper-name "-4y")] {:e m1-4y}]
            :m2 [[m2-link-name (str m2-proper-name "-4y")] {:e m2-4y}]}
          )))

(defn- update-kb-jointed
  "Mutate the constraints as needed."
  [kb wip]
  (let [constraint (:constraint wip)]
    (case (:type constraint)
      :point (update-in kb [:constraint] #(expand-point-constraint % constraint))
      :csys (update-in kb [:constraint] #(expand-csys-constraint % constraint)))))


(with-open [fis (-> "excavator/cad_assembly_boom_dipper_csys.xml"
                    jio/resource jio/input-stream)]
  (let [factory (XMLInputFactory/newInstance)
        ;(XMLInputFactory/newFactory "com.fasterxml.aalto.stax.InputFactoryImpl" nil)
        _ (.setProperty factory XMLInputFactory/IS_COALESCING true)
        reader (.createXMLEventReader factory fis)]
    (loop [reader reader    ;; the xml reader
           kb nil           ;; the goal : build a knowledge base
           zip [[:top 0]]   ;; a list to where you are
           wip {} ]         ;; misc work-in-progress items
      (if-not (.hasNext reader)
        (do
          (println "you have reached the end of the input file")
          (pp/pprint kb) )

        (let [event (.nextEvent reader)
              event-type (.getEventType event)]
          (condp = event-type

            XMLStreamConstants/START_ELEMENT
            (let [elem-type (keyword (.toString (.getName event)))
                  new-zip (conj zip [elem-type 0])
                  parent-type (first (peek zip))]

              (println "start element " elem-type)
              (pp/pprint zip)
              (pp/pprint wip)

              (case elem-type
                :Assemblies (recur reader kb new-zip wip)
                :Assembly (recur reader kb new-zip wip)
                :CADComponent
                (case (first (peek zip))
                  :Assembly
                  (let [base-link-id (parse-string-attribute event "ComponentID")]
                    (recur reader
                           {:constraint []
                            :base base-link-id
                            :link {base-link-id
                                   (ref {:tdof {:# 0} :rdof {:# 0}
                                         :versor {:xlate [0.0 0.0 0.0]
                                                  :rotate [1.0 0.0 0.0 0.0]}})}
                            :mark {:loc (ref #{[base-link-id]})
                                   :z (ref #{[base-link-id]})
                                   :x (ref #{[base-link-id]})}}
                           new-zip
                           (assoc wip :base-link-id base-link-id)))

                  :CADComponent
                  (let [comp-link-id (parse-string-attribute event "ComponentID")]
                    (recur reader
                           (assoc-in kb [:link comp-link-id]
                                     (ref {:tdof {:# 3} :rdof {:# 3}
                                           :versor {:xlate [0.0 0.0 0.0]
                                                    :rotate [1.0 0.0 0.0 0.0]}}))
                           new-zip
                           (assoc wip :link comp-link-id)))

                  (do
                    (println "you have a hierarchy problem. " wip)
                    kb)  )

                :Constraint
                (recur reader kb new-zip (assoc wip :grounded false))

                :Pair
                (if (:grounded wip)
                  (recur reader kb new-zip wip)
                  (let [c-type (parse-string-attribute event "FeatureGeometryType")]
                    (println " pair : " c-type)

                    (recur reader kb new-zip
                           (assoc wip
                             :active-marker :m1

                             :constraint
                             {:type (get constraint-type-map c-type "UNKNOWN")
                              :m1 nil, :m2 nil} ))))

                ;; If either of the constraint features for the pair
                ;; make reference to the ground then the component is
                ;; presumed to be fixed to the ground and a standard
                ;; set of coincident points can be assumed.
                ;; Any other pairs in the constraint can be ignored.
                :ConstraintFeature
                (if (:grounded wip)
                  (recur reader kb new-zip wip)
                  (let [link-name (parse-string-attribute event "ComponentID")
                        proper-name (parse-string-attribute event "FeatureName")
                        constraint (:constraint wip)
                        marker [[link-name proper-name] nil]
                        active (:active-marker wip)]
                    (pp/pprint marker)
                    (cond (= link-name (:base-link-id wip))
                          (recur reader kb new-zip (assoc wip :grounded true))

                          :else
                          (recur reader kb new-zip
                                 (assoc-in wip [:constraint active] marker)))) )


                :GeometryMarker
                (if (:grounded wip) (recur reader kb new-zip wip)
                  (let [x (parse-numeric-attribute event "x")
                        y (parse-numeric-attribute event "y")
                        z (parse-numeric-attribute event "z")

                        i (parse-numeric-attribute event "i")
                        j (parse-numeric-attribute event "j")
                        k (parse-numeric-attribute event "k")

                        pi (parse-numeric-attribute event "pi")

                        active (:active-marker wip)
                        new-wip (assoc-in wip [:constraint active 1]
                                          {:e [x y z] :q [i j k] :pi pi})]
                    (recur reader kb new-zip
                           (update-in new-wip [:active-marker] inc-marker))))

                (recur reader kb new-zip wip) ))


            XMLStreamConstants/END_ELEMENT
            (let [current-type (first (peek zip))
                  elem-type (keyword (.toString (.getName event)))
                  new-zip (pop zip)
                  new-zip (conj (pop new-zip)
                                (update-in (peek new-zip) [1] inc))]

              (println "end element " (.toString (.getName event)))

              (case elem-type
                ;; The end of a pair indicates that a (set of)
                ;; constraint can be added to the knowledge base.
                :Pair
                (let [new-wip (dissoc wip :active-marker :constraint)]
                  (if (:grounded wip)
                    (recur reader kb new-zip new-wip)
                    (recur reader
                           (update-kb-jointed kb wip)
                           new-zip new-wip) ))

                ;; the end of the constraint signifies the wrapping
                ;; up for the component's constraints.
                :Constraint
                (let [new-wip (dissoc wip :grounded)]
                  (if (:grounded wip)
                    (recur reader
                           (update-kb-grounded kb wip)
                           new-zip new-wip)
                    (recur reader kb new-zip new-wip) ))

                ;; the default
                (recur reader kb new-zip wip)))

            XMLStreamConstants/ATTRIBUTE
            (do
              (println "attribute " (.toString (.getName event)))
              (recur reader kb zip wip))

            XMLStreamConstants/CDATA
            (do
              (println "cdata " (.toString (.getName event)))
              (recur reader kb zip wip))

            XMLStreamConstants/CHARACTERS
            (do
              (println "chars " (.toString (.getData event)))
              (recur reader kb zip wip))

            XMLStreamConstants/DTD
            (do
              (println "dtd " (.toString (.getName event)))
              (recur reader kb zip wip))

            XMLStreamConstants/START_DOCUMENT
            (do
              (println "start doc " (.toString event))
              (recur reader kb zip wip))

            XMLStreamConstants/END_DOCUMENT
            (do
              (println "end doc " (.toString event))
              (recur reader kb zip wip))

            XMLStreamConstants/ENTITY_DECLARATION
            (do
              (println "entity " (.toString (.getLocalName event)))
              (recur reader kb zip wip))

            XMLStreamConstants/NAMESPACE
            (do
              (println "namespace " (.toString (.getName event)))
              (recur reader kb zip wip))

            XMLStreamConstants/NOTATION_DECLARATION
            (do
              (println "notation " (.toString (.getName event)))
              (recur reader kb zip wip))

            XMLStreamConstants/PROCESSING_INSTRUCTION
            (do
              (println "processing " (.toString (.getName event)))
              (recur reader kb zip wip))

            (do
              (println "default " (.toString event))
              (recur reader kb zip wip))

            ))))))
