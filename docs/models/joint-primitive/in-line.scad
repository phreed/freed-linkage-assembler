
// This indicates the in-line
// joint-primitive constraint. 
// 
// Here we have a case point on 
// the red component must be found
// on a line in the green component.

color("red", 0.5){
translate([0,0,0])
  rotate([0,0,0])
    sphere(10);
}

color("green", 0.5){
translate([0,0,0])
  rotate([0,0,0])
    cylinder(300, 2, 2, true);
}




