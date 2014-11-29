
// This indicates the pin (revolute) joint.
// 
// The revolute is generically modeled as
// a coincident point and parallel constraint.


// in-plane constraint

color("red", 0.5){
translate([0,0,30])
  rotate(25,[0,1,2])
    sphere(10);
}

color("green", 0.5){
translate([0,0,30])
  rotate(25, [0,1,2])
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

// You can see that the red component
// is free to move the point in translation 
// along the green plane.
// Red can also rotate about an axis
// perpendicular to the red plane but not
// about the non-parallel plane.
// Consider the path of the earth in its
// orbital plane where is can move in the 
// orbtal plane while rotating on its axis. 
