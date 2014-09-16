(ns cyphy.excavator-stax-test
  (:require [midje.sweet :as t]
            [isis.geom.cyphy.cyphy-zip :as cyphy]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-con]
            [isis.geom.machine.misc :as misc]

            [isis.geom.analysis
             [position-analysis :refer [position-analysis]]]

            [isis.geom.machine.misc :as misc]
            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-dispatch]
             [helical-dispatch]
             [in-line-dispatch]
             [in-plane-dispatch]
             [offset-x-dispatch]
             [offset-z-dispatch]
             [parallel-z-dispatch]]))



(with-open [is (-> "excavator/excavator_total_plane.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/knowledge-via-input-stream is)

        con-a2b [:foo]
        ]
    #_(pp/pprint ["constraints" (:constraints kb)])
    (t/facts "constraints via zipper"
           (t/fact "constraint" kb => (t/contains con-a2b)))))
