;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
(require '[midje.repl :as tr] '[clojure.repl :as cr])


(tr/autotest :dirs ".")

(tr/load-facts 'cyphy.excavator-stax-test )

(tr/recheck-fact)
;; (tr/source-of-last-fact-checked)
