;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
(require 'clojure.data)

(map (fn [[a b]]
       [b a])
     {:b 'foo :c 'bar})


(def print-lines
  "Write out the specified number of lines from the input."
  (let [writer (ref println)
        eol #".*(?:(?:\r\n|\n|\r)|$)"]
    (fn [text from to]
      (->>
       (re-seq eol text)
       (drop from)
       (take (- to from))
       (map-indexed #(println (inc %1) "\t" %2))))))

(println-str
 (print-lines
 "this is a line
 and so is this.
 and this." 1 2))

(require '[isis.geom.machine.misc :as misc])

(defn ref->str
  "takes an arbitrary tree and replaces all futures
  with agnostic strings."
  [form]
  (clojure.walk/postwalk #(if (misc/reference? %) [:ref @%] %) form))

(defn- ref->checker
  "A checker that allows the names of references to be ignored."
  [rhs]
  (fn [lhs]
    (= rhs
       (clojure.walk/postwalk
        #(if (misc/reference? %) [:ref @%] %) lhs) )))

(def a {:a 'abc :d "efg" :h (ref {:i 32})})

(update-in a [:a] (fn [o n] n) 45)
(def b (assoc-in a [:h]  '(:ref {:i 32})))

((ref->checker b) a)
(= (ref->str a) b)

(defn initial-board []
  [\r \n \b \q \k \b \n \r
   \p \p \p \p \p \p \p \p
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \P \P \P \P \P \P \P \P
   \R \N \B \Q \K \B \N \R  ])

(let [file-key (int \a), rank-key (int \0)]
  (letfn [(file-component [file] (->> file-key (- (int file)) ))
          (rank-component [rank] (->> rank-key (- (int rank)) (- 8) (* 8)))
          (index [file rank]
                 (let [f (file-component file)
                       r (rank-component rank)]
                   (+ f r)))]
    (defn lookup [board pos]
      (let [[file rank] pos]
        (nth board (index file rank))))
    (defn whoa [] "whoa")))

(let [board (initial-board)]
  (lookup board "b1"))

(whoa)

(defn lookup2 [board pos]
(let [file-key (int \a)
      rank-key (int \0)
      [file rank] (map int pos)
      file-component (->> file-key (- file))
      rank-component (->> rank-key (- rank) (- 8) (* 8))
      index (+ file-component rank-component)]
  (nth board index)))

(let [board (initial-board)]
  (lookup2 board "c1"))

(+ 0 60/21)

(as-> [1 2 3] x
      (map inc x)
      (vec x)
      (conj x 5))

(require '[clojure.pprint :as pp])

(false? Boolean/FALSE)
(false? (Boolean/valueOf "false"))



(pp/pprint  [{:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"] {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_CENTER_PLANE"] {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :planar} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_GUIDE"] {:e [-3741.05 1103.98 1369.99], :pi 0.0, :q [-531.29 -717.57 0.0]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_GUIDE"] {:e [1150.48 864.911 -250.0], :pi 0.0, :q [-334.565 -371.573 0.0]}], :type :planar} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_AXIS"] {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2
[["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BOOM_AXIS"] {:e [2000.0 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :linear} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"] {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "CYLINDER_PLANE"] {:e [-266.844 7143.04 -427.1], :pi 0.0, :q [-44.021 88.35 104.392]}], :type :planar} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "UPPER_CYLINDER_AXIS"] {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "CYLINDER_AXIS"] {:e [-1703.15 6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}], :type :linear} {:m1 [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "JACK_PLANE"] {:e [-266.844 7143.04 -427.1], :pi 0.0, :q [44.021 -88.34 -16.042]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "CYLINDER_CENTER_PLANE"] {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :planar} {:m1 [["{7d252256-d674-4ab2-a8d0-add7baff5491}" "JACK_AXIS"] {:e [1243.25 7949.95 -726.974], :pi 0.0, :q [-27.12 54.43 9.884]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "CYLINDER_AXIS"] {:e [2916.39 464.857 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :linear} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"] {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "CENTER_PLANE"] {:e [0.0 0.0 -1636.32], :pi 0.0, :q [0.0 0.0 1.0]}], :type :planar} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "GUIDE_PLANE"] {:e [-2839.08 4470.16 1369.99], :pi 0.0, :q [-143.69 478.91 0.0]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "BOOM_GUIDE"] {:e [0.0 2591.01 0.0], :pi 0.0, :q [-196.231 154.9 0.0]}], :type :planar} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "BODY_AXIS"] {:e [-2058.7 4704.31 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "UPPER_AXIS"] {:e [1457.0 4436.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :linear} {:m1 [["{887781bd-a089-4cad-a93b-4c468fda00d1}" "CYLINDER_PLANE"] {:e [-266.844 7143.04 -427.1], :pi 0.0, :q [-44.021 88.35 104.392]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "LEFT_PLANE"] {:e [0.0 0.0 -1122.89], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :planar} {:m1 [["{887781bd-a089-4cad-a93b-4c468fda00d1}" "CYLINDER_AXIS"] {:e [-1703.15 6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "LOWER_AXIS"] {:e
[825.0 5444.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :linear} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "LEFT_PLANE"] {:e [-5302.02 3731.18 86.577], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{887781bd-a089-4cad-a93b-4c468fda00d1}" "JACK_PLANE"] {:e [-266.844 7143.04 -427.1], :pi 0.0, :q [44.021 -88.34 -16.042]}], :type :planar} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "BODY_CYLINDER_AXIS"]
{:e [-5483.58 3657.2 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{887781bd-a089-4cad-a93b-4c468fda00d1}" "JACK_AXIS"] {:e [1243.25 7949.95 -726.974], :pi 0.0, :q [-27.12 54.43 9.884]}], :type :linear} {:m1 [["{312dbcd9-789a-412d-bdb2-a6d46039387b}" "CYLINDER_PLANE"] {:e [-266.844 7143.04 -427.1], :pi 0.0, :q [-44.021 88.35 104.392]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "RIGHT_PLANE"] {:e [0.0 0.0 -2149.74], :pi 0.0, :q [0.0 0.0 1.0]}], :type :planar} {:m1 [["{312dbcd9-789a-412d-bdb2-a6d46039387b}" "CYLINDER_AXIS"] {:e [-1703.15
6384.66 -192.041], :pi 0.0, :q [-27.5 55.2 10.023]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "LOWER_AXIS"] {:e [825.0 5444.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :linear} {:m1 [["{312dbcd9-789a-412d-bdb2-a6d46039387b}" "JACK_PLANE"] {:e [-266.844 7143.04
-427.1], :pi 0.0, :q [44.021 -88.34 -16.042]}], :m2 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "RIGHT_PLANE"] {:e [-5302.02 3731.18 1113.423], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :planar} {:m1 [["{312dbcd9-789a-412d-bdb2-a6d46039387b}" "JACK_AXIS"] {:e [1243.25 7949.95 -726.974], :pi 0.0, :q [-27.12 54.43 9.884]}], :m2 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "BODY_CYLINDER_AXIS"] {:e [-5483.58 3657.2 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :linear} {:m1 [["{096654eb-065b-4fda-aa27-e49061dd5b5f}" "CENTER_PLANE"] {:e [460.0 0.0 0.0], :pi 0.0, :q [-1.0 0.0 0.0]}], :m2 nil, :type :planar} {:m1 [["{096654eb-065b-4fda-aa27-e49061dd5b5f}" "AXIS2"] {:e [0.0 900.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}], :m2 nil, :type :linear} {:m1 [["{096654eb-065b-4fda-aa27-e49061dd5b5f}" "CENTER_PLANE"] {:e [460.0 0.0 0.0], :pi
0.0, :q [-1.0 0.0 0.0]}], :m2 nil, :type :planar} {:m1 [["{096654eb-065b-4fda-aa27-e49061dd5b5f}" "AXIS1"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}], :m2 nil, :type :linear} {:m1 [["{096654eb-065b-4fda-aa27-e49061dd5b5f}" "LEFT_PLANE"] {:e [870.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}], :m2 nil, :type :planar} {:m1 [["{096654eb-065b-4fda-aa27-e49061dd5b5f}" "AXIS1"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0
0.0 0.0]}], :m2 nil, :type :linear} {:m1 [["{096654eb-065b-4fda-aa27-e49061dd5b5f}" "RIGHT_PLANE"] {:e [50.0 0.0 0.0], :pi 0.0, :q [-1.0 0.0 0.0]}], :m2 nil, :type :planar} {:m1 [["{096654eb-065b-4fda-aa27-e49061dd5b5f}" "AXIS1"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}], :m2 nil, :type :linear} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "LEFT_PLANE"] {:e [0.0 0.0 -660.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 nil, :type :planar} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "LINK_GUIDE"] {:e [-1123.39 1481.57 -146.69], :pi 0.0, :q [-497.26 -52.26 0.0]}], :m2 nil, :type :planar} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKETLINK_AXIS"] {:e [-984.131 156.602 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 nil, :type :linear} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "RIGHT_PLANE"] {:e [0.0 0.0 160.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 nil, :type :planar} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "LINK_GUIDE"] {:e [-1123.39 1481.57 -146.69], :pi 0.0, :q [-497.26 -52.26 0.0]}], :m2 nil, :type :planar} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKETLINK_AXIS"] {:e [-984.131 156.602 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 nil, :type :linear} {:m1 [["{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1" "TOP"] {:e [1.0 0.0 0.0]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "TOP"] {:e [1.0 0.0 0.0]}], :type :coincident} {:m1 [["{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1" "RIGHT"] {:e [0.0 1.0 0.0]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "RIGHT"] {:e [0.0 1.0 0.0]}], :type :coincident} {:m1 [["{3451cc65-9ad0-4f78-8a0c-290d1595fe74}|1" "FRONT"] {:e [0.0 0.0 1.0]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "FRONT"] {:e [0.0 0.0 1.0]}], :type :coincident} {:m1 [["{96a36430-bd86-49d2-a96a-7e1726e35dea}" "CENTER_PLANE"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_CENTER_PLANE"] {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :planar} {:m1 [["{96a36430-bd86-49d2-a96a-7e1726e35dea}" "CENTER_AXIS"] {:e [0.0 0.0 0.0], :pi
0.0, :q [1.0 0.0 0.0]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKETLINK_AXIS"] {:e [-984.131 156.602 0.0], :pi 0.0, :q [0.0
0.0 -1.0]}], :type :linear} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_CYLINDER_CENTER_PLANE"] {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{f35ae937-89bd-4c8e-bbcf-c15206bb1c5d}" "CENTER_PLANE"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}], :type :planar} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_CYLINDER_AXIS"] {:e [1510.0 1175.78 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{f35ae937-89bd-4c8e-bbcf-c15206bb1c5d}" "CENTER_AXIS"] {:e [0.0 0.0 0.0], :pi 0.0, :q [1.0 0.0 0.0]}], :type :linear} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"] {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{5b94b55f-3a19-4d41-9bbf-41cf1c447765}" "CENTER_PLANE"] {:e [0.0 0.0 684.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :planar} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "ARM_AXIS"] {:e [-8625.71 4720.65 905.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{5b94b55f-3a19-4d41-9bbf-41cf1c447765}" "CENTER_AXIS"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :linear} {:m1 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "CENTER_PLANE"] {:e [0.0 0.0 -1636.32], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{627ab157-62e1-485c-b797-8139c8f6c133}" "CENTER_PLANE"] {:e [0.0
0.0 684.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :planar} {:m1 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "LOWER_AXIS"] {:e [825.0 5444.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{627ab157-62e1-485c-b797-8139c8f6c133}" "CENTER_AXIS"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :linear} {:m1 [["{9f6b7815-51f1-466a-bd56-bcd988333c05}" "CENTER_PLANE"] {:e [0.0 0.0 684.0], :pi 0.0, :q [0.0 0.0
-1.0]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "CENTER_PLANE"] {:e [0.0 0.0 -1636.32], :pi 0.0, :q [0.0 0.0 1.0]}], :type :planar} {:m1 [["{9f6b7815-51f1-466a-bd56-bcd988333c05}" "CENTER_AXIS"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{dce1362d-1b44-4652-949b-995aa2ce5760}" "UPPER_AXIS"] {:e [1457.0 4436.8 98.6836], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :linear} {:m1 [["{fac74c1b-191e-44f0-9ce6-7874fa392d4d}" "CENTER_PLANE"] {:e [0.0 0.0 684.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_CENTER_PLANE"] {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :planar} {:m1 [["{fac74c1b-191e-44f0-9ce6-7874fa392d4d}" "CENTER_AXIS"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_AXIS"] {:e [-1480.92 100.0 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :linear} {:m1 [["{356c5337-4bd2-43f7-945b-3e3f6530d088}" "CENTER_PLANE"] {:e [0.0 0.0 684.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "CYLINDER_CENTER_PLANE"] {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :planar} {:m1 [["{356c5337-4bd2-43f7-945b-3e3f6530d088}" "CENTER_AXIS"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "CYLINDER_AXIS"] {:e [2916.39 464.857 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :linear} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "CENTER_PLANE"] {:e [-5302.02 3731.18 600.0], :pi 0.0, :q [0.0 0.0
-1.0]}], :m2 [["{c80e4c01-9af9-479e-a3a6-e6b80319e485}" "CENTER_PLANE"] {:e [0.0 0.0 684.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :type :planar} {:m1 [["{99ce8e6a-8722-4ed7-aa1a-ed46facf3264}" "UPPER_CYLINDER_AXIS"] {:e [-5190.52 2830.18 675.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2
[["{c80e4c01-9af9-479e-a3a6-e6b80319e485}" "CENTER_AXIS"] {:e [0.0 0.0 0.0], :pi 0.0, :q [0.0 0.0 1.0]}], :type :linear} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_CYLINDER_CENTER_PLANE"] {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 nil, :type :planar} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_CYLINDER_AXIS"] {:e [1510.0 1175.78 0.0], :pi 0.0, :q [0.0 0.0 -1.0]}], :m2
nil, :type :linear} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_ATTACHMENT_PLANE"] {:e [-1480.92 0.0 0.0], :pi 0.0, :q [-1.0 0.0 0.0]}], :m2 nil, :type :planar} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_CENTER_PLANE"] {:e [0.0 0.0 -250.0], :pi 0.0, :q [0.0 0.0 1.0]}], :m2 nil, :type :planar} {:m1 [["{a93ca8b7-6de8-42e3-bc35-7224ec4ed51f}" "BUCKET_AXIS"] {:e [-1480.92 100.0 0.0],
:pi 0.0, :q [0.0 0.0 -1.0]}], :m2 nil, :type :linear}])
