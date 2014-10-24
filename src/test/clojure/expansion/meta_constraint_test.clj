 (ns expansion.meta-constraint-test
  (:require [midje.sweet :as tt]
            [clojure.pprint :as pp]
            [isis.geom.model.meta-constraint :as meta-constraint] ))



 (tt/fact
      "meta expanded "
  (meta-constraint/expand-csys
      {:m1 [["{BAR-1}" "CS1"]
            {:e [50.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{BAR-2}" "CS0"]
             {:e [-50.0 0.0 0.0], :pi 0.5, :q [0.0 0.0 1.0]}],
        :type :csys} ) =>
  [{:m1 [["{BAR-1}" "CS1-origin"] {:e [50.0 0.0 0.0]}],
    :m2 [["{BAR-2}" "CS0-origin"] {:e [-50.0 0.0 0.0]}],
    :type :coincident}
   {:m1 [["{BAR-1}" "CS1-3x"] {:e [350.0 0.0 0.0]}],
    :m2 [["{BAR-2}" "CS0-3x"] {:e [-50.0 300.0 0.0]}],
    :type :coincident}
   {:m1 [["{BAR-1}" "CS1-4y"] {:e [50.0 400.0 0.0]}],
    :m2 [["{BAR-2}" "CS0-4y"] {:e [-450.0 0.0 0.0]}],
    :type :coincident}])

 (tt/fact
      "meta expanded "
  (meta-constraint/expand-csys
      {:m1 [["{BAR-1}" "CS1"]
            {:e [50.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}],
        :m2 [["{BAR-2}" "CS0"]
             {:e [-50.0 0.0 0.0], :pi 1.0, :q [1.0 1.0 0.0]}],
        :type :csys} ) =>
  [{:m1 [["{BAR-1}" "CS1-origin"] {:e [50.0 0.0 0.0]}],
    :m2 [["{BAR-2}" "CS0-origin"] {:e [-50.0 0.0 0.0]}],
    :type :coincident}
   {:m1 [["{BAR-1}" "CS1-3x"] {:e [350.0 0.0 0.0]}],
    :m2 [["{BAR-2}" "CS0-3x"] {:e [-50.0 299.99999999999994 0.0]}],
    :type :coincident}
   {:m1 [["{BAR-1}" "CS1-4y"] {:e [50.0 400.0 0.0]}],
    :m2 [["{BAR-2}" "CS0-4y"] {:e [349.9999999999999 0.0 0.0]}],
    :type :coincident}] )
