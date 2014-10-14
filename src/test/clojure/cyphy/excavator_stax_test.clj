(ns cyphy.excavator-stax-test
  (:require [midje.sweet :as tt]
            [isis.geom.cyphy.cyphy-zip :as cyphy]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model
             [meta-constraint :as meta-constraint]
             [lower-joint :as lower-joint]]
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



(defn facts-about-initial-knowledge
  [kb]
  (let [assy-name "{ASSY}|1"
        carriage-name "{CARRIAGE}"]
    (tt/fact
     "the knowledge-base should have keys"
     (set (keys kb)) =>
     #{:link :constraint :invar :base})

    (tt/fact
     "the base should indicate the name of the assembly"
     (:base kb) => assy-name )

    (tt/fact
     "the base assembly is grounded"
     @(get-in kb [:link assy-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 0} :rdof {:# 0}}  )

    (tt/fact
     "the carriage is *not* grounded"
     @(get-in kb [:link carriage-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 3} :rdof {:# 3}}  )

    ;; (pp/pprint (:constraint kb))
    (tt/fact
     "the carriage is connected to the base assembly via three planes"
     (:constraint kb) =>
     (tt/contains
      [ {:type :planar,
         :m1 [[carriage-name "FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
         :m2 [[assy-name "ASM_FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :planar,
         :m1 [[carriage-name "TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
         :m2 [[assy-name "ASM_TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :planar,
         :m1 [[carriage-name "RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}],
         :m2
         [[assy-name "ASM_RIGHT"]
          {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]} ] ))

    )
  )

(defn facts-about-primitive-knowledge
  [kb]
  (let [assy-name "{ASSY}|1"
        carriage-name "{CARRIAGE}"]
    (tt/fact
     "the knowledge-base should have keys"
     (set (keys kb)) =>
     #{:link :constraint :invar :base})

    (tt/fact
     "the base should indicate the name of the assembly"
     (:base kb) => assy-name )

    (tt/fact
     "the base assembly is *still* grounded"
     @(get-in kb [:link assy-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 0} :rdof {:# 0}}  )

    (tt/fact
     "the carriage is *not* yet grounded"
     @(get-in kb [:link carriage-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 3} :rdof {:# 3}}  )

    ;; (pp/pprint (:constraint kb))
    (tt/incipient-fact
     "the carriage is connected to the base assembly via three planes"
     (:constraint kb) =>
     (tt/contains
      [ {:type :in-plane
         :m1 [[carriage-name "FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[assy-name "ASM_FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[assy-name "ASM_FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[carriage-name "FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[carriage-name "TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[assy-name "ASM_TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[assy-name "ASM_TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[carriage-name "TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[carriage-name "RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[assy-name "ASM_RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[assy-name "ASM_RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[carriage-name "RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}] } ] ))
    ) )

(defn- facts-about-position-analysis-exec
  "This function produces the new knowledge-base
  and other objects. It also checks the
  facts about the other objects."
  [kb]
  (let [result (analysis/position-analysis kb (:constraint kb))
        [success? result-kb result-success result-failure] result]

    (tt/incipient-fact
     "the position analysis produced the plan"
     (:constraint kb) =>
     (tt/contains
      [{:m1 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0] :pi 0.0 :q [0.0 0.0 -1.0]}]
        :m2 [["{ARM}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0] :pi 0.0 :q [0.0 0.0 1.0]}]
        :type :in-plane}
       {:m1 [["{ARM}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0] :pi 0.0 :q [0.0 0.0 1.0]}]
        :m2 [["{BOOM}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0] :pi 0.0 :q [0.0 0.0 -1.0]}]
        :type :in-plane}
       {:m1 [["{BOOM}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99] :pi 0.0 :q [-531.29 -717.57 0.0]}]
        :m2 [["{ARM}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0], :pi 0.0 :q [-334.565 -371.573 0.0]}]
        :type :in-plane}
       {:m1 [["{ARM}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0] :pi 0.0 :q [-334.565 -371.573 0.0]}]
        :m2 [["{BOOM}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99] :pi 0.0, :q [-531.29 -717.57 0.0]}]
        :type :in-plane}
       {:m1 [["{BOOM}" "ARM_AXIS"]
             {:e [-8625.71 4720.65 905.0] :pi 0.0 :q [0.0 0.0 1.0]}]
        :m2 [["{ARM}" "BOOM_AXIS"]
             {:e [2000.0 100.0 0.0] :pi 0.0 :q [0.0 0.0 -1.0]}]
        :type :in-line}
       {:m1 [["{BOOM}" "ARM_AXIS"]
             {:e [-8625.71 4720.65 905.0] :pi 0.0 :q [0.0 0.0 1.0]}]
        :m2 [["{ARM}" "BOOM_AXIS"]
             {:e [2000.0 100.0 0.0] :pi 0.0 :q [0.0 0.0 -1.0]}]
        :type :parallel-z} ]))

    result-kb ) )

(defn facts-about-position-analysis-knowledge
  [kb]
  (let [assy-name "{ASSY}|1"
        carriage-name "{CARRIAGE}"]
    (tt/fact
     "the knowledge-base should have keys"
     (set (keys kb)) =>
     #{:link :constraint :invar :base})

    (tt/fact
     "the base should indicate the name of the assembly"
     (:base kb) => assy-name )

    (tt/fact
     "the base assembly is *still* grounded"
     @(get-in kb [:link assy-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 0} :rdof {:# 0}}  )

    (let [car-map @(get-in kb [:link carriage-name])
          car-keys (set (keys car-map))
          car-rdof (:rdof car-map)
          car-tdof (:tdof car-map)
          car-versor (:versor car-map)
          ]
      (tt/facts
       "the carriage is *not* grounded"
       (tt/fact
        "about the keys"
        car-keys => #{:rdof :tdof :versor})

       (tt/fact
        "about the rdof"
        car-rdof => {:# 1, :dir [0.0 0.0 1637.3199999999997]})

       (tt/facts
        "about the tdof"
        (tt/fact "about the keys"
                (set (keys car-tdof)) => #{:# :plane :point})
        (tt/fact "about the count" (:# car-tdof) => 2)
        (tt/fact "about the plane"
                (:plane car-tdof) =>
                {:e [0.0 0.0 0.0],
                 :n [0.0 0.0 1.0]})
        (tt/fact "about the point"
                (:point car-tdof) => [0.0 0.0 0.0]))

       (tt/fact
        "about the versor"
        car-versor => {:rotate [1.0 0.0 0.0 0.0],
                       :xlate [0.0 0.0 0.0]}) ))

    ;; (pp/pprint (:constraint kb))
    (tt/incipient-fact
     "the carriage is connected to the base assembly via three planes"
     (:constraint kb) =>
     (tt/contains
      [ {:type :in-plane
         :m1 [[carriage-name "FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[assy-name "ASM_FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[assy-name "ASM_FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[carriage-name "FRONT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[carriage-name "TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[assy-name "ASM_TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[assy-name "ASM_TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[carriage-name "TOP"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[carriage-name "RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[assy-name "ASM_RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]}
        {:type :in-plane
         :m1 [[assy-name "ASM_RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
         :m2 [[carriage-name "RIGHT"]
              {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}] } ] ))
    ) )



(comment
  "This performs a sequence of actions on the
  cyphy assembly design.

  A aid to the threading macro.
  The first argument is the thread argument and the
  second is the thread function.
  The final argument is the test which checks that
  the facts are true for the object produced by the
  thread function.

  The update constraint functions update only the
  constraint object.  This function associates that
  constraint object with the knowlege-base.")

(letfn [
        (thread-fact-fn [thread-arg thread-fn facts-fn]
                        (let [new-item (thread-fn thread-arg)]
                          (facts-fn new-item)
                          new-item))

        (update-constraint-fn [update-fn]
                              (fn [kb] (update-in kb [:constraint] update-fn) ))
        ]


  (with-open [is (-> "excavator/excavator_total_plane.xml"
                     jio/resource jio/input-stream)]
    (-> is
        ;; load the initial knowledge and check it
        (thread-fact-fn
         cyphy/knowledge-via-input-stream
         facts-about-initial-knowledge)

        ;; update the initial knowledge to joint-primitives and check it
        (thread-fact-fn
         (update-constraint-fn lower-joint/expand-collection)
         facts-about-primitive-knowledge)

        ;; run the position-analysis and check the resulting knowledge
        (thread-fact-fn
         facts-about-position-analysis-exec
         facts-about-position-analysis-knowledge)
        )) )
