(ns tolerance-test
  "test the tolerance functions."
  (:require [expectations :refer :all]
            [isis.geom.machine.tolerance :as tol]))

(expect true (tol/equivalent? :default [1 2 3] [1 2 3]))
(expect false (tol/equivalent? :default [1 2 3] [1 3 2]))
(= false (tol/equivalent? :default [1 2 3] [1 2]))

(expect false (tol/in-range? :default 1 2 3))
