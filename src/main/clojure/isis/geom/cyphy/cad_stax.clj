(ns isis.geom.cyphy.cad-stax
  "Manipulating the CyPhy2CAD produced CADAssembly.xml
  file using stax."
  (:require
   [isis.geom.machine
    [geobj :as ga]
    [tolerance :as tol]]
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
