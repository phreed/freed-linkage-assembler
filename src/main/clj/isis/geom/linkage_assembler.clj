(ns isis.geom.linkage-assembler
  (:require [isis.geom.machine
             [c3ga :as c3ga]]))


(defn -main [& args]
  "This program proves the capablity of dof analysis in solving loops."
  (println " java version = "(System/getProperty "java.vm.version"))

  #_(c3ga/is-c3ga-working?)

  #_(println "count all relevant triple-loops: " (count (all-triple-loops)))

  ;; validate all permutations of the triple-loop
  ;; (loop-test (all-triple-loops))
  )



