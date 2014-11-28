
// This function produces OSE style 
// square beams with holes on each side.

// constants

width = 100.0;

// objects
square_beam(15.0);

// modules

module square_beam(length) {
width = 100.0;

difference() {
linear_extrude(height = (length*width),
    center = true, convexity = 10)
  import(file = "square_beam_xsec.dxf", 
         layer = "0");

 for(a = [-(length/2):1:(length/2)]) {
   translate([0,0,a*width]){
		 rotate(90, [1,0,0])
		 cylinder(h = width*2, r= 20.0, 
         center = true, $fn = 20); 

		 rotate(90, [0,1,0])
		 cylinder(h = width*2, r= 20.0, 
         center = true, $fn = 20); }}} }

