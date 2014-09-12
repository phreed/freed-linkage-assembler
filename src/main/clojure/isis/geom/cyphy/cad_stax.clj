(ns isis.geom.cyphy.cad-stax
  "Manipulating the CyPhy2CAD produced CADAssembly.xml file using stax."
  (:require
   [isis.geom.machine.geobj :as ga]
   [isis.geom.machine.tolerance :as tol]
   [clojure.pprint :as pp] )
  (:import
   (java.util Date)
   (javax.xml.stream  XMLInputFactory
                      XMLOutputFactory
                      XMLEventFactory
                      events.XMLEvent
                      XMLStreamConstants
                      XMLStreamException)
   (javax.xml.namespace QName)
   ;; (javanet.staxutils IndentingXMLEventWriter)

   (org.codehaus.stax2 XMLInputFactory2
                       XMLStreamReader2
                       XMLStreamWriter2)
   (org.codehaus.staxmate SMOutputFactory
                          SMInputFactory)
   (com.fasterxml.aalto.stax  InputFactoryImpl)))


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
  '{"POINT" :point
    "SURFACE" :planar
    "PLANE" :planar
    "AXIS"  :linear
    "LINE"  :linear
    "CSYS" :csys})


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


(defn- update-kb-jointed
  "Mutate the constraints as needed."
  [kb wip]
  (update-in kb [:constraint] conj (:constraint wip)))


