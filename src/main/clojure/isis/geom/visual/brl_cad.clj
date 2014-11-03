(ns isis.geom.visual.brl-cad
  "http://brlcad.org/
  There are some problems building a sample
  BRL-CAD model. This activity will have to
  be postponed until an acceptable model is
  available. "
  (:require [clojure.pprint :as pp] ))

(def primitive "some geometry for brl-cad")

(defn write-knowledge
  "write the knowledge about position to brl-cad.
  Process all of the links.
  Writing their names and versors."
  [fos kb]
  (doseq [link (-> kb :link vals)]
    (when-not (nil? (:name @link))
      (->>
       #_(model/import (str (:name @link) ".stl"))
       #_(model/rotate (-> @link :versor :rotate first Math/acos (* 2))
                     (-> @link :versor :rotate rest) )
       #_(model/translate (-> @link :versor :xlate))
       #_scad/write-scad

       primitive
       .getBytes
       (.write fos) ))))
