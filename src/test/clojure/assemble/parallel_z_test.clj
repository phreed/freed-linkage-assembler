(ns assemble.parallel-z-test
  "Test the projection of versors"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.action [parallel-z-slice :as parallel-z]]))


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
