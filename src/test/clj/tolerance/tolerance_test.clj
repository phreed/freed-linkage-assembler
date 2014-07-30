(ns tolerance.tolerance-test
  "test the tolerance functions."
  (:require [midje.sweet :refer [facts fact]]

            [isis.geom.machine.tolerance :as tol]))

(facts "about tolerances"
       (fact "about near-same?"
             (tol/near-same? :default [1 2 3] [1 2 3]) => true)
       (fact "about near-same? differ in value"
             (tol/near-same? :default [1 2 3] [1 3 2]) => false)
       (fact "about near-same? differ is arity"
             (tol/near-same? :default [1 2 3] [1 2]) => false)

       (fact "about tol/near-equal?"
             (tol/near-equal? :default 1 2 3) => false))
