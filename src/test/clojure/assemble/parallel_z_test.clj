(ns assemble.parallel-z-test
  "Test the parallel-z assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.action
             [parallel-z-slice :as parallel-z]]))

(defn kb-three-fixed-planes-w-mobile-point []
  (let
    [m1-link-name "mobile-point"
     m2-link-name "static-planes"]
    {:invar {:dir (ref #{[m2-link-name]}),
             :twist (ref #{[m2-link-name]}),
             :loc (ref #{[m2-link-name]})},
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
        :rdof {:# 0}})}}))


 (defn- t3-r3-test! [kb]
  (parallel-z/assemble!->t3-r3
   kb
   [["mobile-point" "MARK-POINT"]
    {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   [["static-planes" "MP-1"]
    {:e [2.0 2.0 2.0], :pi 0.0, :q [0.0 0.0 1.0]}])

  (tt/facts
   "parallel-z : assemble t3-r3"
   (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

   (tt/fact "invar direction"
            (-> kb :invar :dir deref)  => #{["static-planes"]}  )
   (tt/fact "invar location"
            (-> kb :invar :loc deref)  => #{["static-planes"]}  )
   (tt/fact "invar twist"
            (-> kb :invar :twist deref)  => #{["static-planes"]}  )

   (tt/fact "link names" (-> kb :link keys set) =>
            #{"mobile-point" "static-planes"})

   (let [mobile-point (-> kb :link (get "mobile-point") deref)]
     (tt/fact "link 'mobile-point'"
              (-> mobile-point keys set) =>  #{:versor :tdof :rdof})

     (tt/fact "link 'mobile-point' :versor"
              (:versor mobile-point) =>
              {:xlate [0.0 0.0 0.0],
               :rotate [1.0 0.0 0.0 0.0]})

     (tt/fact "link 'mobile-point' :tdof"
              (:tdof mobile-point) =>
              {:# 3})

     (tt/fact "link 'mobile-point' :rdof"
              (:rdof mobile-point) =>
              {:# 1 :axis [1.0 0.0 0.0]} ) )))


 (defn- t3-r1-test! "===== t3-r1 =====" [kb]

  (parallel-z/assemble!->t3-r1
   kb
   [["mobile-point" "MARK-POINT"]
    {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   [["static-planes" "MP-2"]
    {:e [0.0 10.0 -50.0], :pi 0.0, :q [0.0 1.0 0.0]}])

  (tt/facts
   "parallel-z : assemble t2-r3"
   (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

   (tt/fact "invar direction"
            (-> kb :invar :dir deref)  => #{["static-planes"]}  )
   (tt/fact "invar location"
            (-> kb :invar :loc deref)  => #{["static-planes"]}  )
   (tt/fact "invar twist"
            (-> kb :invar :twist deref)  => #{["static-planes"]}  )

   (tt/fact "link names" (-> kb :link keys set) =>
            #{"mobile-point" "static-planes"})

   (let [mobile-point (-> kb :link (get "mobile-point") deref)]
     (tt/fact "link 'mobile-point'"
              (-> mobile-point keys set) =>  #{:versor :tdof :rdof})

     (tt/fact "link 'mobile-point' :versor (no change expected)"
              (:versor mobile-point) =>
              {:xlate [0.0 0.0 -3.0],
               :rotate [1.0 0.0 0.0 0.0]})

     (tt/fact "link 'mobile-point' :tdof (no change expected)"
              (:tdof mobile-point) => {:# 3})

     (tt/fact "link 'mobile-point' :rdof"
              (:rdof mobile-point) =>
              {:# 0,
               :point [3.0 4.0 2.0],
               :line (ga/line [2.0 2.0 2.0] [0.0 0.0 1.0]),
               :lf [3.0 4.0 2.0]}) ) ))


 (defn- t3-r0-test! "===== t3-r0 =====" [kb]

  (parallel-z/assemble!->t3-r0
   kb
   [["mobile-point" "MARK-POINT"]
    {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   [["static-planes" "MP-3"]
    {:e [2.0 0.0 -5.0], :pi 0.0, :q [1.0 0.0 0.0]}])

  (tt/facts
   "parallel-z : assemble t3-r0"
   (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

   (tt/fact "invar location"
            (-> kb :invar :loc deref)  =>
            #{["static-planes"]} #{["mobile-point" "MARK-POINT"]}  )

   (tt/fact "invar direction"
            (-> kb :invar :dir deref)  => #{["static-planes"]}  )

   (tt/fact "invar twist"
            (-> kb :invar :twist deref)  => #{["static-planes"]}  )

   (tt/fact "link names" (-> kb :link keys set) =>
            #{"mobile-point" "static-planes"})

   (let [mobile-point (-> kb :link (get "mobile-point") deref)]
     (tt/fact "link 'mobile-point'"
              (-> mobile-point keys set) =>  #{:versor :tdof :rdof})

     (tt/fact "link 'mobile-point' :versor"
              (:versor mobile-point) =>
              {:xlate [0.0 0.0 -3.0],
               :rotate [1.0 0.0 0.0 0.0]})

     (tt/fact "link 'mobile-point' :tdof"
              (:tdof mobile-point) => {:# 3})

     (tt/fact "link 'mobile-point' :rdof"
              (:rdof mobile-point) =>
              {:# 0,
               :point [1.0 0.0 3.0]}) ) ) )

(let [kb (kb-three-fixed-planes-w-mobile-point)]
  (t3-r3-test! kb)
  (t3-r1-test! kb)
  (t3-r0-test! kb))

;; a sample from a full model

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
   m2 [[m2-link-name "FRONT"]
       {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
   assy-result (parallel-z/assemble!->t3-r3 kb m1 m2)]

  (let [link (-> kb :link (get m1-link-name) deref)]
    (tt/facts "parallel-z t3-r3 m1"
              (tt/fact "about keys"
                       (set (keys link)) => #{:rdof :tdof :versor})
              (tt/fact "about rdof"
                       (:rdof link) => {:# 3})
              (tt/facts "about tdof"
                        (tt/fact "count"
                                 (get-in link [:tdof :#]) =>  2)

                        (tt/fact "count"
                                 (get-in link [:tdof :lf])
                                 => [0.0 0.0 0.0])

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
   "parallel-z t3-r3 m2"
   @(get-in kb [:link m2-link-name])
   =>
   {:rdof {:# 0},
    :tdof {:# 0},
    :versor {:rotate [1.0 0.0 0.0 0.0],
             :xlate [0.0 0.0 0.0]}}) )


(comment "not-implemented parallel-z :t1-r3")
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
   (parallel-z/assemble!->t2-r3 kb m1 m2)]

  (tt/fact
   "parallel-z :t2-r3 m1"
   @(clojure.core/get-in kb [:link m1-link-name])
   =>
   {:rdof {:# 3}
    :tdof {:# 2
           :point [0.0 0.0 0.0]}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 0.0]}})

  (tt/fact
   "parallel-z :t2-r3 m2"
   @(clojure.core/get-in kb [:link m2-link-name])
   =>
   {:rdof {:# 0}
    :tdof {:# 0}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 0.0]}}) )

(comment "not-implemented parallel-z :t2-r3")
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
   (parallel-z/assemble!->t2-r3 kb m1 m2)]

  (tt/fact
   "parallel-z :t2-r3 m1"
   @(clojure.core/get-in kb [:link m1-link-name])
   =>
   {:rdof {:# 3}
    :tdof {:# 2
           :plane [0.0 0.0 2236.3199999999997]
           :point [0.0 0.0 2236.3199999999997]}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 2486.3199999999997]}} )

  (tt/fact
   "parallel-z :t2-r3 m2"
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



(let
  [m1-link-name "{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}"
   m2-link-name "{7d252256-d674-4ab2-a8d0-add7baff5491}"
   kb
   {:link
    {m1-link-name
     (ref
      {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
       :tdof {:# 1, :point [-5302.02 3731.18 600.0]},
       :rdof {:# 3}})

     m2-link-name
     (ref
      {:versor
       {:xlate [-58.51337709985842 117.43615244479886 138.75942078118214],
        :rotate [1.0 0.0 0.0 0.0]},
       :tdof
       {:# 2,
        :point [-5302.02 3731.18 600.0],
        :plane [-325.3573770998584 7260.476152444799 -288.3405792188179]},
       :rdof {:# 3}})
     }
    :invar {:loc (ref #{})
            :dir (ref #{})
            :twist (ref #{})} }
   m1
   [[m1-link-name "UPPER_CYLINDER_AXIS"]
    {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}]
   m2
   [[m2-link-name "CYLINDER_AXIS"]
    {:e [-1703.15 6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}]

   assy-result (parallel-z/assemble!->t2-r3 kb m1 m2) ]

  ;; (pp/pprint ["kb" kb])
  (tt/fact "parallel-z-slice :t2r3 m1"
           @(get-in kb [:link m1-link-name]) =>
           {:rdof {:# 3}
            :tdof {:# 1
                   :point [-5302.02 3731.18 600.0]}
            :versor {:rotate [1.0 0.0 0.0 0.0]
                     :xlate [0.0 0.0 0.0]}})

  (tt/fact "parallel-z-slice :t2r3 m2"
           @(get-in kb [:link m2-link-name]) =>
           {:rdof {:# 1
                   :dir [0.0 0.0 1.0]}
            :tdof {:# 2
                   :plane [-325.3573770998584
                           7260.476152444799
                           -288.3405792188179]
                   :point [-5302.02 3731.18 600.0]}
            :versor {:rotate [0.9285547630875105
                              -0.3322386817479072
                              -0.16554005647549477
                              0.0]
                     :xlate [-601.6002224923432
                             1207.4107569571424
                             4107.547846451218]}}))

(clojure.core/let
 [m1-link-name "{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}"
  m2-link-name "{c80e4c01-9af9-479e-a3a6-e6b80319e485}"
  kb
  {:invar
   {:dir (clojure.core/ref #{}),
    :twist (clojure.core/ref #{}),
    :loc (clojure.core/ref #{})},
   :link
   {m1-link-name
    (clojure.core/ref
     {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
      :tdof {:# 1, :point [-5302.02 3731.18 600.0]},
      :rdof {:# 3}}),
    m2-link-name
    (clojure.core/ref
     {:versor {:xlate [0.0 0.0 -84.0], :rotate [1.0 0.0 0.0 0.0]},
      :tdof
      {:# 2, :point [-5302.02 3731.18 600.0], :plane [0.0 0.0 600.0]},
      :rdof {:# 3}})}}
  m1 [[m1-link-name "UPPER_CYLINDER_AXIS"]
      {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}]
  m2 [[m1-link-name "UPPER_CYLINDER_AXIS"]
      {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}]
  assy-result (parallel-z/assemble!->t2-r3 kb m1 m2)]

 #_(tt/fact "parallel-z-slice t2-r3 m1"
  @(clojure.core/get-in kb [:link m1-link-name])
  =>
  {:versor :m1-goal})

 #_(tt/fact "parallel-z-slice t2-r3 m2"
  @(clojure.core/get-in kb [:link m2-link-name])
  =>
  {:versor :m2-goal}))
