
// This indicates the in-plane
// joint-primitive constraint. 
// 
// Here we have a case where a point on 
// the red component must be found
// on a plane in the green component.

color("red", 0.5){
translate([0,0,0])
  rotate([0,0,0])
    sphere(10);
}

color("green", 0.5){
translate([0,0,0])
  rotate([0,0,0])
    square([300, 300], true);
}