(defn- extract-knowledge-from-cad-assembly-aux
  "Receive a new event with which the knowledge-base (kb) will be updated.
  zip is the ancestry of the current elemnt.
  wip holds information about the current element. "
  [event kb zip wip]
  (let [event-type (.getEventType event)]
    (condp = event-type

      XMLStreamConstants/START_ELEMENT
      (let [elem-type (keyword (.toString (.getName event)))
            new-zip (conj zip [elem-type 0])
            parent-type (first (peek zip))]

        ;; (println "start element " elem-type)
        ;; (pp/pprint zip)
        ;; (pp/pprint wip)

        (case elem-type
          :Assemblies [kb new-zip wip]
          :Assembly [kb new-zip wip]
          :CADComponent
          (case (first (peek zip))
            :Assembly
            (let [base-link-id (parse-string-attribute event "ComponentID")]
              [{:constraint []
                :base base-link-id
                :link {base-link-id
                       (ref {:tdof {:# 0} :rdof {:# 0}
                             :versor {:xlate [0.0 0.0 0.0]
                                      :rotate [1.0 0.0 0.0 0.0]}})}
                :invar {:loc (ref #{[base-link-id]})
                       :dir (ref #{[base-link-id]})
                       :twist (ref #{[base-link-id]})}}
               new-zip
               (assoc wip :base-link-id base-link-id)])

            :CADComponent
            (let [comp-link-id (parse-string-attribute event "ComponentID")]
              [(assoc-in kb [:link comp-link-id]
                         (ref {:tdof {:# 3} :rdof {:# 3}
                               :versor {:xlate [0.0 0.0 0.0]
                                        :rotate [1.0 0.0 0.0 0.0]}}))
               new-zip
               (assoc wip :link comp-link-id)])

            (do
              (println "you have a hierarchy problem. " wip)
              [kb new-zip wip])  )

          :Constraint
          [kb new-zip (assoc wip :grounded false)]

          :Pair
          (if (:grounded wip)
            [kb new-zip wip]
            (let [c-type (parse-string-attribute event "FeatureGeometryType")]
              ;; (pp/pprint c-type)
              (cond (contains? constraint-type-map c-type)
                    [kb new-zip
                     (assoc wip
                       :active-marker :m1

                       :constraint
                       {:type (get constraint-type-map c-type)
                        :m1 nil, :m2 nil} )]

                    :else
                    (do
                      (println "you have an unknown constraint type: " c-type)
                      [kb new-zip wip])) ))


          ;; If either of the constraint features for the pair
          ;; make reference to the ground then the component is
          ;; presumed to be fixed to the ground and a standard
          ;; set of coincident points can be assumed.
          ;; Any other pairs in the constraint can be ignored.
          :ConstraintFeature
          (if (:grounded wip)
            [kb new-zip wip]
            (let [link-name (parse-string-attribute event "ComponentID")
                  proper-name (parse-string-attribute event "FeatureName")
                  constraint (:constraint wip)
                  marker [[link-name proper-name] nil]
                  active (:active-marker wip)]
              (cond (= link-name (:base-link-id wip))
                    [kb new-zip (assoc wip :grounded true)]

                    :else
                    [kb new-zip
                     (assoc-in wip [:constraint active] marker)])) )


          :GeometryMarker
          (if (:grounded wip)
            [kb new-zip wip]
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
              [kb new-zip
               (update-in new-wip [:active-marker] inc-marker)]))

          [kb new-zip wip] ))


      XMLStreamConstants/END_ELEMENT
      (let [current-type (first (peek zip))
            elem-type (keyword (.toString (.getName event)))
            new-zip (pop zip)
            new-zip (conj (pop new-zip)
                          (update-in (peek new-zip) [1] inc))]

        ;; (pp/pprint ["end element " (.toString (.getName event))])

        (case elem-type
          ;; The end of a pair indicates that a (set of)
          ;; constraint can be added to the knowledge base.
          :Pair
          (let [new-wip (dissoc wip :active-marker :constraint)]
            ;; (pp/pprint ["jointed " wip])
            (if (:grounded wip)
              [kb new-zip new-wip]
              [(update-kb-jointed kb wip) new-zip new-wip]))

          ;; the end of the constraint signifies the wrapping
          ;; up for the component's constraints.
          :Constraint
          (let [new-wip (dissoc wip :grounded)]
            (if (:grounded wip)
              [(update-kb-grounded kb wip) new-zip new-wip]
              [kb new-zip new-wip] ) )

          ;; the default
          [kb new-zip wip]))

      XMLStreamConstants/ATTRIBUTE
      (do
        ;; (println "attribute " (.toString (.getName event)))
        [kb zip wip])

      XMLStreamConstants/CDATA
      (do
        ;; (println "cdata " (.toString (.getName event)))
        [kb zip wip])

      XMLStreamConstants/CHARACTERS
      (do
        ;; (println "chars " (.toString (.getData event)))
        [kb zip wip])

      XMLStreamConstants/DTD
      (do
        ;; (println "dtd " (.toString (.getName event)))
        [kb zip wip])

      XMLStreamConstants/START_DOCUMENT
      (do
        ;; (println "start doc " (.toString event))
        [kb zip wip])

      XMLStreamConstants/END_DOCUMENT
      (do
        ;; (println "end doc " (.toString event))
        [kb zip wip])

      XMLStreamConstants/ENTITY_DECLARATION
      (do
        ;; (println "entity " (.toString (.getLocalName event)))
        [kb zip wip])

      XMLStreamConstants/NAMESPACE
      (do
        ;; (println "namespace " (.toString (.getName event)))
        [kb zip wip])

      XMLStreamConstants/NOTATION_DECLARATION
      (do
        ;; (println "notation " (.toString (.getName event)))
        [kb zip wip])

      XMLStreamConstants/PROCESSING_INSTRUCTION
      (do
        ;; (println "processing " (.toString (.getName event)))
        [kb zip wip])

      (do
        ;; (println "default " (.toString event))
        [kb zip wip])

      )))


(defn extract-knowledge-from-cad-assembly
  "Extract the constraints, links and others from a Cyph2Cad cad-assembly.xml input."
  [fis]
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
          ;; (println "you have reached the end of the input file")
          ;; (pp/pprint ["kb:" kb])
          kb)
        (let [event (.nextEvent reader)
              [new-kb new-wip new-zip]
              (extract-knowledge-from-cad-assembly-aux event kb zip wip) ]
          (recur reader new-kb new-wip new-zip))))))



(defn write-cad-assembly-using-knowledge
  "Update the CAD-Assembly.xml using the link information."
  [fos kb]
  (let [factory (SMOutputFactory. (XMLOutputFactory/newInstance))
        doc (.createOutputDocument factory fos)]
    (.setIndentation doc "\n  " 1 1)
    (.addComment doc (str " generated: " (.toString (Date.))))

    (let [empl (.addElement doc "employee")
          empl-id (.addAttribute empl nil "id" 123)
          ename (.addElement empl "name")]
      (.addCharacters (.addElement ename "proper") "fred")
      (.addCharacters (.addElement ename "surname") "eisele")
      (.closeRoot doc) )  ))

(defn read-cad-assembly-using-knowledge
  "Update the CAD-Assembly.xml using the link information."
  [fis kb]
  (let [factory (SMInputFactory. (XMLInputFactory/newInstance))
        root-cursor (.rootElementCursor factory fis)
        _ (.advance root-cursor)
        employee-id (.getAttrIntValue root-cursor 0)
        employee-name-cursor (.advance (.childElementCursor root-cursor "name"))
        leaf-cursor (.advance (.childElementCursor employee-name-cursor))
        proper-name (.collectDescendantText leaf-cursor false)
        _ (.advance leaf-cursor)
        surname (.collectDescendantText leaf-cursor false)
        _ (.closeCompletely (.getStreamReader root-cursor))
        ]
    (pp/pprint [:id employee-id :proper proper-name :surname surname])))

(defn- add-versor-element
  [writer event-factory link-map comp-link-id]
  (let [_ (.add writer (.createSpace event-factory "\n    "))
        elem (.add writer (.createStartElement event-factory "" nil "versor"))
        link-ref @(get link-map comp-link-id)
        link-versor (:versor link-ref)
        { [x y z] :xlate, [qw q1 q2 q3] :rotate} link-versor
        pi (/ (Math/acos (if (nil? qw) 0.0 qw)) Math/PI 0.5)]
    ;;(pp/pprint link-ref)
    (.add writer (.createAttribute event-factory "x" (str x)))
    (.add writer (.createAttribute event-factory "y" (str y)))
    (.add writer (.createAttribute event-factory "z" (str z)))

    (.add writer (.createAttribute event-factory "pi" (str pi)))
    (.add writer (.createAttribute event-factory "i" (str q1)))
    (.add writer (.createAttribute event-factory "j" (str q2)))
    (.add writer (.createAttribute event-factory "k" (str q3)))
    (.add writer (.createEndElement event-factory "" nil "versor"))
    ))


(defn update-cad-assembly-using-knowledge
  "Update the CAD-Assembly.xml using the link information."
  [fis fos kb]
  (let [in-factory (XMLInputFactory/newInstance)
        _ (.setProperty in-factory XMLInputFactory/IS_COALESCING true)
        reader (.createXMLEventReader in-factory fis)

        out-factory (XMLOutputFactory/newInstance)
        writer (.createXMLEventWriter out-factory fos)
        ;; writer (IndentingXMLEventWriter. writer)

        event-factory (XMLEventFactory/newInstance)
        link-map (:link kb) ]

    (loop []
      (if-not (.hasNext reader)
        (do
          (.close writer)
          (.close reader) )

        (let [event (.nextEvent reader)]
          (.add writer event)
          (condp = (.getEventType event)

            XMLStreamConstants/START_ELEMENT
            (let [elem-type (keyword (.toString (.getName event)))]
              (case elem-type
                :CADComponent
                (add-versor-element writer event-factory link-map
                                    (parse-string-attribute event "ComponentID"))
                "default-element-type") )

            "default-event"
            )
          (recur)) ))))



;; Refs:
;; Stax2 API : http://woodstox.codehaus.org/4.2.0/javadoc/index.html
;; Staxmate  : http://wiki.fasterxml.com/StaxMateDocumentation
;;             http://fasterxml.github.io/StaxMate/javadoc/2.2.0/
;; Aalto-xml : http://fasterxml.github.io/aalto-xml/javadoc/0.9.7/
