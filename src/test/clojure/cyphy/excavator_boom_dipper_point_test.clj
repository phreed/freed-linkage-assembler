(ns cyphy.excavator-boom-dipper-point-test
  (:require [midje.sweet :as tt]

            [isis.geom.cyphy
             [cyphy-zip :as cyphy]
             [cad-stax :as stax]]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-con]
            [isis.geom.machine.misc :as misc]

            [isis.geom.analysis
             [position-analysis :as analysis]]

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


(def assy-name "{059166f0-b3c0-474f-9dcb-d5e865754d77}|1")
(def arm-name "{bb160c79-5ba3-4379-a6c1-8603f29079f2}")
(def boom-name "{62243423-b7fd-4a10-8a98-86209a6620a4}")

(def grounded-constraints
  [{:m1 [[arm-name "FRONT"]
         {:e [1.0 0.0 0.0] :pi 0.0 :q [0.0 0.0 0.0]}]
    :m2 [[assy-name "ASM_FRONT"]
         {:e [1.0 0.0 0.0] :pi 0.0 :q [0.0 0.0 0.0]}]
    :type :coincident}
   {:m1 [[arm-name "TOP"]
         {:e [0.0 1.0 0.0] :pi 0.0 :q [0.0 0.0 0.0]}]
    :m2 [[assy-name "ASM_TOP"]
         {:e [0.0 1.0 0.0] :pi 0.0 :q [0.0 0.0 0.0]}]
    :type :coincident}
   {:m1 [[arm-name "RIGHT"]
         {:e [0.0 0.0 1.0] :pi 0.0 :q [0.0 0.0 0.0]}]
    :m2 [[assy-name "ASM_RIGHT"]
         {:e [0.0 0.0 1.0] :pi 0.0 :q [0.0 0.0 0.0]}]
    :type :coincident}])


(defn facts-about-initial-knowledge
  "check the data loaded from the data source"
  [kb]

  (tt/fact
   "the knowledge-base should have keys"
   (set (keys kb)) =>
   #{:link :constraint :invar :base})

  (tt/fact
   "the base should indicate the name of the assembly"
   (:base kb) => assy-name )

  (tt/fact
   "the base (arm) assembly is grounded"
   @(get-in kb [:link assy-name]) =>
   {:name nil
    :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
    :tdof {:# 0} :rdof {:# 0}}  )

  (tt/fact
   "the follower (boom) is *not* grounded"
   (-> kb :link (get arm-name) deref) =>
   {:name "BOOM_EX_375"
    :versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
    :tdof {:# 3} :rdof {:# 3}}  )


  (tt/fact
   "about initial ground-arm constraints"
   (:constraint kb) =>
   (tt/contains grounded-constraints))

  (tt/fact
   "about initial arm-boom joint constraints"
   (:constraint kb) =>
   (tt/contains
    [{:m1 [[boom-name "APNT_2"]
           {:e [-8649.51 4688.51 600.0] :pi 0.0 :q [0.0 0.0 0.0]}]
      :m2 [[arm-name "PNT2"]
           {:e [3467.85 43.0687 302.5] :pi 0.0 :q [0.0 0.0 0.0]}]
      :type :coincident}
     {:m1 [[boom-name "APNT_1"]
           {:e [-8625.71 4720.65 570.0] :pi 0.0 :q [0.0 0.0 0.0]}]
      :m2 [[arm-name "PNT1"]
           {:e [3455.57 5.0 332.5] :pi 0.0 :q [0.0 0.0 0.0]}]
      :type :coincident}
     {:m1 [[boom-name "APNT_0"]
           {:e [-8625.71 4720.65 600.0] :pi 0.0 :q [0.0 0.0 0.0]}]
      :m2 [[arm-name "PNT0"]
           {:e [3455.57 5.0 302.5] :pi 0.0 :q [0.0 0.0 0.0]}]
      :type :coincident}]))


  (tt/fact
   "about the initial BOOM link settings"
   (-> kb :link (get boom-name) deref) =>
   {:name "BOOM"
    :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
    :tdof {:# 3}, :rdof {:# 3}} )

  (tt/fact
   "about the initial ARM link settings"
   (-> kb :link (get arm-name) deref) =>
   {:name "BOOM_EX_375"
    :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
    :tdof {:# 3}, :rdof {:# 3}} )

  (tt/fact
   "about the initial ASSY link settings"
   (-> kb :link (get assy-name) deref) =>
   {:name nil
    :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
    :tdof {:# 0}, :rdof {:# 0}} )

  (tt/fact
   "about the mark :loc initial"
   (-> kb :invar :loc deref) => #{[assy-name]})

  (tt/fact
   "about the mark :dir initial"
   (-> kb :invar :dir deref) => #{[assy-name]})

  (tt/fact
   "about the mark :twist initial"
   (-> kb :invar :twist deref) => #{[assy-name]} ) )


(defn facts-about-primitive-knowledge
  "after expanding the constraints we have primitive knowledge."
  [kb]
  ;; (pp/pprint result-success)
  ;; (pp/pprint result-link)
  (tt/fact
   "about the expanded ground-arm constraints"
   (:constraint kb) =>
   (tt/contains grounded-constraints))

  (tt/fact
   "about the expanded arm-boom joint constraints"
   (:constraint kb) =>
   (tt/contains
    [{:type :coincident,
      :m1 [[boom-name "APNT_2"]
           {:e [-8649.51 4688.51 600.0], :q [0.0 0.0 0.0], :pi 0.0}],
      :m2 [[arm-name "PNT2"]
           {:e [3467.85 43.0687 302.5], :q [0.0 0.0 0.0], :pi 0.0}]}
     {:type :coincident,
      :m1 [[boom-name "APNT_1"]
           {:e [-8625.71 4720.65 570.0], :q [0.0 0.0 0.0], :pi 0.0}],
      :m2 [[arm-name "PNT1"]
           {:e [3455.57 5.0 332.5], :q [0.0 0.0 0.0], :pi 0.0}]}
     {:type :coincident,
      :m1 [[boom-name "APNT_0"]
           {:e [-8625.71 4720.65 600.0], :q [0.0 0.0 0.0], :pi 0.0}],
      :m2 [[arm-name "PNT0"]
           {:e [3455.57 5.0 302.5], :q [0.0 0.0 0.0], :pi 0.0}]}]))

  (tt/fact
   "about the number of constraints"
   (count (:constraint kb)) => 6)   )

(defn- facts-about-position-analysis-exec
  "This function produces the new knowledge-base
  and other objects. It also checks the
  facts about the other objects."
  [kb]
  (let [result (analysis/position-analysis kb (:constraint kb))
        [success? result-kb result-success result-failure] result]

    (tt/fact
     "about the success result for ground-arm constraints"
     result-success =>
     (tt/contains grounded-constraints))

    (tt/fact
     "about the success result for the arm-boom joint constraints"
     result-success =>
     (tt/contains
      [{:type :coincident,
        :m1 [[boom-name "APNT_2"]
             {:e [-8649.51 4688.51 600.0], :q [0.0 0.0 0.0], :pi 0.0}],
        :m2 [[arm-name "PNT2"]
             {:e [3467.85 43.0687 302.5], :q [0.0 0.0 0.0], :pi 0.0}]}
       {:type :coincident,
        :m1 [[boom-name "APNT_1"]
             {:e [-8625.71 4720.65 570.0], :q [0.0 0.0 0.0], :pi 0.0}],
        :m2 [[arm-name "PNT1"]
             {:e [3455.57 5.0 332.5], :q [0.0 0.0 0.0], :pi 0.0}]}
       {:type :coincident,
        :m1 [[boom-name "APNT_0"]
             {:e [-8625.71 4720.65 600.0], :q [0.0 0.0 0.0], :pi 0.0}],
        :m2 [[arm-name "PNT0"]
             {:e [3455.57 5.0 302.5], :q [0.0 0.0 0.0], :pi 0.0}]}] ))

    (tt/fact
     "about the number of constraints"
     (count result-success) => 6)

    (tt/fact
     "about the failure result"
     result-failure => [])

    result-kb ) )

(defn facts-about-position-analysis-knowledge
  [kb]
  ;; (t/incipient-fact
  ;;  "about the expanded constraints"
  ;;  constraints-exp =>

  (tt/fact
   "about the BOOM link result"
   (-> kb :link (get boom-name) deref) =>
   {:name "BOOM"
    :versor {:xlate [12315.409884054143 -4259.980462767625 903.0008418651848],
             :rotate
             [4.9967957578778144E-5
              -0.8894325067015925
              0.4570665300942336
              -2.435861248047122E-5]},
    :tdof {:# 0, :point [3467.8500000000004 43.06869999999981 302.5]},
    :rdof {:# 0}})

  (tt/fact
   "about the ARM link result"
   (-> kb :link (get arm-name) deref) =>
   {:name "BOOM_EX_375"
    :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
    :tdof {:# 0, :point [1.0 0.0 0.0]},
    :rdof {:# 0}} )

  (tt/fact
   "about the ASSY link result"
   (-> kb :link (get assy-name) deref) =>
   {:name nil
    :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
    :tdof {:# 0},
    :rdof {:# 0}} )


  (tt/fact
   "about the mark :loc result"
   (-> kb :invar :loc deref) =>
   #{[assy-name] [boom-name] [arm-name]})

  (tt/fact
   "about the mark :dir result"
   (-> kb :invar :dir deref) =>
   #{[assy-name] [boom-name] [arm-name]})

  (tt/fact
   "about the mark :twist result"
   (-> kb :invar :twist deref) =>
   #{[assy-name] [boom-name] [arm-name]} ) )


(letfn [
        (thread-fact-fn [thread-arg thread-fn facts-fn]
                        (let [new-item (thread-fn thread-arg)]
                          (facts-fn new-item)
                          new-item))

        (update-constraint-fn [update-fn]
                              (fn [kb] (update-in kb [:constraint] update-fn) ))
        ]
  (with-open [fis (-> "excavator/excavator_boom_dipper_point.xml"
                      jio/resource jio/input-stream)]
    (-> fis
        ;; load the inital knowledge and check it
        (thread-fact-fn
         cyphy/knowledge-via-input-stream
         facts-about-initial-knowledge)

        ;; update the initial knowledge to joint-primitives and check it
        (thread-fact-fn
         (update-constraint-fn meta-con/expand-collection)
         facts-about-primitive-knowledge)

        ;; run the position-analysis and check the resulting knowledge
        (thread-fact-fn
         facts-about-position-analysis-exec
         facts-about-position-analysis-knowledge)

        ;;      #_(with-open [fis (-> "excavator/excavator_boom_dipper_point.xml"
        ;;                            jio/resource jio/input-stream)
        ;;                    fos (-> "/tmp/excavator_boom_dipper_aug.xml"
        ;;                            jio/output-stream)]
        ;;
        ;;          (cyphy/update-cad-assembly-using-knowledge fis fos kb) )
        ) ))

