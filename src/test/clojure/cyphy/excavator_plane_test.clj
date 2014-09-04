
(ns cyphy.excavator-plane-test
  (:require [midje.sweet :as chk]
            [isis.geom.cyphy.cad-stax :as cyphy]

            [clojure.java.io :as jio]
            [clojure.data]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-joint :as meta-joint]
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

(with-open [fis (-> "excavator/excavator_total_plane.xml"
                    jio/resource jio/input-stream)]
  (let [kb (cyphy/extract-knowledge-from-cad-assembly fis)
        constraints (:constraint kb)
        exp-constraints nil #_(meta-joint/expand-higher-constraints constraints)

        ;;  _ (pp/pprint ["exp-con:" exp-constraints])

        constraint-checker-arm2
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


        constraint-checker-arm-cyl-a-j1
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

        constraint-checker-arm-cyl-a-j2
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
        (chk/contains
         '[])

        mark-checker
        (chk/contains
         '[])


        mark-checker-2
        (chk/contains
         '[])


        success-checker
        (chk/contains
         '[])


        failure-checker (chk/contains '[])
        ]


    (chk/facts "about the parsed cad-assembly file with :planar"
           (chk/fact "arm2" constraints => (chk/contains constraint-checker-arm2))
           (chk/fact "arm cyl a first" constraints => (chk/contains constraint-checker-arm-cyl-a-j1))
           (chk/fact "arm cyl a second" constraints => (chk/contains constraint-checker-arm-cyl-a-j2))
           #_(chk/fact "about the initial link settings" (:link kb) => link-checker)
           #_(chk/fact "about the base link id" (:base kb) => "{cd51d123-aab8-4d6e-b27f-fd94701e0007}|1")
           #_(chk/fact "about the initial marker invariants" (:mark kb) => mark-checker)


           #_(chk/fact "about the expanded constraints" exp-constraints => expanded-constraint-checker))


    #_(let [result (position-analysis kb exp-constraints)
            [success? result-kb result-success result-failure] result
            {result-mark :mark result-link :link} result-kb ]

        ;; (pp/pprint result-success)
        ;; (pp/pprint result-link)
        (chk/facts "about results of linkage-assembly"
               (chk/fact "about the mark result" result-mark => mark-checker-2)
               (chk/fact "about the link result" result-link => link-checker-2)
               (chk/fact "about the success result" result-success => success-checker)
               (chk/fact "about the failure result" result-failure => failure-checker) )

        #_(with-open [fis (-> "excavator/excavator_total_plane.xml"
                              jio/resource jio/input-stream)
                      fos (-> "/tmp/excavator_total_plane_aug.xml"
                              jio/output-stream)]

            (cyphy/update-cad-assembly-using-knowledge fis fos kb) ) ) ))


