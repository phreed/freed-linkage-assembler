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
         (parse-numeric (:z orig))]
     :pi (parse-numeric (:pi orig))
     :q [(parse-numeric (:i orig))
         (parse-numeric (:j orig))
         (parse-numeric (:k orig))] }))

(def ^{:private true} dummy-marker
  {:e [0.0 0.0 0.0] :pi 0.0 :q [0.0 0.0 1.0]})

(defn- reform-marker-pair
  "If one of the markers is missing fill in with something reasonable.
  If a geometric-marker is missing then a dummy will be created.
  If one geomeric-marker is present then it is duplicated.
  If neither geometric-marker is present then a default marker is used."
  [g-a g-b]
  (cond (and (nil? g-a) (nil? g-b)) [dummy-marker dummy-marker]
        (nil? g-a) (let [real (reform-marker g-b)] [real real])
        (nil? g-b) (let [real (reform-marker g-a)] [real real])
        :else [(reform-marker g-a) (reform-marker g-b)] ))


(def ^{:private true} constraint-type-map
  "A mapping between the types specified in the xml and the type required."
  {"SURFACE" :planar
   "POINT" :coincident
   "PLANE" :planar
   "AXIS" :linear
   "LINE" :linear
   "CSYS" :csys })

(defn- extract-constraints-for-single-link
  "Given something that looks like this...

  <Assemblies>
  <Assembly ConfigurationID='{059166f0-b3c0-474f-9dcb-d5e865754d77}|1' _id='id1'>
  <CADComponent ComponentID='{059166f0-b3c0-474f-9dcb-d5e865754d77}|1'
  Name='Boom_Arm_POINT_1' DisplayName='Boom_Arm_POINT_1'
  Type='ASSEMBLY' SpecialInstruction=''
  MaterialID='' Representation='' _id='id2'>
  ...
  <CADComponent ComponentID='{bb160c79-5ba3-4379-a6c1-8603f29079f2}'
  Name='BOOM_EX_375' DisplayName='Arm'
  Type='PART' SpecialInstruction=''
  MaterialID='' Representation='' _id='id19'>
  <Constraint _id='id23'>
  <Pair FeatureInterfaceType='CAD_DATUM' FeatureGeometryType='POINT'
  FeatureAlignmentType='ALIGN' _id='id24'>
  <ConstraintFeature ComponentID='{bb160c79-5ba3-4379-a6c1-8603f29079f2}'
  FeatureName='FRONT' FeatureOrientationType='SIDE_A'
  _id='id25'>
  <GeometryMarker x='1.0' y='0' z='0'
  i='0' j='0' k='0' pi='0' _id='id8' />
  </ConstraintFeature>
  <ConstraintFeature ComponentID='{059166f0-b3c0-474f-9dcb-d5e865754d77}|1'
  FeatureName='ASM_FRONT' FeatureOrientationType='SIDE_A'
  _id='id26'>
  <GeometryMarker x='1.0' y='0' z='0'
  i='0' j='0' k='0' pi='0' _id='id8' />
  </ConstraintFeature>
  </Pair>
  </Constraint>
  ...
  </CADComponent>
  </CADComponent>
  </Assembly>
  </Assemblies>

  ... make something that looks like this...

  [... {:type :coincident,
  :m1
  [['{bb160c79-5ba3-4379-a6c1-8603f29079f2}' 'FRONT']
  {:e [1.0 0.0 0.0]}],
  :m2
  [['{059166f0-b3c0-474f-9dcb-d5e865754d77}|1' 'ASM_FRONT']
  {:e [1.0 0.0 0.0]}]} ...]
  "
  [constraint]
  (let [pair-list (zx/xml-> constraint :Pair)]
    (for [pair pair-list]
      (let [
            feature-pair (zx/xml-> pair :ConstraintFeature)
            c-type (zx/attr pair :FeatureGeometryType)

            a-feat (first feature-pair)
            a-link-name (zx/attr a-feat :ComponentID)
            a-proper-name (zx/attr a-feat :FeatureName)
            a-geom (zx/xml1-> a-feat :GeometryMarker)

            b-feat (second feature-pair)
            b-link-name (zx/attr b-feat :ComponentID)
            b-proper-name (zx/attr b-feat :FeatureName)
            b-geom (zx/xml1-> b-feat :GeometryMarker)

            [a-marker b-marker] (reform-marker-pair a-geom b-geom) ]

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


(defn knowledge-via-input-stream
  "Given an input-stream to a cyphy-assembly file
  extract the knowledge about the assembly."
  [is] (cad-assembly->knowledge (-> is xml/parse zip/xml-zip)))

(defn kb-from-cyphy-file
  "Given an input-file-path to a cyphy-assembly file
  extract the knowledge about the assembly."
  [file-path]
  (with-open [is (-> file-path jio/input-stream)]
    (knowledge-via-input-stream is)))
