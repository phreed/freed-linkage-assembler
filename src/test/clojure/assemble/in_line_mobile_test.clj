(ns assemble.in-line-mobile-test
  "Test the in-line-mobile assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.action
             [in-line-dispatch :as in-line]]))


(defn test-t2-r1 []
  (let
    [m1-link-name "{CARRIAGE-BOOM-UPPER-PIN}"
     m2-link-name "{CARRIAGE}"
     [kb m1 m2 motive :as precon]
     (in-line/precondition
      {:invar
       {:dir (ref #{m2-link-name}),
        :twist (ref #{m2-link-name}),
        :loc (ref #{m2-link-name})},
       :link
       {m1-link-name
        (ref
         {:name "PIN",
          :tdof {:# 2,
                 :point [0.0 0.0 -1636.3199999999997],
                 :plane {:e [0.0 0.0 -1636.32], :n [0.0 0.0 1.0]}},
          :rdof {:# 1, :axis [0.0 0.0 1.0]},
          :versor
          {:xlate [0.0 0.0 -952.3199999999996],
           :rotate
           [6.123233995736766E-17
            0.7071067811865476
            -0.7071067811865476
            0.0]}}),
        m2-link-name
        (ref
         {:name "UPPER_BODY",
          :tdof {:# 0, :point [0.0 0.0 0.0]},
          :rdof {:# 0},
          :versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]}})}}
      [[m1-link-name "CENTER_AXIS"]
       {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}]
      [[m2-link-name "UPPER_AXIS"]
       {:e [1457.0 4436.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}])]

    (tt/fact "precondition satisfied" precon =not=> nil?)
    (tt/fact "what is the motive" motive => :mobile)

    (in-line/assemble! kb m1 m2 motive)

    (tt/fact
     "in-line-mobile :t2-r1 m1"
     (-> kb :link (get m1-link-name) deref) =>
     {:versor :m1-goal})

    (tt/fact
     "in-line-mobile :t2-r1 m2"
     (-> kb :link (get m2-link-name) deref) =>
     {:versor :m2-goal})))

(test-t2-r1)
