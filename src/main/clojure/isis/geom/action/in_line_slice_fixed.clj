(ns isis.geom.action.in-line-slice-fixed
  "The table of rules for the in-line constraint where
  the point marker is FIXED and the line is mobile."
  (:require [isis.geom.machine
             [geobj :refer [translate
                           vec-diff
                           gmp normalize]]]
            [isis.geom.action  [auxiliary :as dof]]
            [isis.geom.model [invariant :as invariant]]))
