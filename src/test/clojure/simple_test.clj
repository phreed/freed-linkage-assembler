(ns simple-test
  (:require [midje.sweet :as tt]
            [clojure.java.io :as jio]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zx]
            [clojure.data.xml :as xml]
            [clojure.string :as str]))

;; see http://ravi.pckl.me/short/functional-xml-editing-using-zippers-in-clojure/

(tt/fact nil? nil)

(def nested-vecs
  [:foo [:bar :baz [:bork]]])

(def z (zip/vector-zip nested-vecs))

(-> z zip/down zip/node)

(-> z zip/down
    zip/right zip/down
    zip/right zip/right zip/down
    (zip/edit #(str/upper-case %)) zip/node)

(-> z zip/down
    zip/right zip/down
    zip/right zip/right zip/down
    (zip/edit #(str/upper-case %)) zip/root)

(def excavator (->
                "excavator/excavator_boom_dipper_point.xml"
                jio/resource jio/input-stream xml/parse))

(def ex-zip (zip/xml-zip excavator))

(zx/xml1-> ex-zip :Assembly :CADComponent :CADComponent
           (zx/attr= :Name "BOOM_EX_375"))
(zip/node (zx/xml1-> ex-zip :Assembly :CADComponent :CADComponent
                     (zx/attr= :Name "BOOM_EX_375") :Constraint ))







