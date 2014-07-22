

// fit the two pyramids together 
// back to back with the peak 
// in the z coord sticking out.
// The green object is the ground,
// the red objec is the brick.
// Then stack the blue object onto the red.

color( "LimeGreen", a=0.5 )
polyhedron(
points=[[2,0,0],[5,0,0],[2,4,0],[2,0,-1]], 
faces=[[0,1,2],[0,3,1],[0,2,3],[1,3,2]]);


translate(v=[2,0,0])
rotate( a=120, v=[-1, -1, -1])
color( "Red", a=0.5 ) 
polyhedron(
points=[[0,0,0],[0,3,0],[0,0,4],[1,0,0]], 
faces=[[0,1,2],[0,3,1],[0,2,3],[1,3,2]]);

// AFTER FIRST POINT : 3-3-coincident
//translate(v=[5.00, -3.00, 0.00])

// AFTER SECOND POINT : 0-3-coincident
// translate(v=[2.37,-0.923, 1.108])
// rotate( a=72, v=[-0.186, 0.062, -0.558] )

// FINAL : 0-1-coincident
translate(v=[2.56, 0.426, 1.704])
rotate( a=120, v=[-0.73,-0.42, -0.19] )

color( "Blue", a=0.5 ) 
polyhedron(
points=[[0,0,0],[0,3,0],[0,0,4],[-1,0,0]], 
faces=[[0,1,2],[0,3,1],[0,2,3],[1,3,2]]);