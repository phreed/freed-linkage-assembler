
// This indicates the offset-axis
// joint-primitive constraint. 
// 
// First the coincident constraint is met.
// The constraint is the difference 
// (offset) between two lines passing 
// through the coincident points.
//
// Do the lines have to pass through
// the coincident points? 

// parameters

// renders

color("red", 0.25){
 rotate([0,90,0])
    sphere(10, true);
  rotate([0,0,0])
    cylinder(300, 1,1, true);
}

color("green", 0.25){
 rotate([0,90,25])
    sphere(10, true);
  rotate([0,30,10])
    cylinder(300, 1,1, true);
}

// modules




