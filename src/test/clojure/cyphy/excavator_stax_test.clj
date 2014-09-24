(ns cyphy.excavator-stax-test
  (:require [midje.sweet :as t]
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
  (let [assy-name "{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1"
        carriage-name "{dce1362d-1b44-4652-949b-995aa2ce5760}"]
    (t/fact
     "the knowledge-base should have keys"
     (set (keys kb)) =>
     #{:link :constraint :invar :base})

    (t/fact
     "the base should indicate the name of the assembly"
     (:base kb) => assy-name )

    (t/fact
     "the base assembly is grounded"
     @(get-in kb [:link assy-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 0} :rdof {:# 0}}  )

    (t/fact
     "the carriage is *not* grounded"
     @(get-in kb [:link carriage-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 3} :rdof {:# 3}}  )

    ;; (pp/pprint (:constraint kb))
    (t/fact
     "the carriage is connected to the base assembly via three planes"
     (:constraint kb) =>
     (t/contains
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
  (let [assy-name "{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1"
        carriage-name "{dce1362d-1b44-4652-949b-995aa2ce5760}"]
    (t/fact
     "the knowledge-base should have keys"
     (set (keys kb)) =>
     #{:link :constraint :invar :base})

    (t/fact
     "the base should indicate the name of the assembly"
     (:base kb) => assy-name )

    (t/fact
     "the base assembly is *still* grounded"
     @(get-in kb [:link assy-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 0} :rdof {:# 0}}  )

    (t/fact
     "the carriage is *not* yet grounded"
     @(get-in kb [:link carriage-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 3} :rdof {:# 3}}  )

    ;; (pp/pprint (:constraint kb))
    (t/fact
     "the carriage is connected to the base assembly via three planes"
     (:constraint kb) =>
     (t/contains
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

    (t/fact
     "the position analysis produced the plan"
     (:constraint kb) =>
     (t/contains
      [{:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0] :pi 0.0 :q [0.0 0.0 -1.0]}]
        :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0] :pi 0.0 :q [0.0 0.0 1.0]}]
        :type :in-plane}
       {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_CENTER_PLANE"]
             {:e [0.0 0.0 -250.0] :pi 0.0 :q [0.0 0.0 1.0]}]
        :m2 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"]
             {:e [-5302.02 3731.18 600.0] :pi 0.0 :q [0.0 0.0 -1.0]}]
        :type :in-plane}
       {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99] :pi 0.0 :q [-531.29 -717.57 0.0]}]
        :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0], :pi 0.0 :q [-334.565 -371.573 0.0]}]
        :type :in-plane}
       {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_GUIDE"]
             {:e [1150.48 864.911 -250.0] :pi 0.0 :q [-334.565 -371.573 0.0]}]
        :m2 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_GUIDE"]
             {:e [-3741.05 1103.98 1369.99] :pi 0.0, :q [-531.29 -717.57 0.0]}]
        :type :in-plane}
       {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_AXIS"]
             {:e [-8625.71 4720.65 905.0] :pi 0.0 :q [0.0 0.0 1.0]}]
        :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_AXIS"]
             {:e [2000.0 100.0 0.0] :pi 0.0 :q [0.0 0.0 -1.0]}]
        :type :in-line}
       {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_AXIS"]
             {:e [-8625.71 4720.65 905.0] :pi 0.0 :q [0.0 0.0 1.0]}]
        :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_AXIS"]
             {:e [2000.0 100.0 0.0] :pi 0.0 :q [0.0 0.0 -1.0]}]
        :type :parallel-z} ]))

    result-kb ) )

(defn facts-about-position-analysis-knowledge
  [kb]
  (let [assy-name "{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1"
        carriage-name "{dce1362d-1b44-4652-949b-995aa2ce5760}"]
    (t/fact
     "the knowledge-base should have keys"
     (set (keys kb)) =>
     #{:link :constraint :invar :base})

    (t/fact
     "the base should indicate the name of the assembly"
     (:base kb) => assy-name )

    (t/fact
     "the base assembly is *still* grounded"
     @(get-in kb [:link assy-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 0} :rdof {:# 0}}  )

    (t/fact
     "the carriage is *not* grounded"
     @(get-in kb [:link carriage-name]) =>
     {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
      :tdof {:# 1 :point [0.0 0.0 0.0]} :rdof {:# 3}}  )

    ;; (pp/pprint (:constraint kb))
    (t/fact
     "the carriage is connected to the base assembly via three planes"
     (:constraint kb) =>
     (t/contains
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
