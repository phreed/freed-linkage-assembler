
// This indicates the pin (revolute) joint.
// 
// The revolute is generically modeled as
// a coincident point and parallel constraint.


// in-plane constraint

translate([-40,0,30]){
  color("red", 0.5)
    sphere(10);
  rotate(25, [0,1,2])
    color("green", 0.4)
      square([300, 300], true);
}

// parallel-axis constraint

color("red", 0.5){
translate([50,0,50])
  rotate([5,40,0])
    square([300,300], true);
}

color("green", 0.5){
translate([0,10,50])
  rotate([5,40,0])
    square([300,300], true);
}
// application of these two constraints 
// results in t2-r1 DoF for the two components
// (one component fixed).

// You can see that the red component
// is free to move the point in translation 
// along the green plane.
// Red can also rotate about an axis
// perpendicular to the red plane but not
// about the non-parallel plane.

// in-line constraint

translate([-30, -40, -10]) {
color("red", 0.5){
translate([0,0,0])
  rotate([0,0,0])
    sphere(10);
}

color("green", 0.5){
translate([0,0,0])
  rotate([0,0,0])
    cylinder(300, 2, 2, true);
} }

// Consider the path of the earth in its
// orbital plane where is can move in the 
// orbtal plane while rotating on its axis.
// This last constraint pins the red component
// so that it revolves about an axis,
// perpendicular to the parallel constraint and
// passing through the in-line red point.

 
