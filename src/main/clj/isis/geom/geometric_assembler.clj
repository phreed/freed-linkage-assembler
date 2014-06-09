(ns isis.geom.geometric-assembler
  (:require isis.geom.machine.functions)
  (:import (c3ga_pkg c3ga) ))


(defn -main []
  "This program proves the capablity of dof analysis in solving loops."
  (println " java version = "(System/getProperty "java.vm.version"))

  #_(let [ mv (c3ga_pkg.mv.)]
    (.set mv 25.0)
    (println "multi-vector =" (.toString mv)))

  #_(let [ e1 (c3ga_pkg.c3ga/vectorE1)
         e2 (c3ga_pkg.c3ga/vectorE2)
         e3 (c3ga_pkg.c3ga/vectorE3)
         e1+e2 (c3ga_pkg.c3ga/add e1 e2)]
    (println "e1 + e2 = " (.toString e1+e2)))

  #_(println "count all relevant triple-loops: " (count (all-triple-loops)))

  ;; validate all permutations of the triple-loop
  ;; (loop-test (all-triple-loops))
  )



