(ns versor.versor-test
   "The functions for developing versors."
  (:require [midje.sweet :refer [facts fact]]
            [isis.geom.algebra [versor :as versor]]))


(facts "verifying dimension independent functions"
       (fact "build a blade from a named basis"
             (versor/basis-bit "e1234") => 15 )
       (fact "generate a named blade from a bitmap"
             (versor/basis-string 15) => "e1234")
       )


(clojure.pprint/pprint
 (macroexpand-1 '(versor/versor->create c3ga {:foo 'bar})))


(versor/versor->create c3ga
  {
   :conformal true
   :bases #{:e1 :e2 :e3 :e4 :e5 }
   :metric [1  1  1  1  -1]} )

(facts "verifying dimension dependent functions"
         (fact "construct a point"
               (pnt 1 2 3 4 5) =>
               '{:e1 1 :e2 2 :e3 3 :e4 4 :e5 5})
       )




