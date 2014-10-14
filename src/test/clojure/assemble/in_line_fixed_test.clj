(ns assemble.in-plane-mobile-test
  "Test the in-plane-mobile assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.action
             [in-plane-mobile-slice :as in-plane-mobile]]))

"in-line-fixed-slice :t2-r3"
(let
 [m1-link-name "{CARRIAGE}"
  m2-link-name "{627ab157-62e1-485c-b797-8139c8f6c133}"
  kb
  {:invar {:dir (ref #{}), :twist (ref #{}), :loc (ref #{})},
   :link
   {m1-link-name
    (ref
     {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
      :tdof
      {:# 2,
       :point [0.0 0.0 0.0],
       :plane {:e [0.0 0.0 0.0], :n [0.0 0.0 1.0]},
       :lf [0.0 0.0 0.0]},
      :rdof {:# 3}}),
    m2-link-name
    (ref
     {:versor
      {:xlate [0.0 0.0 -2320.3199999999997],
       :rotate [1.0 0.0 0.0 0.0]},
      :tdof
      {:# 2,
       :point [0.0 0.0 -1636.32],
       :plane [0.0 0.0 -1636.3199999999997]},
      :rdof {:# 3}})}}
  m1 [[m1-link-name "LOWER_AXIS"]
   {:e [825.0 5444.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}]
  m2 [[m1-link-name "LOWER_AXIS"]
   {:e [825.0 5444.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}]
  assy-result (in-line-fixed-slice/assemble!->t2-r3 kb m1 m2)]

 (tt/fact
  "in-line-fixed-slice :t2-r3 m1"
  @(get-in kb [:link m1-link-name])
  =>
  {:versor :m1-goal})

 (tt/fact
  "in-line-fixed-slice :t2-r3 m2"
  @(get-in kb [:link m2-link-name])
  =>
  {:versor :m2-goal}))
