(defproject geometric-assembler "0.1.0-SNAPSHOT"
  :description "develop examples for geometric assembly"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :resource-paths ["src/main/resource/c3ga.jar"]
  :source-paths ["src/main/clj"]
  ;; :java-source-paths ["src/main/java"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.match "0.2.1"]
                 [lein-kibit "0.0.8"]
                 [org.clojure/tools.logging "0.2.6"]]
  :main isis.geometric-assembler.core
  :target-path "target/%s"
  :profiles {
             :uberjar {:aot :all}
             :dev {:aot :all} } )
