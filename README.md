geometric-assembler
===================

A geometric assembler for 3D kinematic assemblies.

History
=======

This work is based on that of Glenn A. Kramer and implements the technique found in US patents.
5427531 and 5617510
The detail for this work is published in "Solving Geometric Constraint Systems: A Case Study in Kinematics", 1992.

Status
======

Checking the copyright on the code provided in the [Kramer-1992].


Implementation
==============

The work was originally implemented in Lisp.
This project starts with that work converting it into the Clojure dialect of Lisp.
Adaptors to specific engineering environments are provided:
  * GME : via ClojureCLR generating specific interpretors.
  * WebGME : via ClojureScript or Wisp.
  * FreeCAD : via Jython and Clojure.
  * BRL-CAD : via ?

 
References
==========

http://clojure.org/
https://github.com/Gozala/wisp
http://jeditoolkit.com/try-wisp/


Inspiration
===========

http://www.cloud-invent.com/CAD-Future/CloudCAD.aspx

