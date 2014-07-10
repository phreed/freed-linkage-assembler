
from sympy.galgebra.GA import *
# from sympy.galgebra.precedence import *
from sympy import *
import math

basis = 'no e1 e2 e3 ni'
metric = '0 0 0 0 -1, 0 1 0 0 0, 0 0 1 0 0, 0 0 0 1 0, -1 0 0 0 0'

bt = MV.setup(basis,metric=metric)
(no,e1,e2,e3,ni) = bt
print " e1: ", e1
a =  1.0*no + 4*e3 + 8*ni

print "dot product: ", a|a
print "quat: ", functions.exp(a)
euclid = [1,0,0]

cga_pt = no + euclid[0]*e1 + euclid[1]*e2 + euclid[2]*e3 +  0.5 *math.sqrt(euclid[0]**2 + euclid[1]**2 + euclid[2]**2)*ni
print "norm: ", cga_pt|cga_pt

def make_conformal_point(euclid, bt):
  (no,e1,e2,e3,ni) = bt
  return no + euclid[0]*e1 + euclid[1]*e2 + euclid[2]*e3 +  0.5 *math.sqrt(euclid[0]**2 + euclid[1]**2 + euclid[2]**2)*ni

b = make_conformal_point([1,0,0], bt)
print "norm: ", b|b

def make_conformal_plane(place, normal, bt):
  (no,e1,e2,e3,ni) = bt
  norm = math.sqrt(normal[0]**2 + normal[1]**2 + normal[2]**2)
  unit_normal = normal / norm
  delta = sum(unit_normal * place)
  return normal[0]*e1 + normal[1]*e2 + normal[2]*e3 + delta*ni

c = make_conformal_point([1,0,0], [3,4,0], bt)
print "dual plane: ", c
