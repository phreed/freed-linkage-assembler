(ns isis.geom.lang.cyphy-cad
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
  {'"SURFACE" :planar, '"POINT" :coincident, '"PLANE" :planar})

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

(defn- tree-edit
  "Traverse the tree looking for elements which satisfy the matcher.
  When they are found update the node using the editor and the
  update information from the map."
  [zipper matcher editor]
  (loop [loc zipper]
    (if (zip/end? loc)
      (zip/root loc)
      (if-let [matcher-result (matcher loc)]
        (let [new-loc (zip/edit loc editor)]
          (if (not (= (zip/node new-loc) (zip/node loc)))
            (recur (zip/next new-loc))))
        (recur (zip/next loc))))))

(defn- match-link?
  "Match any CADComponent, i.e. a link."
  [loc]
  (let [tag (:tag (zip/node loc))]
    (= :CADComponent tag)))

(defn- make-versor-element
  [link-versor]
  (let [{xlate :xlate, rotate :rotate} link-versor
        [x1 x2 x3] xlate
        [q0 q1 q2 q3] rotate]
  (xml/element :versor { :x x1 :y x2 :z x3
                         :i q1 :j q2 :k q3
                         :pi (/ (Math/acos q0) (. Math PI))})))

(defn- editor
  "Once you have a component update its versor."
  [link-versors node]
  (let [link-id (-> node :attrs :ComponentID)
        new-content (conj
                     (:content node)
                     (make-versor-element
                      (:versor @(get link-versors link-id))))]
    (assoc-in node [:content] new-content)))


(defn graph-from-cyphy-zipper
  [root]
  (let [asm-link (zx/xml1-> root :Assembly :CADComponent)
        base-link-id (zx/attr asm-link :ComponentID)
        link-map (extract-link-map base-link-id asm-link)
        constraint-list (extract-constraints-for-all-links asm-link)]
     {:root root
      :constraint constraint-list
      :link link-map
      :base base-link-id
      :mark {:loc (ref #{[base-link-id]})
             :z (ref #{[base-link-id]})
             :x (ref #{[base-link-id]})} } ))

(defn graph-to-cyphy-zipper
  [graph]
  (let [link-versors (:link graph)
        node-editor (partial editor link-versors)]
  (assoc graph :augmented
    (tree-edit (:root graph) match-link? node-editor))))


(defn graph-from-cyphy-file
  [file-path]
  (graph-from-cyphy-zipper
   (-> file-path
       jio/resource jio/input-stream xml/parse zip/xml-zip)))


(defn graph-to-cyphy-file
  [graph file-path]
  (with-open [fd (jio/writer file-path)]
    (xml/emit (:augmented graph) fd)))
