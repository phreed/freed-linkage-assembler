(ns assemble.in-line-o2p-test
  "Test the in-line-fixed assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.algebra
             [geobj :as ga]]
            [isis.geom.action
             [in-line-dispatch :as in-line]]))


(defn test-t2-r1-pin []
 (let
  [m1-link-name "{CARRIAGE}"
   m2-link-name "{CARRIAGE-BOOM-LOWER-PIN}"
   [kb m1 m2 motive :as precon]
   (in-line/precondition
    {:invar
     {:dir (ref #{m1-link-name}),
      :twist (ref #{m1-link-name}),
      :loc (ref #{m1-link-name})},
     :link
     {m1-link-name
      (ref
       {:name "UPPER_BODY",
        :tdof {:# 0, :point [0.0 0.0 0.0]},
        :rdof {:# 0},
        :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]}}),
      m2-link-name
      (ref
       {:name "PIN",
        :tdof
        {:# 2,
         :point [0.0 0.0 -1636.32],
         :plane (ga/plane [0.0 0.0 684.0] [0.0 0.0 -1.0]),
         :lf (ga/plane [0.0 0.0 684.0] [0.0 0.0 -1.0])},
        :rdof {:# 1, :axis [0.0 0.0 1.0]},
        :versor
        {:xlate [0.0 0.0 -952.32],
         :rotate
         [0.0
          0.7071067811865476
          -0.7071067811865476
          0.0]}})}}
    [[m1-link-name "LOWER_AXIS"]
     {:e [825.0 5444.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}]
    [[m2-link-name "CENTER_AXIS"]
     {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}])]

  #_(tt/fact "precondition satisfied" precon =not=> nil?)
  (in-line/assemble! kb m1 m2 motive)

  #_(tt/fact
   "in-line t2-r1 m1"
   (-> kb :link (get m1-link-name) deref) =>
   {:versor :m1-goal})

  #_(tt/fact
   "in-line t2-r1 m2"
   (-> kb :link (get m2-link-name) deref) =>
   {:versor :m2-goal})))

(test-t2-r1-pin)
