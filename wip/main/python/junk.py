
from sympy.galgebra.GA import *
# from sympy.galgebra.precedence import *
from sympy import *
import math

basis = 'no e1 e2 e3 ni'
metric = '0 0 0 0 -1, 0 1 0 0 0, 0 0 1 0 0, 0 0 0 1 0, -1 0 0 0 0'

bt = MV.setup(basis,metric=metric)
(no,e1,e2,e3,ni) = bt
print " e1: ", e1
a =  0.0* no + 4*e3 + 8*ni
print "dot product: ", a|a
print "quat: ", functions.exp(a)

def make_conformal_point(euclid, bt):
  (no,e1,e2,e3,ni) = bt
  return no + euclid[0]*e1 + euclid[1]*e2 + euclid[2]*e3 +  0.5 *math.sqrt(euclid[0]**2 + euclid[1]**2 + euclid[2]**2)*ni

def make_conformal_versor(euclid, axis, angle, bt):
  (no,e1,e2,e3,ni) = bt
  return no + euclid[0]*e1 + euclid[1]*e2 + euclid[2]*e3 +  0.5 *math.sqrt(euclid[0]**2 + euclid[1]**2 + euclid[2]**2)*ni

b = make([1,0,0], bt)

print "norm: ", b|b
