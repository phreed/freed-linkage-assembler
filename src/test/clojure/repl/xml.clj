(require '[clojure.xml :as xml]
         '[clojure.zip :as zip]
         '[clojure.data.xml :as x]
         '[clojure.data.zip.xml :as zx]
         '[clojure.pprint :as pp])

(-> "<root><any>foo<empy/>bar</any>bar</root>"
    .getBytes
    java.io.ByteArrayInputStream.
    xml/parse)
(->{:tag :root :content [{:tag :any :content ["foo" "bar"]} "bar"]}
   zip/xml-zip
   zip/down
   zip/right
   zip/node)


(def data
  "<constraint name=\"a\">
    <pair name=\"ab\">
      <feature name=\"ab1\">
        <geom name=\"ab1x\"/>
      </feature>
      <feature name=\"ab2\">
        <geom name=\"ab2y\"/>
      </feature>
    </pair>
    <pair name=\"ac\">
      <feature name=\"ac1\">
        <geom name=\"ac1z\"/>
      </feature>
      <feature name=\"ac2\"/>
    </pair>
  </constraint>" )


(def xml-tree
  (-> data
    .getBytes
    java.io.ByteArrayInputStream.
    xml/parse  ))

xml-tree
(-> xml-tree
    zip/xml-zip
    zip/down
    zip/down
    zip/down
    zip/node
    )


(for [pair-list  (-> xml-tree
                zip/xml-zip
                (zx/xml-> :pair)) ]
  (let [pair (zx/xml-> pair-list :feature)
        a-feat (first  pair)
        b-feat (second pair)
        a-geo (zx/xml1-> a-feat :geom)
        b-geo (zx/xml1-> b-feat :geom)]
    ["a"
     "b" b-geo]))

