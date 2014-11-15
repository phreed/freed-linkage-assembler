(ns isis.geom.position-dispatch
  "The dispatch functions for performing actions."
  (:require  [clojure.pprint :as pp]) )



(defmulti constraint-attempt?
  "Attempt to make invariant one or more properties
  of the links referenced by the indicated markers.
  Update the geometry invariant to indicate the
  realized DoF predicates and  other invariants.

  Return - nil or false indicating that the
  attempt failed and the constraint could not be
  satisfied.  Any other result indicates that the
  constraint has been applied.

  Must return on of the allowed return codes.
  see position-analysis for a list. "
  (fn [kb constraint] (:type constraint))

  :default
  (fn [kb constraint]
    (pp/pprint "constraint-attempt-default")
    nil))

(defmacro defmethod-transform
  "Generate the defmethods for the multifn.
  e.g.
  (defmethod assemble!
  {:tdof 0 :rdof 0 :motive :o2p}
  [kb point line motive] (fixed/assemble!->t0-r0 kb m1 m2))"
  [multifn nspaces]
  (cond
   (map? nspaces)
   `(do
      ~@(for [tdof [0 1 2 3] rdof [0 1 2 3]
              [motive nspace] (seq nspaces)]
          `(defmethod ~multifn
             {:tdof ~tdof :rdof ~rdof :motive ~motive}
             [kb# m1# m2# motive#]
             (~(symbol (str (eval nspace))
                       (str "assemble!->t" tdof "-r" rdof))
               kb# m1# m2# ))))
   :else
   `(do
      ~@(for [tdof [0 1 2 3] rdof [0 1 2 3] ]
          `(defmethod ~multifn
             {:tdof ~tdof :rdof ~rdof}
             [kb# m1# m2#]
             (~(symbol (str (eval nspaces))
                       (str "assemble!->t" tdof "-r" rdof))
               kb# m1# m2# ))))) )


(defn- test-template
  "Print a message that can be the basis for a test (cut & paste). "
  [xform nspace kb m1 m2 motive]
  {:pre [(string? xform)  (string? nspace)
         (map? kb)  (vector? m1) (vector? m2)]}

  (let [[[m1-link-name m1-proper-name] m1-payload] m1
        [[m2-link-name m2-proper-name] m2-payload] m2
        m1-link @(get-in kb [:link m1-link-name])
        m2-link @(get-in kb [:link m2-link-name])]
    (pp/pprint
     `(~'defn ~(symbol (str "test-" (name xform))) []
              (~'let [~'m1-link-name ~m1-link-name
                      ~'m2-link-name ~m2-link-name
                      [~'kb ~'m1 ~'m2 :as ~'precon]
                      (~(symbol (str nspace "/precondition"))
                        {:link
                         {~'m1-link-name (~'ref ~m1-link)
                          ~'m2-link-name (~'ref ~m2-link) }

                         :invar {:loc (~'ref #{~'m1-link-name})
                                 :dir (~'ref #{~'m1-link-name})
                                 :twist (~'ref #{~'m1-link-name})} }
                        [[~'m1-link-name ~m1-proper-name] ~m1-payload]
                        [[~'m2-link-name ~m2-proper-name] ~m2-payload]) ]

                     (tt/fact "precondition satisfied" ~'precon ~'=not=> ~'nil?)
                     (~(symbol (str nspace "/assemble!")) ~'kb ~'m1 ~'m2)

                     (tt/fact
                      ~(str nspace " " xform " m1")
                      (~'-> ~'kb :link (~'get ~'m1-link-name) ~'deref) ~'=>
                      {:versor :m1-goal})

                     (tt/fact
                      ~(str nspace " " xform " m2")
                      (~'-> ~'kb :link (~'get ~'m2-link-name) ~'deref) ~'=>
                      {:versor :m2-goal}))))))

(defn show-constraint
  "Show a summary of the applied constraint."
  ([kb nspace dispatch-fn m1 m2]
   (pp/fresh-line)
   (println
    (str (format "%-17s: " nspace)
         (dispatch-fn kb m1 m2)
         (format " mobile: [%-12s %12s] " (ffirst m2) (second (first m2)))
         (format " static: [%-12s %12s] " (ffirst m1) (second (first m1))) )))

  ([kb nspace dispatch-fn m1 m2 motive]
   (let [dispatch (select-keys (dispatch-fn kb m1 m2 motive) [:rdof :tdof])
         [ms-n mm-n mo] (condp = motive
                       :o2p [(first m1) (first m2) "o2p"]
                       :p2o [(first m2) (first m1) "p2o"]) ]
     (pp/fresh-line)
     (println
      (str (format "%-12s %-4s: " nspace mo)
           dispatch
           (format " mobile: [%-12s %12s] " (first mm-n) (second mm-n))
           (format " static: [%-12s %12s] " (first ms-n) (second ms-n)) ) ))))

(defn dump
  "Print a message building a test for this call. "
  ([ex dispatch-fn nspace kb m1 m2]
   (let [dispatch (dispatch-fn kb m1 m2)]
     (pp/pprint ex)
     (pp/pprint (str "Dump TEST-TEMPLATE ================ "
                     nspace " " dispatch))
     (test-template (str "t" (:tdof dispatch) "-r" (:rdof dispatch))
                    nspace kb m1 m2 :none)
     (if-not (nil? ex)
       (println (.printStackTrace ex System/out) ) ) ))

  ([ex dispatch-fn nspace kb m1 m2 motive]
   (let [dispatch (dispatch-fn kb m1 m2 motive)]
     (pp/pprint ex)
     (pp/pprint (str "Dump TEST-TEMPLATE ================ "
                     nspace " " dispatch))
     (test-template (str "t" (:tdof dispatch) "-r" (:rdof dispatch))
                    nspace kb m1 m2 motive)
     (if-not (nil? ex)
       (println (.printStackTrace ex System/out) ) ) ))  )


(defn unimpl
  "Print a message indicating that the transform is not implemented"
  [xform nspace kb m1 m2]
  (pp/pprint (str "not-implemented " nspace " " xform))
  (test-template xform nspace kb m1 m2)
  :not-implemented )

(defn unreal
  "Print a message indicating that the transform should not be reachable."
  [xform nspace kb m1 m2]
  (pp/pprint (str "not-reachable " nspace " " xform))
  :not-realizable )
