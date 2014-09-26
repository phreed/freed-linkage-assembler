(ns assemble.in-plane-mobile-test
  "Test the in-plane-mobile assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.action
             [in-plane-slice-mobile :as in-plane-mobile]]))

(comment "in-plane :mobile :t3-r3")
(let
  [m1-link-name "{dce1362d-1b44-4652-949b-995aa2ce5760}"
   m2-link-name "{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1"
   kb
   {:invar {:dir (ref #{}), :twist (ref #{}), :loc (ref #{})},
    :link
    {m1-link-name
     (ref
      {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
       :tdof {:# 3},
       :rdof {:# 3}}),
     m2-link-name
     (ref
      {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
       :tdof {:# 0},
       :rdof {:# 0}})}}
   m1 [[m1-link-name "FRONT"]
       {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   m2 [[m1-link-name "FRONT"]
       {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   assy-result (in-plane-mobile/assemble!->t3-r3 kb m1 m2)]

  (let [link @(get-in kb [:link m1-link-name])]
    (tt/facts "in-plane-mobile t3-r3 m1"
              (tt/fact "about keys"
                       (set (keys link)) => #{:rdof :tdof :versor})
              (tt/fact "about rdof"
                       (:rdof link) => {:# 3})
              (tt/facts "about tdof"
                        (tt/fact "count"
                                 (get-in link [:tdof :#]) =>  2)

                        (tt/fact "count"
                                 (get-in link [:tdof :lf]) => [0.0 0.0 0.0])

                        (tt/fact "plane"
                                 (get-in link [:tdof :plane])
                                 => {:e [0.0 0.0 0.0],
                                     :n [0.0 0.0 1.0]})
                        (tt/fact "point"
                                 (get-in link [:tdof :point])
                                 =>  [0.0 0.0 0.0]) )
              (tt/fact "about versor"
                       (:versor link) => {:rotate [1.0 0.0 0.0 0.0],
                                          :xlate [0.0 0.0 0.0]}) ))

  (tt/fact
   "in-plane-mobile t3-r3 m2"
   @(get-in kb [:link m2-link-name])
   =>
   {:rdof {:# 0},
    :tdof {:# 0},
    :versor {:rotate [1.0 0.0 0.0 0.0],
             :xlate [0.0 0.0 0.0]}}) )


(comment "not-implemented in-plane-mobile :t1-r3")
(let
  [m1-link-name
   "{dce1362d-1b44-4652-949b-995aa2ce5760}"
   m2-link-name
   "{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1"
   kb
   {:invar
    {:dir (clojure.core/ref #{}),
     :twist (clojure.core/ref #{}),
     :loc (clojure.core/ref #{})},
    :link
    {m1-link-name
     (ref
      {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
       :tdof {:# 2, :point [0.0 0.0 0.0]},
       :rdof {:# 3}}),
     m2-link-name
     (ref
      {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
       :tdof {:# 0},
       :rdof {:# 0}})}}
   m1 [[m1-link-name "TOP"]
       {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   m2 [[m1-link-name "TOP"]
       {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   assy-result
   (in-plane-mobile/assemble!->t2-r3 kb m1 m2)]

  (tt/fact
   "in-plane-mobile :t2-r3 m1"
   @(clojure.core/get-in kb [:link m1-link-name])
   =>
   {:rdof {:# 3}
    :tdof {:# 2
           :point [0.0 0.0 0.0]}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 0.0]}})

  (tt/fact
   "in-plane-mobile :t2-r3 m2"
   @(clojure.core/get-in kb [:link m2-link-name])
   =>
   {:rdof {:# 0}
    :tdof {:# 0}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 0.0]}}) )

(comment "not-implemented in-plane-mobile :t2-r3")
(let
  [m1-link-name
   "{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}"
   m2-link-name
   "{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}"
   kb
   {:invar
    {:dir (ref #{}),
     :twist (ref #{}),
     :loc (ref #{})},
    :link
    {m1-link-name
     (ref
      {:versor
       {:xlate [0.0 0.0 2486.3199999999997],
        :rotate [1.0 0.0 0.0 0.0]},
       :tdof
       {:# 2,
        :point [0.0 0.0 2236.3199999999997],
        :plane [0.0 0.0 2236.3199999999997]},
       :rdof {:# 3}}),
     m2-link-name
     (ref
      {:versor
       {:xlate [6056.068568289035
                1285.2113238369175
                220.0398552624306],
        :rotate
        [0.903723365158405
         -0.04068401573948011
         0.12144977372159717
         0.4085080691896436]},
       :tdof
       {:# 2,
        :point [0.0 0.0 2236.3199999999997],
        :plane {:e [0.0 0.0 -1636.32], :n [0.0 0.0 1.0]}},
       :rdof
       {:# 1,
        :dir [266.4038129165836
              -7142.156541926609
              2149.900428065357]}})}}
   m1
   [[m1-link-name "BOOM_CENTER_PLANE"]
    {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}]
   m2
   [[m1-link-name "BOOM_CENTER_PLANE"]
    {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}]
   assy-result
   (in-plane-mobile/assemble!->t2-r3 kb m1 m2)]

  (tt/fact
   "in-plane-mobile :t2-r3 m1"
   @(clojure.core/get-in kb [:link m1-link-name])
   =>
   {:rdof {:# 3}
    :tdof {:# 2
           :plane [0.0 0.0 2236.3199999999997]
           :point [0.0 0.0 2236.3199999999997]}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 2486.3199999999997]}} )

  (tt/fact
   "in-plane-mobile :t2-r3 m2"
   @(clojure.core/get-in kb [:link m2-link-name])
   =>
   {:rdof {:# 1
           :dir [266.4038129165836
                 -7142.156541926609
                 2149.900428065357]}
    :tdof {:# 2
           :plane {:e [0.0 0.0 -1636.32]
                   :n [0.0 0.0 1.0]}
           :point [0.0 0.0 2236.3199999999997]}
    :versor {:rotate [0.903723365158405
                      -0.04068401573948011
                      0.12144977372159717
                      0.4085080691896436]
             :xlate [6056.068568289035
                     1285.2113238369175
                     220.0398552624306]}} ) )
