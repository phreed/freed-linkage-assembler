(ns testing.test-utils
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.machine.misc :as misc]))

(defn- ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (try
    (clojure.walk/postwalk
     #(if (misc/reference? %) [:ref @%] %) form)
    (catch Exception ex
      (str "ref->str exception: " ))))


(tt/facts "misc"
       (tt/fact "does the ref->str work"
             (ref->str
              {"{BOOM}"
               (ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 3},
                 :rdof {:# 3}}),
               "{CARRIAGE}"
               (ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 3},
                 :rdof {:# 3}}),
               "{ASSY}|1"
               (ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 0},
                 :rdof {:# 0}})})
             =>
             '{"{BOOM}"
               (:ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 3},
                 :rdof {:# 3}}),
               "{CARRIAGE}"
               (:ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 3},
                 :rdof {:# 3}}),
               "{ASSY}|1"
               (:ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 0},
                 :rdof {:# 0}})})

       (tt/fact "does the ref->str work"
             (ref->str
              {:loc (ref #{["{ASSY}|1"]}),
               :dir (ref #{["{ASSY}|1"]}),
               :twist (ref #{["{ASSY}|1"]})} )
             =>
             '{:loc (:ref #{["{ASSY}|1"]}),
              :dir (:ref #{["{ASSY}|1"]}),
              :twist (:ref #{["{ASSY}|1"]})} ))
