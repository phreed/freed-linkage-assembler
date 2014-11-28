
// This indicates the parallel-axis
// joint-primitive constraint. 
// 
// Here we have a case where a 
// plane of red is parallel to 
// a plane of green.
// This is equivalent to parallel-axis.


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




