(ns versor.versor-apply-test
  "Sample assembly for rotate (and translate)."
  (:require [midje.sweet :as tt]
            [isis.geom.algebra [versor :as versor]]
            [isis.geom.algebra [geobj :as ga]]))


(let [g1-bc 2r010
      g2-bc 2r01010
      g2-bc-1 2r01000
      g1-blade (versor/make->blade g1-bc 4.3)
      g2-blade (versor/make->blade g2-bc 5.2)]

  (tt/facts "verifying dimension independent functions"

         (tt/fact "make a blade" g1-blade =>  '{:bitmap 2 :weight 4.3} )
         (tt/fact "check blade grade"
               (versor/grade (:bitmap g1-blade)) => 1
               (versor/grade (:bitmap g2-blade)) => 2 )
         (tt/fact "check blade sign"
               (versor/sign g1-bc g2-bc) => 1
               (versor/sign g2-bc g1-bc) => -1 )
         (tt/fact "check blade product"
               (versor/product g2-bc g1-bc) => {:bitmap 2r1000 :weight -1.0}
               (versor/product g1-bc g2-bc) => {:bitmap 2r1000 :weight 1.0} )
         (tt/fact "check outer product"
               (versor/outer g2-bc g1-bc) => {:bitmap 2r0 :weight 0.0}
               (versor/outer g1-bc g2-bc-1) => {:bitmap 2r1010 :weight 1.0} )
         (tt/fact "check involute"
               (versor/involute g1-bc) => {:bitmap 2r0010 :weight -1.0}
               (versor/involute g2-bc) => {:bitmap 2r1010 :weight 1.0} )
         (tt/fact "check reversed"
               (versor/reversed g1-bc) => {:bitmap 2r0010 :weight 1.0}
               (versor/reversed g2-bc) => {:bitmap 2r1010 :weight -1.0} )
         (tt/fact "check conjugate"
               (versor/conjugate g1-bc) => {:bitmap 2r0010 :weight -1.0}
               (versor/conjugate g2-bc) => {:bitmap 2r1010 :weight -1.0} )
         (tt/fact "build a bitmap from a named basis"
               (versor/basis-bit "e1234") => 15 )
         (tt/fact "generate a named blade from a bitmap"
               (versor/basis-string 15) => "e1234")
         (tt/fact "compile a set of bases"
               (versor/basis-bits ["s" "e1" "e45"]) => [0 2r1 2r11000])
         (tt/fact "compress a multivector"
               (versor/compress
                (map (fn [[bn wt]]
                       (versor/make->blade
                        (versor/basis-bit bn) wt))
                     [["s" 4.0]
                      ["e2" 3.2]
                      ["e2" -1.2]
                      ["e45" 6.0]]))
               =>
               [(versor/make->blade 0 4.0)
                (versor/make->blade 2 2.0)
                (versor/make->blade 24 6.0)] )
         ))

(tt/facts "utility functions"
       (tt/fact "check debugging and logging aids"
             (println-str (versor/print-lines
              "This is a line.
              So is this.
              And this."
              1 2) )
             => #"1\t\s+So is this.\n"))


(tt/facts "generate basis functions"
       (tt/fact (macroexpand-1
              '(versor/versor->create c3ga {:foo 'bar}))
             =>
             '(do
                (clojure.core/defn i* [a b])
                (clojure.core/defn o* [a b])
                (clojure.core/defn t* [a b])
                (clojure.core/defn dual [a])
                (clojure.core/defn
                  pnt
                  [x y z no ni]
                  {:e1 x, :e2 y, :e3 z, :e4 no, :e5 ni}))))


(versor/versor->create c3ga
                       {
                        :conformal true
                        :bases #{:e1 :e2 :e3 :e4 :e5 }
                        :metric [1  1  1  1  -1]} )

(tt/facts "verifying dimension dependent functions"
       (tt/fact "construct a point"
             (pnt 1 2 3 4 5) =>
             '{:e1 1 :e2 2 :e3 3 :e4 4 :e5 5})
       )




