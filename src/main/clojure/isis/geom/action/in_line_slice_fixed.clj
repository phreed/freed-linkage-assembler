(ns isis.geom.action.in-line-slice-fixed
  "The table of rules."
  (:require [isis.geom.machine
             [geobj :refer [translate
                           vec-diff
                           gmp normalize]]]
            [isis.geom.action
             [auxiliary :refer [dof-1r:p->p
                                dof-3r:p->p]] ]
             [isis.geom.model.invariant
             :refer [set-link-invariant!
                     set-marker-invariant!]]))
