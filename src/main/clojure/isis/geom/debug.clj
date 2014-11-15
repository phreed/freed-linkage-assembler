(ns isis.geom.debug
(:require [clojure.pprint :as pp]
          [isis.geom.action
           [in-line-o2p-slice :as o2p]
           [in-line-p2o-slice :as p2o] ]
          [isis.geom.position-dispatch :as ms]) )

(defn foo [nspace] (str nspace))
(class 'o2p)
(foo 'o2p)

(defmulti assemble! #(println "hey") :default nil)

(def for-later-use (create-ns 'my-namespace))
(the-ns for-later-use)
(alias 'string 'clojure.string)

(defn the-alias [name] (get (ns-aliases *ns*) name))
(ns-name (the-ns 'user))
(ns-aliases 'user)
(seq {:o2p 'o2p :p2o 'p2o})
(pp/pprint (macroexpand-1 '(ms/defmethod-transform assemble!
      {:o2p 'o2p :p2o 'p2o}) )
 )


(defn contextual-eval [ctx expr]
    (eval
        `(let [~@(mapcat (fn [[k v]] [k `'~v]) ctx)]
             ~expr)))

(defmacro local-context []
    (let [symbols (keys &env)]
        (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))

(defn readr [prompt exit-code]
    (let [input (clojure.main/repl-read prompt exit-code)]
        (if (= input ::tl)
            exit-code
             input)))

;;make a break point
(defmacro break []
  `(clojure.main/repl
    :prompt #(print "debug=> ")
    :read readr
    :eval (partial contextual-eval (local-context))))
