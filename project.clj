(defproject geometric-assembler "0.1.0-SNAPSHOT"
  :description "develop examples for geometric assembly"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clj"]
  :test-paths ["src/test/clj"]
  ;; :java-source-paths ["src/main/java"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.match "0.2.1"]
                 [org.clojure/tools.logging "0.3.0"]
                 [expectations "2.0.7"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :plugins [[lein-expectations "0.0.7"]
            [lein-autoexpect "1.2.2"]
            [lein-ancient "0.5.5"]]
  :main isis.geom.linkage-assembler
  :target-path "target/%s"
  :profiles {
             :uberjar {:aot :all}
             :dev {:aot :all} }
  :aliases {"slamhound" ["run" "-m" "slam.hound"]} )
