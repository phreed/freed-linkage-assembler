(ns assemble.parallel-z-test
  "Test the projection of versors"
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.action [parallel-z-slice :as xlice]]))


;; #_(["not-implemented parallel-z-slice :t2r3"
;; "m1"
;; [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "UPPER_CYLINDER_AXIS"]
;;  {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}]
;; {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
;;  :tdof {:# 1, :point [-5302.02 3731.18 600.0]},
;;  :rdof {:# 3}}
;; "m2"
;; [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "CYLINDER_AXIS"]
;;  {:e [-1703.15 6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}]
;; {:versor
;;  {:xlate [-58.51337709985842 117.43615244479886 138.75942078118214],
;;   :rotate [1.0 0.0 0.0 0.0]},
;;  :tdof
;;  {:# 2,
;;   :point [-5302.02 3731.18 600.0],
;;   :plane [-325.3573770998584 7260.476152444799 -288.3405792188179]},
;;  :rdof {:# 3}}])

(let [m1-link-name "{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}"
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
        }}
      m1
      [[m1-link-name "UPPER_CYLINDER_AXIS"]
       {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}]
      m2
      [[m2-link-name "CYLINDER_AXIS"]
       {:e [-1703.15 6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}]

      assy-result (xlice/assemble!->t2-r3 kb m1 m2) ]

  (pp/pprint kb)
  (tt/fact "parallel-z-slice :t2r3"
           @(get-in kb [:link m1-link-name]) => {:versor :m1-goal}))

