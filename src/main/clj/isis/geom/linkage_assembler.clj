(ns isis.geom.linkage-assembler
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.java.io :as jio]

            [isis.geom.lang.cyphy-cad
             :refer [graph-from-cyphy-file
                     graph-to-cyphy-file]]
            [isis.geom.analysis.position-analysis
             :refer [position-analysis]]))


(def ^{:private true} parse-options
  ;; An option with a required argument
  [["-i" "--input INPUT" "cyphy2cad file (CADAssembly.xml)"
    :id "input"
    :default "CADAssembly.xml"
    :parse-fn #(jio/as-file %)
    :validate [#(.exists %) "Must be a valid file path"]]
   ["-o" "--output OUTPUT" "cyphy2cad file (CADAssembly.xml) augmented with versors"
    :id "output"
    :default "CADAssembly_augmented.xml"
    :parse-fn #(jio/as-file %)
    :validate [#(true) "Must be a valid writable file path"] ]
   ;; A non-idempotent option
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])


(defn usage [options-summary]
  (->> ["This program uses dof analysis to assemble components."
        ""
        "Usage: linkage-assembler [options]"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn -main

  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args parse-options)]
    (cond
     (:help options) (exit 0 (usage summary))
     (> 1 (count arguments)) (exit 1 (usage summary))
     errors (exit 1 (error-msg errors)))

    (when (< 2 (:verbosity options))
      (println " java version = "(System/getProperty "java.vm.version")) )

    (let [graph (graph-from-cyphy-file (:input options))
          constraints (:constraint graph)
          [success? result-graph result-success result-failure] (position-analysis graph constraints)]

      (graph-to-cyphy-file result-graph (:output options))  )))



