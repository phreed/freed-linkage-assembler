
// This indicates the revolute joint.
// 
// The revolute is generically modelled as
// a coincident point and parallel constraint.
// Here we have a case where a 
// plane of red is parallel to 
// a plane of green.
// This is equivalent to parallel-axis.

// first the parallel surfaces constraint
color("red", 0.5){
translate([50,0,0])
  rotate([5,20,0])
    square([300,300], true);
}

color("green", 0.5){
translate([0,10,0])
  rotate([5,20,0])
    square([300,300], true);
}

// now the coincident point constraint.
translate([0,0, 100]) {
color("red", 0.5)
translate([0,0,0])
  rotate([0,0,0])
    sphere(10);

color("green", 0.5)
translate([0,0,0])
  rotate([0,0,0])
    sphere(10);
}



