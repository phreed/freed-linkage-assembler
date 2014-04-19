// This is a simple three linkage assembly.
// Its primary purpose is to develop the
// geometric assembler.

module bar() {
	minkowski() {
			cube(size=[6.0,0.5,0.1], center=true);
			cylinder(r=0.25, h=0.1);
	}
}

module link() {
	translate([2.5,0,0.1]) {
		bar();
	}
	translate([-2.5,0,-0.1]) {
		bar();
	}
}

color("Red")
translate([0,-sin(60)*5.0,0]) {
	link();
};

color("Blue")
translate([-2.5,0.0,0.0]) {
	rotate(a=-120, v=[0,0,1])
		link();
};

color("Green")
translate([2.5,0.0,0.0]) {
	rotate(a=120, v=[0,0,1])
		link();
};
