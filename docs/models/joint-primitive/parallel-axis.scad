
// This indicates the parallel-axis
// joint-primitive constraint. 
// 
// Here we have a case where a 
// line on red is parallel to 
// a line on green.

color("red", 0.5){
translate([50,0,0])
  rotate([5,20,0])
    cylinder(300, 1,1, true);
}

color("green", 0.5){
translate([0,10,0])
  rotate([5,20,0])
    cylinder(300, 1,1, true);
}




