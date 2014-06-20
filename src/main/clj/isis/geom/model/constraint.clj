
(let [sample (
              {:type :coincident, :markers {[ground g2] {:p1 1.0, :p3 0.0, :p2 0.0}, [brick b2] {:p1 -99.0, :p3 10.0, :p2 50.0}}}
              {:type :coincident, :markers {[ground g3] {:p1 0.0, :p3 0.0, :p2 1.0}, [brick b3] {:p1 -100.0, :p3 10.0, :p2 51.0}}}
              {:type :in-line, :markers {[ground g1] {}, [brick b1] {:p1 -100.0, :p3 10.0, :p2 50.0}}}
              {:type :parallel-z, :markers {[ground g1] {}, [brick b1] {:p1 -100.0, :p3 10.0, :p2 50.0}}}
              {:type :offset-x, :markers {[ground g1] {}, [brick b1] {:p1 -100.0, :p3 10.0, :p2 50.0}}} ) ]
  )

