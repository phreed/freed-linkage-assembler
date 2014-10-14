(ns assemble.parallel-z-test
  "Test the parallel-z assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.algebra [geobj :as ga]]
            [isis.geom.action
             [parallel-z-dispatch :as parallel-z]]))

(defn kb-three-fixed-planes-w-mobile-link []
  (let
    [m1-link-name "static-link"
     m2-link-name "mobile-link"]
    {:invar {:dir (ref #{[m1-link-name]}),
             :twist (ref #{[m1-link-name]}),
             :loc (ref #{[m1-link-name]})},
     :link
     {m1-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 0},
        :rdof {:# 0}}),
      m2-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 3},
        :rdof {:# 3}})}}))


(defn- t3-r3-test! [kb]
  (apply
   parallel-z/assemble!
   (parallel-z/precondition
    kb
    [["static-link" "M1"]
     {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 0.0 0.0]}]
    [["mobile-link" "M2"]
     {:e [2.0 2.0 2.0], :pi 0.0, :q [0.0 0.0 1.0]}]))

  (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

  (tt/fact "invar direction"
           (-> kb :invar :dir deref)  => #{["static-link"]}  )
  (tt/fact "invar location"
           (-> kb :invar :loc deref)  => #{["static-link"]}  )
  (tt/fact "invar twist"
           (-> kb :invar :twist deref)  => #{["static-link"]}  )

  (tt/fact "link names" (-> kb :link keys set) =>
           #{"static-link" "mobile-link"})

  (let [mobile-link (-> kb :link (get "mobile-link") deref)]
    (tt/fact "link 'mobile-link'"
             (-> mobile-link keys set) =>  #{:versor :tdof :rdof})

    (tt/fact "link 'mobile-link' :versor"
             (:versor mobile-link) =>
             {:xlate [0.0 0.0 0.0],
              :rotate [1.0 0.0 0.0 0.0]})

    (tt/fact "link 'mobile-link' :tdof"
             (:tdof mobile-link) =>
             {:# 3})

    (tt/fact "link 'mobile-link' :rdof"
             (:rdof mobile-link) =>
             {:# 1 :axis [0.0 0.0 1.0]} ) ))


(defn- t3-r1-test! "===== t3-r1 =====" [kb]

  (apply
   parallel-z/assemble!
   (parallel-z/precondition
    kb
    [["static-link" "M1"]
     {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 1.0 0.0]}]
    [["mobile-link" "M2"]
     {:e [0.0 10.0 -50.0], :pi 0.0, :q [0.0 1.0 0.0]}]))

  (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

  (tt/fact "invar direction"
           (-> kb :invar :dir deref)  =>
           #{["static-link"] ["mobile-link" "M2"]}  )

  (tt/fact "invar location"
           (-> kb :invar :loc deref)  => #{["static-link"]}  )
  (tt/fact "invar twist"
           (-> kb :invar :twist deref)  => #{["static-link"]}  )

  (tt/fact "link names" (-> kb :link keys set) =>
           #{"static-link" "mobile-link"})

  (let [mobile-link (-> kb :link (get "mobile-link") deref)]
    (tt/fact "link 'mobile-link'"
             (-> mobile-link keys set) =>  #{:versor :tdof :rdof})

    (tt/fact "link 'mobile-link' :versor (no change expected)"
             (:versor mobile-link) =>
             {:xlate [0.0 0.0 0.0],
              :rotate [1.0 0.0 0.0 0.0]})

    (tt/fact "link 'mobile-link' :tdof (no change expected)"
             (:tdof mobile-link) => {:# 3})

    (tt/fact "link 'mobile-link' :rdof"
             (:rdof mobile-link) => {:# 0}) ))


(defn- t3-r0-test! "===== t3-r0 =====" [kb]

  (apply
   parallel-z/assemble!
   (parallel-z/precondition
    kb
    [["static-link" "M1"]
     {:e [3.0 4.0 5.0], :pi 0.0, :q [0.0 0.0 0.0]}]
    [["mobile-link" "MP-3"]
     {:e [2.0 0.0 -5.0], :pi 0.0, :q [1.0 0.0 0.0]}]))

  (tt/fact "knowledge properties" (set (keys kb)) => #{:invar :link})

  (tt/fact "invar direction"
           (-> kb :invar :dir deref)  =>
           #{["static-link"] ["mobile-link" "M2"]}  )

  (tt/fact "invar location"
           (-> kb :invar :loc deref)  => #{["static-link"]}  )
  (tt/fact "invar twist"
           (-> kb :invar :twist deref)  => #{["static-link"]}  )

  (tt/fact "link names" (-> kb :link keys set) =>
           #{"static-link" "mobile-link"})

  (let [mobile-link (-> kb :link (get "mobile-link") deref)]
    (tt/fact "link 'mobile-link'"
             (-> mobile-link keys set) =>  #{:versor :tdof :rdof})

    (tt/fact "link 'mobile-link' :versor (no change expected)"
             (:versor mobile-link) =>
             {:xlate [0.0 0.0 0.0],
              :rotate [1.0 0.0 0.0 0.0]})

    (tt/fact "link 'mobile-link' :tdof (no change expected)"
             (:tdof mobile-link) => {:# 3})

    (tt/fact "link 'mobile-link' :rdof"
             (:rdof mobile-link) => {:# 0}) ))


(let [kb (kb-three-fixed-planes-w-mobile-link)]
  (tt/facts "parallel-z : assemble t3-r3" (t3-r3-test! kb))
  (tt/facts "parallel-z : assemble t3-r1" (t3-r1-test! kb))
  (tt/facts "parallel-z : assemble t3-r0" (t3-r0-test! kb)) )

;; a sample from a full model

(comment "in-plane :mobile :t3-r3")
(let
  [m1-link-name "{CARRIAGE}"
   m2-link-name "{ASSY}|1"
   [kb m1 m2] (parallel-z/precondition
               {:invar {:dir (ref #{[m2-link-name]})
                        :twist (ref #{[m2-link-name]})
                        :loc (ref #{[m2-link-name]})}
                :link
                {m1-link-name
                 (ref
                  {:versor {:xlate [0.0 0.0 0.0]
                            :rotate [1.0 0.0 0.0 0.0]}
                   :tdof {:# 3}
                   :rdof {:# 3}})
                 m2-link-name
                 (ref
                  {:versor {:xlate [0.0 0.0 0.0]
                            :rotate [1.0 0.0 0.0 0.0]}
                   :tdof {:# 0}
                   :rdof {:# 0}})}}
               [[m1-link-name "FRONT"]
                {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
               [[m2-link-name "FRONT"]
                {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}])
   assy-result (parallel-z/assemble! kb m1 m2)]

  (let [link (-> kb :link (get m1-link-name) deref)]
    (tt/facts "parallel-z t3-r3 m1"
              (tt/fact "about keys"
                       (set (keys link)) => #{:rdof :tdof :versor})
              (tt/fact "about rdof"
                       (:rdof link) => {:# 1, :axis [0.0 0.0 1.0]})
              (tt/fact "about tdof"
                       (:tdof link) => {:# 3})
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


(comment "parallel-z :t3-r1")
(let
  [m1-link-name
   "{CARRIAGE}"
   m2-link-name
   "{ASSY}|1"

   [kb m1 m2 :as precon]
   (parallel-z/precondition
    {:invar
     {:dir (clojure.core/ref #{[m2-link-name]}),
      :twist (clojure.core/ref #{[m2-link-name]}),
      :loc (clojure.core/ref #{[m2-link-name]})},
     :link
     {m1-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0]
                 :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 3, :point [0.0 0.0 0.0]},
        :rdof {:# 1 :axis [0.0 0.0 1.0]}}),
      m2-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0]
                 :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 0},
        :rdof {:# 0}})}}
    [[m1-link-name "TOP"]
     {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}]
    [[m2-link-name "TOP"]
     {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 1.0 0.0]}] )]

  (tt/fact "precondition statisfied" precon =not=> nil?)

  (parallel-z/assemble! kb m1 m2)

  (tt/fact
   "parallel-z :t3-r1 m1"
   (-> kb :link (get m1-link-name) deref) =>
   {:rdof {:# 0},
    :tdof {:# 3},
    :versor {:rotate [1.0 0.0 0.0 0.0],
             :xlate [0.0 0.0 0.0]}} )


  (tt/fact
   "parallel-z :t3-r1 m2"
   (-> kb :link (get m2-link-name) deref)
   =>
   {:rdof {:# 0}
    :tdof {:# 0}
    :versor {:rotate [1.0 0.0 0.0 0.0]
             :xlate [0.0 0.0 0.0]}}) )

(comment "parallel-z :t3-r0")
(let
  [m1-link-name
   "{ARM}"
   m2-link-name
   "{BOOM}"
   [kb m1 m2 :as precon]
   (parallel-z/precondition
    {:invar
     {:dir (ref #{[m2-link-name]}),
      :twist (ref #{[m2-link-name]}),
      :loc (ref #{[m2-link-name]})},
     :link
     {m1-link-name
      (ref
       {:versor {:xlate [0.0 0.0 2486.32],
                 :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 3
               :point [0.0 0.0 2236.32],
               :plane [0.0 0.0 2236.32]}
        :rdof {:# 1 :axis [0.0 1.0 0.0]}}),
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
        :tdof {:# 0}
        :rdof {:# 0}})}}

    [[m1-link-name "BOOM_CENTER_PLANE"]
     {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}]
    [[m2-link-name "BOOM_CENTER_PLANE"]
     {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}] ) ]

  (tt/fact "precondition statisfied" precon =not=> nil?)

  (parallel-z/assemble! kb m1 m2)

  (tt/fact
   "parallel-z :t3-r0 m1"
   (-> kb :link (get m1-link-name) deref) =>
   {:rdof {:# 0},
    :tdof {:# 3},
    :versor {:rotate [0.8957734707381336  0.0
                      0.44451084252440687 0.0],
             :xlate [4372.170591394167 0.0
                     6280.9161159899395]}} )

  (tt/fact
   "parallel-z :t3-r0 m2"
   (-> kb :link (get m2-link-name) deref) =>
   {:rdof {:# 0 }
    :tdof {:# 0 }
    :versor {:rotate [0.903723365158405
                      -0.04068401573948011
                      0.12144977372159717
                      0.4085080691896436]
             :xlate [6056.068568289035
                     1285.2113238369175
                     220.0398552624306]}} ) )


(let
  [m1-link-name "{ASSY}|1"
   m2-link-name "{CARRIAGE}"
   [kb m1 m2 :as precon]
   (parallel-z/precondition
    {:invar {:dir (ref #{["{ASSY}|1"]}),
             :twist (ref #{"{ASSY}|1"}),
             :loc (ref #{"{ASSY}|1"})},
     :link
     {m1-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof {:# 0},
        :rdof {:# 0}}),
      m2-link-name
      (ref
       {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
        :tdof
        {:# 2,
         :point [0.0 0.0 0.0],
         :plane {:e [0.0 0.0 0.0], :n [0.0 0.0 1.0]}},
        :rdof {:# 1, :axis [0.0 0.0 1.0]}})}}
    [[m1-link-name "ASM_TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]
    [[m2-link-name "TOP"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 0.0]}]) ]

  (tt/fact "precondition statisfied" precon =not=> nil?)
  (parallel-z/assemble! kb m1 m2)

  (tt/fact
   "parallel-z :t2-r1 m1"
   @(get-in kb [:link m1-link-name]) =>
    {:rdof {:# 0}, :tdof {:# 0},
     :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}})

  (tt/fact
   "parallel-z :t2-r1 m2"
   @(get-in kb [:link m2-link-name]) =>
    {:rdof {:# 0},
     :tdof {:# 2, :lf nil,
            :plane {:e [0.0 0.0 0.0], :n [0.0 0.0 1.0]},
            :point [0.0 0.0 0.0]},
     :versor {:rotate [1.0 0.0 0.0 0.0], :xlate [0.0 0.0 0.0]}} ))
