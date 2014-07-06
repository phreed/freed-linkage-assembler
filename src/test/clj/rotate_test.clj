

(ns rotate-test
  "Sample assembly for rotate (and translate)."
  (:require [expectations :refer :all]
            [isis.geom.machine.misc :as misc]
            [isis.geom.model
             [joint :refer [joint-primitive-map]]
             [graph :refer [make->link make->marker
                            port->expand
                            port-pair->make-constraint
                            graph->init-invariants
                            joint-pair->constraint
                            joints->constraints]]
             [invariant :refer [init-marker-invariant-s
                                init-link-invariant-s
                                init-link-invariant
                                marker->add-invariant!
                                make->invariant]]]
            [isis.geom.action-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-slice]
             [helical-slice]
             [in-line-slice]
             [in-plane-slice]
             [offset-x-slice]
             [offset-z-slice]
             [parallel-z-slice]]))
