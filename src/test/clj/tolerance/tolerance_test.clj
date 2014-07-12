(ns tolerance.tolerance-test
  "test the tolerance functions."
  (:require [expectations :refer :all]
            [isis.geom.machine.tolerance :as tol]))

(expect true (tol/near-same? :default [1 2 3] [1 2 3]))
(expect false (tol/near-same? :default [1 2 3] [1 3 2]))
(= false (tol/near-same? :default [1 2 3] [1 2]))

(expect false (tol/near-equal? :default 1 2 3))
