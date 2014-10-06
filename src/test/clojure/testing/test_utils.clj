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
              {"{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"
               (ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 3},
                 :rdof {:# 3}}),
               "{51f63ec8-cde2-4ac0-886f-7f9389faad04}"
               (ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 3},
                 :rdof {:# 3}}),
               "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"
               (ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 0},
                 :rdof {:# 0}})})
             =>
             '{"{c1fb29d9-0a81-423c-bc8f-459735cb4db3}"
               (:ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 3},
                 :rdof {:# 3}}),
               "{51f63ec8-cde2-4ac0-886f-7f9389faad04}"
               (:ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 3},
                 :rdof {:# 3}}),
               "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"
               (:ref
                {:versor {:xlate [0.0 0.0 0.0], :rotate [1.0 0.0 0.0 0.0]},
                 :tdof {:# 0},
                 :rdof {:# 0}})})

       (tt/fact "does the ref->str work"
             (ref->str
              {:loc (ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}),
               :dir (ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}),
               :twist (ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]})} )
             =>
             '{:loc (:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}),
              :dir (:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]}),
              :twist (:ref #{["{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1"]})} ))
