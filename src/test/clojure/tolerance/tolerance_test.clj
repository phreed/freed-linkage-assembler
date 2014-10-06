(ns tolerance.tolerance-test
  "test the tolerance functions."
  (:require [midje.sweet :as tt]
            [isis.geom.machine.tolerance :as tol]))

(tt/facts "about tolerances"
       (tt/fact "about near-same?"
             (tol/near-same? :default [1 2 3] [1 2 3]) => true)
       (tt/fact "about near-same? differ in value"
             (tol/near-same? :default [1 2 3] [1 3 2]) => false)
       (tt/fact "about near-same? differ is arity"
             (tol/near-same? :default [1 2 3] [1 2]) => false)

       (tt/fact "about tol/near-equal?"
             (tol/near-equal? :default 1 2 3) => false))
