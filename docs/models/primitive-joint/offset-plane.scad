
// This indicates the offset-plane
// joint-primitive constraint. 
// 
// First the parallel constraint is met.
// The constraint is the difference 
// (offset) between two planes passing 
// through the parallel lines.

// parameters

// renders

color("red", 0.25){
translate([50,0,0]){
  rotate([0,0,0])
    cylinder(300, 1,1, true);
  rotate([0,90,0])
    square([300,300], true);
}}

color("green", 0.25){
translate([0,10,0]){
  rotate([0,0,0])
    cylinder(300, 1,1, true);
  rotate([0,90,25])
    square([300,300], true);
}}

// modules




