
// This indicates the helical
// joint-primitive constraint. 
// 
// First the in-line and parallel constraints are met.
// Then the twist about the 
//

// parameters

// renders

color("red", 0.25){
  rotate([0,30,10]) {
    cylinder(300, 1,1, true);
  for(p=[-150:20:150])
    rotate(p*2,[0,0,1])
       translate([20,0,p])
         sphere(5);
}}

color("green", 0.25){
  rotate([0,30,10]) {
    rotate(90, [0,1,0])
      square([200,200], true, 0.1);
    cylinder(300, 1,1, true);
}}

// modules




