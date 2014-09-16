;;
;; This program is typically run as follows:
;;
;;  lein run -- -i  ./src/test/resources/excavator/excavator_boom_dipper_point.xml -o ./src/test/resources/excavator/cad_assembly_boom_dipper_aug.xml
;;
(ns isis.geom.linkage-assembler
  (:gen-class :main true)
  (:require [isis.geom.cyphy
             [cyphy-zip :as cyphy]
             [cad-stax :as stax]]

            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.java.io :as jio]

            [isis.geom.analysis.position-analysis
             :refer [position-analysis]]

            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-dispatch]
             [helical-dispatch]
             [in-line-dispatch]
             [in-plane-dispatch]
             [offset-x-dispatch]
             [offset-z-dispatch]
             [parallel-z-dispatch]]))


(def ^{:private true} parse-options
  ;; An option with a required argument
  [["-i" "--input INPUT" "cyphy2cad file (CADAssembly.xml)"
    :id :input
    :default "CADAssembly.xml"
    :parse-fn #(jio/as-file %)
    :validate [#(.exists  %) "Must be a valid file path"]]
   ["-o" "--output OUTPUT" "cyphy2cad file (CADAssembly.xml) augmented with versors"
    :id :output
    :default "CADAssembly_augmented.xml"
    :parse-fn #(jio/as-file %)
    :validate [(constantly true) "Must be a valid writable file path"] ]
   ;; A non-idempotent option
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ;; A boolean option defaulting to nil
   ["-h" "--help"
    :id "help"]])


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
     (< 1 (count arguments)) (exit 1 (usage summary))
     errors (exit 1 (error-msg errors)))

    (when (< 2 (:verbosity options))
      (println " java version = "(System/getProperty "java.vm.version")) )

    (println " input file = "  (.toString (:input options)))
    (println " output file = "  (.toString (:output options)))
    (with-open [fis (-> (:input options) jio/input-stream)]
      ;; (let [kb (graph-from-cyphy-input-stream is)
      (let [kb (cyphy/knowledge-via-input-stream fis)
            constraints (:constraint kb)
            ;; here is the call to the position analysis
            [success? result-kb result-success result-failure]
            (position-analysis kb constraints)]
        (with-open [fis (-> (:input options) jio/input-stream)
                    fos (-> (:output options) jio/output-stream)]

          (stax/update-cad-assembly-using-knowledge fis fos kb) ) ) )))


