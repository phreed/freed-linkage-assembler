
// This indicates the pin (revolute) joint.
// 
// The revolute is generically modeled as
// a coincident point and parallel constraint.


// in-plane constraint

translate([-40,0,30]){
  color("red", 0.5)
    sphere(10);
  rotate(25, [0,1,2])
    color("blue", 0.4)
      square([300, 300], true);
}

// parallel-axis constraint

color("red", 0.5){
translate([50,0,50])
  rotate([5,40,0])
    square([300,300], true);
}

color("blue", 0.5){
translate([0,10,50])
  rotate([5,40,0])
    square([300,300], true);
}
// application of these two constraints 
// results in t2-r1 DoF for the two components
// (one component fixed).

// You can see that the red component
// is free to move the point in translation 
// along the blue plane.
// red can also rotate about an axis
// perpendicular to the red plane but not
// about the non-parallel plane.

// in-line constraint

translate([-30, -40, -10]) {
color("blue", 0.5){
translate([0,0,0])
  rotate([0,0,0])
    sphere(10);
}

color("red", 0.5){
translate([0,0,0])
  rotate([0,0,0])
    cylinder(300, 2, 2, true);
} }

// Consider blue to be stationary and red mobile.
// For a moment, ignore the in-line constraint.
// red can slide (translate) on the blue plane and
// rotate about the axis perpendicular to the 
// parallel planes and passing through the red point.
// Adding the in-line constraint results in the
// red line tracing out a hour-glass surface.
// The blue point must remain on this line as red
// revolves on its axis. 

 
