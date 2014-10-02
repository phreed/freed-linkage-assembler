freed-linkage-assembler
===================

A linkage assembler for 3D kinematic assemblies.

History
=======

This work is based on that of Glenn A. Kramer and implements the technique found in US patents.
5427531 and 5617510
The detail for this work is published in "Solving Geometric Constraint Systems: A Case Study in Kinematics", 1992.
The name for this project is taken from "The Linkage Assembler" found in that book.

Status
======

Glenn Kramer and MIT press have granted permission to replicate and use the copyright material found in the text [Kramer-1992].


Implementation
==============

The work was originally implemented in Lisp.
This project starts with that work converting it into the Clojure dialect of Lisp.
Adaptors to specific engineering environments are provided:
  * GME : via ClojureCLR generating specific interpretors.
  * WebGME : via ClojureScript or Wisp.
  * FreeCAD : via Jython and Clojure.
  * BRL-CAD : via ?
The implementation is proceeding but is not yet complete.
Presently the coincident points are being placed.
The higher joint, csys, has been implemented via a set of three point constraints.
The line and plane primitive constraints will be implemented soon.
The plan is to replace the vector based geometry with a geometric algebra implementation.

This project is not ready for production use.

 
References
==========

  * http://clojure.org/
  * https://github.com/Gozala/wisp
  * http://jeditoolkit.com/try-wisp/
  * http://nodejs.org/
  * http://www.freecadweb.org/
  * http://brlcad.org/
  * http://en.wikipedia.org/wiki/Conformal_geometric_algebra
  
  * https://github.com/webgme/webgme
  * http://www.isis.vanderbilt.edu/Projects/gme/
  * https://github.com/webgme/webgme
  * http://webgme.org/
  * https://www.youtube.com/watch?v=0YCo4cpoB7k


Inspiration
===========

http://www.cloud-invent.com/CAD-Future/CloudCAD.aspx

