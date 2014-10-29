(ns assemble.in-line-fixed-test
  "Test the in-line-fixed assembly"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.action
             [in-line-dispatch :as in-line]]))


(defn test-t2-r1 []
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
         {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
          :tdof {:# 0, :point [0.0 0.0 0.0]},
          :rdof {:# 0}}),
        m2-link-name
        (ref
         {:versor
          {:xlate [0.0 0.0 -2320.3199999999997],
           :rotate [1.0 0.0 0.0 0.0]},
          :tdof
          {:# 2,
           :point [0.0 0.0 -1636.32],
           :plane [0.0 0.0 -1636.3199999999997]},
          :rdof {:# 1, :axis [0.0 0.0 1.0]}})}}
      [[m1-link-name "LOWER_AXIS"]
       {:e [825.0 5444.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}]
      [[m2-link-name "CENTER_AXIS"]
       {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}])]

    (tt/fact "precondition satisfied" precon =not=> nil?)
    (tt/fact "about motive" motive => :fixed)

    (tt/incipient-fact
     "about dispatch"
     (in-line/assemble-dispatch kb m1 m2 motive) =>
     {:tdof 2 :rdof 1 :motive motive})

    #_(in-line/assemble! kb m1 m2 motive)

    (tt/incipient-fact
     "in-line-fixed :t2-r1 m1"
     @(get-in kb [:link m1-link-name]) =>
     {:versor :m1-goal})

    (tt/incipient-fact
     "in-line-fixed :t2-r1 m2"
     @(get-in kb [:link m2-link-name]) =>
     {:versor :m2-goal})) )

(test-t2-r1)
