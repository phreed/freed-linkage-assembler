(ns isis.geom.action.in-plane-slice-moves
  "The table of rules."
  (:require [isis.geom.machine
             [geobj :refer [translate
                           vec-diff
                           gmp normalize]]]
            [isis.geom.action
             [auxiliary :refer [dof-1r:p->p
                                dof-3r:p->p]] ]
             [isis.geom.model.invariant :as invariant]))
