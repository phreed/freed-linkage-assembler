

translate(v = [0.0000000000, 0.0000000000, 0.0000000000]) {
	rotate(a = 0.0000000000, v = [0.0000000000, 0.0000000000, 0.0000000000]) {
		import_stl(filename = "STL\\BOOM_EX_375.stl");
	}
}

translate(v = [2204.5909459444, -9748.0744297844, 902.5000000000]) {
	rotate(a = 180.0000000000, v = [-0.4539904997, 0.8910065242, 0.0000000000]) {
		import_stl(filename = "STL\\BOOM.stl");
	}
}