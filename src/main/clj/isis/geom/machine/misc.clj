(ns isis.geom.machine.misc
  "functions that I am suprised are not in core.")


(defprotocol Ireference? (reference? [this]))
(extend-type java.lang.Object Ireference? (reference? [this] false))
(extend-type nil Ireference? (reference? [this] false))
(extend-type clojure.lang.Ref Ireference? (reference? [this] true))
(extend-type clojure.lang.Agent Ireference? (reference? [this] true))


