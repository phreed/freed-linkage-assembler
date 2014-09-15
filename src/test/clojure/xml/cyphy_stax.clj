(ns cyphy.cyphy-stax
  (:require [midje.sweet :refer [defchecker chatty-checker checker facts fact]]
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
  (cyphy/kb-from-cyphy-input-stream is))
