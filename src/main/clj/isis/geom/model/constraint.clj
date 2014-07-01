
(let [sample (
              {:type :coincident, :markers {[ground g2] {:e1 1.0, :e3 0.0, :e2 0.0}, [brick b2] {:e1 -99.0, :e3 10.0, :e2 50.0}}}
              {:type :coincident, :markers {[ground g3] {:e1 0.0, :e3 0.0, :e2 1.0}, [brick b3] {:e1 -100.0, :e3 10.0, :e2 51.0}}}
              {:type :in-line, :markers {[ground g1] {}, [brick b1] {:e1 -100.0, :e3 10.0, :e2 50.0}}}
              {:type :parallel-z, :markers {[ground g1] {}, [brick b1] {:e1 -100.0, :e3 10.0, :e2 50.0}}}
              {:type :offset-x, :markers {[ground g1] {}, [brick b1] {:e1 -100.0, :e3 10.0, :e2 50.0}}} ) ]
  )

