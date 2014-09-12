
(ns cyphy.excavator-plane-test
  (:require [midje.sweet :as chk]
            [isis.geom.cyphy.cad-stax :as cyphy]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-constraint]
            [isis.geom.model.lower-joint :as lower-joint]
            [isis.geom.machine.misc :as misc]

            [isis.geom.analysis
             [position-analysis :refer [position-analysis]]]

            [isis.geom.machine.misc :as misc]
            [isis.geom.position-dispatch
             :refer [constraint-attempt?]]
            [isis.geom.action
             [coincident-slice]
             [helical-slice]
             [in-line-slice]
             [in-plane-slice]
             [offset-x-slice]
             [offset-z-slice]
             [parallel-z-slice]]))

(defn unref
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) @% %) form))

(with-open [fis (-> "excavator/excavator_total_plane.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/extract-knowledge-from-cad-assembly fis)
        constraints-orig (:constraint kb)
        constraints-meta (meta-constraint/expand-collection constraints-orig)
        constraints-lower (lower-joint/expand-collection constraints-meta)

        ;;  _ (pp/pprint ["exp-con:" exp-constraints])

        ;; The arm2 {a93..51f} is connected to
        ;; the boom {99c..264} via a revolute joint.
        con-chk-arm2boom
        '[{:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"]
            {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_CENTER_PLANE"]
            {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :type :planar}
          {:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_GUIDE"]
            {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_GUIDE"]
            {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
           :type :planar}
          {:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_AXIS"]
            {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_AXIS"]
            {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :type :linear}]

        con-chk-arm2boom-meta
        '[{:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"]
            {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_CENTER_PLANE"]
            {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :type :planar}
          {:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_GUIDE"]
            {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_GUIDE"]
            {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}],
           :type :planar}
          {:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_AXIS"]
            {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_AXIS"]
            {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :type :linear}]

        con-chk-arm2boom-lower
        '[{:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"]
            {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_CENTER_PLANE"]
            {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :type :in-plane}
          {:m2
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"]
            {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :m1
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_CENTER_PLANE"]
            {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :type :in-plane}]


        ;; The boom {99c..264} is connected to
        ;; a hydraulic-jack {7d2..491} via a revolute joint.
        con-chk-boom2jack
        '[{:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"]
            {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :m2
           [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "CYLINDER_PLANE"]
            {:e [-266.844 7143.04 -427.1],
             :pi 0.0,
             :q [-44.021 88.35 104.392]}],
           :type :planar}
          {:m1
           [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "UPPER_CYLINDER_AXIS"]
            {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :m2
           [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "CYLINDER_AXIS"]
            {:e [-1703.15 6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}],
           :type :linear}
          ]

        ;; The hydaulic-jack {7d2..491} is connected to
        ;; the arm {a93..51f} via a revolute joint.
        con-chk-jack2arm
        '[{:m1
           [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "JACK_PLANE"]
            {:e [-266.844 7143.04 -427.1],
             :pi 0.0,
             :q [44.021 -88.34 -16.042]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "CYLINDER_CENTER_PLANE"]
            {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :type :planar}
          {:m1
           [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "JACK_AXIS"]
            {:e [1243.25 7949.95 -726.974], :pi 0.0, :q [-27.12 54.43 9.884]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "CYLINDER_AXIS"]
            {:e [2916.39 464.857 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :type :linear}
          ]

        con-chk-grd
        '[{:m1
           [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "JACK_PLANE"]
            {:e [-266.844 7143.04 -427.1],
             :pi 0.0,
             :q [44.021 -88.34 -16.042]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "CYLINDER_CENTER_PLANE"]
            {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}],
           :type :planar}
          {:m1
           [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "JACK_AXIS"]
            {:e [1243.25 7949.95 -726.974], :pi 0.0, :q [-27.12 54.43 9.884]}],
           :m2
           [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "CYLINDER_AXIS"]
            {:e [2916.39 464.857 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}],
           :type :linear}
          ]

        expanded-constraint-checker
        (chk/contains
         '[])

        link-checker
        '{
          "{7d252256-d674-4ab2-a8d0-add7baff5491}"
          {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
           :tdof {:# 3} :rdof {:# 3}}
          "{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}"
          {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
           :tdof {:# 3} :rdof {:# 3}}
          "{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}"
          {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
           :tdof {:# 3} :rdof {:# 3}}
           }


        link-checker-final
        '{
          "{7d252256-d674-4ab2-a8d0-add7baff5491}"
          {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
           :tdof {:# 3} :rdof {:# 3}}
          "{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}"
          {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
           :tdof {:# 3} :rdof {:# 3}}
          "{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}"
          {:versor {:xlate [0.0 0.0 0.0] :rotate [1.0 0.0 0.0 0.0]}
           :tdof {:# 3} :rdof {:# 3}}
           }

        mark-checker
        {:loc #{["{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1"]}
          :x #{["{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1"]}
          :z #{["{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1"]}}

        mark-checker-final
        (chk/contains
         '[])


        failure-checker (chk/contains '[])
        ]


    (chk/facts
     "about the parsed cad-assembly file with :planar"
               (chk/fact "arm2" constraints-orig => (chk/contains con-chk-arm2boom))
               (chk/fact "arm cyl a first" constraints-orig => (chk/contains con-chk-boom2jack))
               (chk/fact "arm cyl a second" constraints-orig => (chk/contains con-chk-jack2arm))
               (chk/fact "about the initial link settings" (unref (:link kb)) => (chk/contains link-checker))
               (chk/fact "about the base link id" (:base kb) => "{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1")
               (chk/fact "about the initial marker invariants" (unref (:mark kb)) => mark-checker)


               (chk/fact "arm2 meta expanded" constraints-meta => (chk/contains con-chk-arm2boom-meta))
               (chk/fact "arm2 lower expanded" constraints-lower => (chk/contains con-chk-arm2boom-lower)) )

    (let [result (position-analysis kb constraints-lower)
            [success? result-kb result-success result-failure] result
            {result-mark :mark result-link :link} result-kb ]

        ;; (pp/pprint result-success)
        ;; (pp/pprint result-link)
        (chk/facts "about results of linkage-assembly"
                   #_(chk/fact "about the mark result" result-mark => mark-checker-2)
                   #_(chk/fact "about the link result" result-link => link-checker-2)
                   #_(chk/fact "about the success result" result-success => success-checker)
                   #_(chk/fact "about the failure result" result-failure => failure-checker) )

        #_(with-open [fis (-> "excavator/excavator_total_plane.xml"
                              jio/resource jio/input-stream)
                      fos (-> "/tmp/excavator_total_plane_aug.xml"
                              jio/output-stream)]

            (cyphy/update-cad-assembly-using-knowledge fis fos kb) ) ) ))


