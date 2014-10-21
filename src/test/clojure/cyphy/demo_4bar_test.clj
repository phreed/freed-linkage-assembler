(ns cyphy.demo-4bar-test
  (:require [midje.sweet :as tt]
            [isis.geom.cyphy
             [cyphy-zip :as cyphy]
             [cad-stax :as stax]]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-constraint]
            [isis.geom.model.lower-joint :as lower-joint]
            [isis.geom.machine.misc :as misc]
            [isis.geom.algebra [geobj :as ga]]

            [isis.geom.analysis
             [position-analysis :as pa]]

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


(with-open [fis (-> "demo/four_bar_csys.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/knowledge-via-input-stream fis)
        constraints-orig (:constraint kb)
        constraints-nil-patch (lower-joint/nil-patch-collection constraints-orig)
        constraints-meta (meta-constraint/expand-collection constraints-nil-patch)
        constraints-lower (lower-joint/expand-collection constraints-meta)

        choose-pair (fn [x c1 c2]
                      (let [l1 (-> x :m1 first first)
                            l2 (-> x :m2 first first) ]
                        (or (and (= l1 c1) (= l2 c2))
                            (and (= l1 c2) (= l2 c1)) ))) ]

    ;; _ (pp/pprint ["constraints-orig:" constraints-orig])

    ;; The arm2 {a93..51f} is connected to
    ;; the boom {99c..264} via a revolute joint.
    (tt/facts
     "about the parsed cad-assembly file ground (assembly)-to-carriage"
     (tt/fact "about the base link id" (:base kb) => "{ASSY}")

     (tt/fact
      "original the carriage is connected to the base assembly via three planes"
      (filterv #(choose-pair % "{ASSY}" "{BAR-1}")
               constraints-orig) =>
       [{:m1 [["{BAR-1}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
         :m2 [["{ASSY}" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
         :type :planar}
        {:m1 [["{BAR-1}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
         :m2 [["{ASSY}" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
         :type :planar}
        {:m1 [["{BAR-1}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
         :m2 [["{ASSY}" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
         :type :planar}] )

     (tt/fact
      "meta expanded "
      (filterv #(choose-pair % "{BAR-1}" "{BAR-2}")
               constraints-meta) =>
      [{:m1 [["{BAR-1}" "CS1-origin"] {:e [0.0 0.0 50.0]}],
        :m2 [["{BAR-2}" "CS0-origin"] {:e [0.0 0.0 -50.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-3x"] {:e [300.0 0.0 50.0]}],
                            :m2 [["{BAR-2}" "CS0-3x"] {:e [0.0 0.0 -350.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-4y"] {:e [0.0 400.0 50.0]}],
        :m2 [["{BAR-2}" "CS0-4y"] {:e [0.0 400.0 -50.0]}],
        :type :coincident}] )

     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}" "{BAR-1}")
                     (= :parallel-z (:type %)) )
               constraints-lower) =>
       [{:m1 [["{BAR-1}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
         :m2 [["{ASSY}" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
         :type :parallel-z}
        {:m1 [["{BAR-1}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
         :m2 [["{ASSY}" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
         :type :parallel-z}
        {:m1 [["{BAR-1}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
         :m2 [["{ASSY}" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
         :type :parallel-z}])


     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}" "{BAR-1}")
                     (= :in-plane (:type %)) )
               constraints-lower) =>
      [{:m1 [["{BAR-1}" "FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{ASSY}" "ASM_FRONT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :type :in-plane}
       {:m1 [["{BAR-1}" "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :m2 [["{ASSY}" "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}],
        :type :in-plane}
       {:m1 [["{BAR-1}" "RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{ASSY}" "ASM_RIGHT"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :type :in-plane}] )


     (tt/fact
      "fully expanded to in-line primitive-joints "
      (filterv #(and (choose-pair % "{ASSY}" "{BAR-1}")
                     (= :in-line (:type %)) )
               constraints-lower) =>
      []) )


    (tt/facts
     "about the parsed cad-assembly file arm-to-boom"

     (tt/fact
      "original "
      (filterv #(choose-pair % "{BAR-1}" "{BAR-2}")
               constraints-orig) =>
      [{:m1 [["{BAR-1}" "CS1"] {:e [0.0 0.0 50.0], :pi 0.0, :q [1.0 0.0 0.0]}],
        :m2 [["{BAR-2}" "CS0"] {:e [0.0 0.0 -50.0], :pi 0.5, :q [0.0 1.0 0.0]}],
        :type :csys}] )


     (tt/fact
      "meta expanded "
      (filterv #(choose-pair % "{BAR-1}" "{BAR-2}")
               constraints-meta) =>
      [{:m1 [["{BAR-1}" "CS1-origin"] {:e [0.0 0.0 50.0]}],
        :m2 [["{BAR-2}" "CS0-origin"] {:e [0.0 0.0 -50.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-3x"] {:e [300.0 0.0 50.0]}],
        :m2 [["{BAR-2}" "CS0-3x"] {:e [0.0 0.0 -350.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-4y"] {:e [0.0 400.0 50.0]}],
        :m2 [["{BAR-2}" "CS0-4y"] {:e [0.0 400.0 -50.0]}],
        :type :coincident}] )


     (tt/fact
      "fully expanded to in-plane primitive-joints "
      (filterv #(and (choose-pair % "{BAR-1}" "{BAR-2}")
                     (= :coincident (:type %)) )
               constraints-lower) =>
      [{:m1 [["{BAR-1}" "CS1-origin"] {:e [0.0 0.0 50.0]}],
        :m2 [["{BAR-2}" "CS0-origin"] {:e [0.0 0.0 -50.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-3x"] {:e [300.0 0.0 50.0]}],
        :m2 [["{BAR-2}" "CS0-3x"] {:e [0.0 0.0 -350.0]}],
        :type :coincident}
       {:m1 [["{BAR-1}" "CS1-4y"] {:e [0.0 400.0 50.0]}],
        :m2 [["{BAR-2}" "CS0-4y"] {:e [0.0 400.0 -50.0]}],
        :type :coincident}] ) )


    (tt/facts
     "about the parsed cad-assembly file invariants"

     (tt/fact
      "about the initial link settings"
      (->> kb :link
           (filter #(contains? #{ "{ASSY}" "{BAR-1}"
                                  "{BAR-2}" "{BAR-3}"
                                  "{BAR-4}"} (first %)))
           (mapv #(vector (first %) @(second %)))
           (into {})) =>
      { "{ASSY}"  {:rdof {:# 0}, :tdof {:# 0},
                     :versor {:rotate [1.0 0.0 0.0 0.0],
                              :xlate [0.0 0.0 0.0]}},

        "{BAR-1}" {:rdof {:# 3}, :tdof {:# 3},
                      :versor {:rotate [1.0 0.0 0.0 0.0],
                               :xlate [0.0 0.0 0.0]}},

        "{BAR-2}" {:rdof {:# 3},
                  :tdof {:# 3},
                  :versor {:rotate [1.0 0.0 0.0 0.0],
                           :xlate [0.0 0.0 0.0]}},
        "{BAR-3}" {:rdof {:# 3},
                 :tdof {:# 3},
                 :versor {:rotate [1.0 0.0 0.0 0.0],
                          :xlate [0.0 0.0 0.0]}},
        "{BAR-4}" {:rdof {:# 3},
                    :tdof {:# 3},
                    :versor {:rotate [1.0 0.0 0.0 0.0],
                             :xlate [0.0 0.0 0.0]}} } )


     (tt/fact
      "about the initial marker invariants"
      (into {} (map (fn [[k v]] [k @v]) (:invar kb))) =>
      {:loc #{["{ASSY}"]}
       :dir #{["{ASSY}"]}
       :twist #{["{ASSY}"]}} )  )



    (let [result (pa/position-analysis kb constraints-lower)
          [success? result-kb result-success result-failure] result
          {result-mark :invar result-link :link} result-kb ]

      ;; (pp/pprint result-success)
      ;; (pp/pprint result-link)
      (tt/facts
       "about results of linkage-assembly"

       (tt/fact
        "about the mark result" result-mark =>
        (tt/contains
         []) ))


      (tt/fact
       "about the final link settings"
       (->> result-link
            (filter #(contains? #{ "{ASSY}" "{BAR-1}"
                                   "{BAR-2}" "{BAR-3}"
                                   "{BAR-4}"} (first %)))
            (mapv #(vector (first %) @(second %)))
            (into {})) =>
        {"{ASSY}" {:rdof {:# 0}, :tdof {:# 0},
                   :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}},
         "{BAR-1}" {:rdof {:# 0}, :tdof {:# 0, :point [0.0 0.0 0.0]},
                    :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}},
         "{BAR-2}" {:rdof {:# 0}, :tdof {:# 0, :point [0.0 0.0 50.0]},
                    :versor {:rotate [0.7071067811865476 0.0 -0.7071067811865475 0.0],
                             :xlate [-50.0 0.0 50.0]}},
         "{BAR-3}" {:rdof {:# 0}, :tdof {:# 0, :point [-100.0 0.0 50.0]},
                    :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [-100.0 0.0 100.0]}},
         "{BAR-4}" {:rdof {:# 0}, :tdof {:# 0, :point [-100.0 0.0 150.0]},
                    :versor {:rotate [0.7071067811865476 0.0 -0.7071067811865475 0.0],
                             :xlate [-150.0 0.0 150.0]}}} )


      (tt/incipient-fact
       "about the success result" result-success => [])

      (tt/incipient-fact
       "about the failure result" result-failure =>
       [] )

      #_(with-open [fis (-> "demo/four_bar_csys.xml"
                            jio/resource jio/input-stream)
                    fos (-> "/tmp/four_bar_csys_aug.xml"
                            jio/output-stream)]

          (cyphy/update-cad-assembly-using-knowledge fis fos kb) ) ) ))


