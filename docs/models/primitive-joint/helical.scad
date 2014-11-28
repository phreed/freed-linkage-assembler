
// This indicates the helical
// joint-primitive constraint. 
// 
// First the in-line and parallel constraints 
// are met, thus constructing an axis.
// Then the twist about the axis defines the 
// possible positions.
// The line in the green lies on the helical
// surface of the red.
//

// parameters

// renders

color("red", 0.25){
 rotate([0,30,10]) {
   translate([0,0,-150])
	linear_extrude(height=300, center=false, 
			convexity=10, twist=400) {
	  translate([2,0,0])
	    square([100,1],true); } 

   cylinder(400, 2,2, true);
}}

color("green", 0.25){
  rotate([0,30,10]) {
    translate([0,0,30])
    rotate(90, [7,4,0])
      cylinder(300, 2,2, true);
    cylinder(400, 2,2, true);
}}


// modules




