(ns isis.geom.visual.openscad
  "http://adereth.github.io/blog/2014/04/09/3d-printing-with-clojure/
  http://www.openscad.org/documentation.html
  https://github.com/farrellm/scad-clj
  "
  (:require [clojure.pprint :as pp]
            [scad-clj
             [scad :as scad]
             [model :as model]] ) )

(def primitives
  (model/union
   (model/cube 100 100 100)
   (model/sphere 70)
   (model/cylinder 10 160)))


(defn write-knowledge
  "write the knowledge about position to a scad file.
  Process all of the links.
  Writing their names and versors."
  [fos kb]
  (doseq [link (-> kb :link vals)]
    (when-not (nil? (:name @link))
      (->>
       (model/import (str (:name @link) ".stl"))
       (model/rotate (-> @link :versor :rotate first Math/acos (* 2))
                     (-> @link :versor :rotate rest) )
       (model/translate (-> @link :versor :xlate))
       scad/write-scad
       .getBytes
       (.write fos) ))))
