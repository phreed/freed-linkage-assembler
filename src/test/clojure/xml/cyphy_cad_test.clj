(ns xml.cyphy-cad-test
  (:require [midje.sweet :refer [defchecker chatty-checker checker facts fact]]
            [isis.geom.lang.cyphy-cad-stax :as cyphy]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.machine.misc :as misc]

            [isis.geom.analysis
             [position-analysis :refer [position-analysis]]]

            [isis.geom.machine.misc :as misc]
            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-slice]
             [helical-slice]
             [in-line-slice]
             [in-plane-slice]
             [offset-x-slice]
             [offset-z-slice]
             [parallel-z-slice]]))


(defchecker ref->checker
  "A checker that allows the names of references to be ignored."
  [expected]
  (checker [actual]
           (let [actual-deref (clojure.walk/postwalk
                               #(if (misc/reference? %) [:ref @%] %) actual)]
             (if (= actual-deref expected) true
               (do
                 (println "Actual result:\n")
                 (clojure.pprint/pprint actual-deref)
                 (println "Expected result:\n")
                 (clojure.pprint/pprint expected)
                 )))))

(with-open [fis (-> "excavator/cad_assembly_boom_dipper.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/extract-knowledge-from-cad-assembly fis)
        constraints (:constraint kb)]

    (pp/pprint constraints)
    (facts "about the parsed cad-assembly file with points"
           (fact "about the constraints"
                 constraints =>
                 '[{:m1 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "TOP"]
                         {:e [1.0 0.0 0.0]}]
                    :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "TOP"]
                         {:e [1.0 0.0 0.0]}]
                    :type :coincident}
                   {:m1 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "RIGHT"]
                         {:e [0.0 1.0 0.0]}]
                    :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "RIGHT"]
                         {:e [0.0 1.0 0.0]}]
                    :type :coincident}
                   {:m1 [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "FRONT"]
                         {:e [0.0 0.0 1.0]}]
                    :m2 [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "FRONT"]
                         {:e [0.0 0.0 1.0]}]
                    :type :coincident}]
                 )


           (fact "about the initial link settings"
                 (:link kb) => (ref->checker
                                '{"{62243423-b7fd-4a10-8a98-86209a6620a4}"
                                  [:ref
                                   {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                                    :tdof {:# 3},
                                    :rdof {:# 3}}],
                                  "{bb160c79-5ba3-4379-a6c1-8603f29079f2}"
                                  [:ref
                                   {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                                    :tdof {:# 3},
                                    :rdof {:# 3}}],
                                  "{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"
                                  [:ref
                                   {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                                    :tdof {:# 0},
                                    :rdof {:# 0}}]} ))

           (fact "about the base link id"
                 (:base kb) => "{059166f0-b3c0-474f-9dcb-d5e865754d77}|1")

           (fact "about the initial marker invariants"
                 (:mark kb) => (ref->checker
                                '{:loc [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]}],
                                  :z [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]}],
                                  :x [:ref #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]}]} )) )


    (let [mark-pattern
          '{:loc
            [:ref
             #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
               ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}],
            :z
            [:ref
             #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
               ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}],
            :x
            [:ref
             #{["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"]
               ["{bb160c79-5ba3-4379-a6c1-8603f29079f2}"]}]}

          link-pattern
          '{"{62243423-b7fd-4a10-8a98-86209a6620a4}"
            [:ref
             {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
              :tdof {:# 3},
              :rdof {:# 3}}],
            "{bb160c79-5ba3-4379-a6c1-8603f29079f2}"
            [:ref
             {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
              :tdof {:# 0, :point [1.0 0.0 0.0]},
              :rdof {:# 0}}],
            "{059166f0-b3c0-474f-9dcb-d5e865754d77}|1"
            [:ref
             {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
              :tdof {:# 0},
              :rdof {:# 0}}]}

          success-pattern
          '[{:type :coincident,
             :m1
             [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "TOP"]
              {:e [1.0 0.0 0.0]}],
             :m2
             [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "TOP"]
              {:e [1.0 0.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "RIGHT"]
              {:e [0.0 1.0 0.0]}],
             :m2
             [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "RIGHT"]
              {:e [0.0 1.0 0.0]}]}
            {:type :coincident,
             :m1
             [["{059166f0-b3c0-474f-9dcb-d5e865754d77}|1" "FRONT"]
              {:e [0.0 0.0 1.0]}],
             :m2
             [["{bb160c79-5ba3-4379-a6c1-8603f29079f2}" "FRONT"]
              {:e [0.0 0.0 1.0]}]}]

          failure-pattern '[]
          ]
      (let [result (position-analysis kb constraints)
            [success? result-kb result-success result-failure] result
            {result-mark :mark result-link :link} result-kb ]

        ;; (pp/pprint result-success)
        ;; (pp/pprint result-link)
        (facts "about results of linkage-assembly"
               (fact "about the mark result"
                     result-mark => (ref->checker mark-pattern) )
               (fact "about the success result"
                     result-success => (ref->checker success-pattern) )
               (fact "about the failure result"
                     result-failure => (ref->checker failure-pattern) )
               (fact "about the link result"
                     result-link => (ref->checker link-pattern) ))

        #_(with-open [fis (-> "excavator/cad_assembly_boom_dipper.xml"
                              jio/resource jio/input-stream)
                      fos (-> "/tmp/cad_assembly_boom_dipper_aug.xml"
                              jio/output-stream)]

            (cyphy/update-cad-assembly-using-knowledge fis fos kb) ) ) )))

;;    (pp/pprint (ref->str result-link))

;; trying to use staxmate rather than straight stax
#_(with-open [fos (-> "junk.xml"
                      jio/output-stream)]
    (cyphy/write-cad-assembly-using-knowledge fos nil)
    (with-open [fis (-> "junk.xml"
                        jio/input-stream)]
      (cyphy/read-cad-assembly-using-knowledge fis nil)))
