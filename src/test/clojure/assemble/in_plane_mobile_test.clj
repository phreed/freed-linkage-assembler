(ns assemble.in-plane-mobile-test
  "Test the in-plane-mobile assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.action [in-plane-slice-mobile :as in-plane-mobile]]))


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
      :tdof {:# 1, :point [0.0 0.0 0.0]},
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
  (in-plane-mobile/assemble!->t1-r3 kb m1 m2)]

 (tt/fact
  "in-plane-mobile :t1-r3 m1"
  @(clojure.core/get-in kb [:link m1-link-name])
  =>
  {:rdof {:# 3}
   :tdof {:# 1
          :point [0.0 0.0 0.0]}
   :versor {:rotate [1.0 0.0 0.0 0.0]
            :xlate [0.0 0.0 0.0]}})

 (tt/fact
  "in-plane-mobile :t1-r3 m2"
  @(clojure.core/get-in kb [:link m2-link-name])
  =>
  {:rdof {:# 0}
   :tdof {:# 0}
   :versor {:rotate [1.0 0.0 0.0 0.0]
            :xlate [0.0 0.0 0.0]}}) )
