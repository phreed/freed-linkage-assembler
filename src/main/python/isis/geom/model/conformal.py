import sys, os
from sympy import Symbol, cos, sin
from sympy.galgebra.ga import *
import math

# Refer to: http://www.wolftype.com/versor/colapinto_masters_final_02.pdf
""" These functions take in euclidean data and transform
    them into the conformal space. """

def init_conformal_basis():
    basis = 'no e_1 e_2 e_3 ni'
    metric = '0 0 0 0 -1, 0 1 0 0 0, 0 0 1 0 0, 0 0 0 1 0, -1 0 0 0 0'
    (no, e1, e2, e3, ni) = MV.setup(basis, metric=metric)
    return (no, e1, e2, e3, ni)


def make_conformal_point(epnt, basis):
    (no, e1, e2, e3, ni) = basis
    #return no + epnt[0]*e1 + epnt[1]*e2 + epnt[2]*e3 \
    #        + 0.5*math.sqrt(epnt[0]**2 + epnt[1]**2 + epnt[2]**2)*ni
    return no + epnt[0]*e1 + epnt[1]*e2 + epnt[2]*e3 \
            + 0.5*(epnt[0]**2 + epnt[1]**2 + epnt[2]**2)*ni


def make_conformal_plane_versor(epnt, normal, basis):
    """
        epnt: Point on plane
        normal: Point along normal to plane
        DP = normal + delta*ni
    """
    # http://en.wikipedia.org/wiki/Quaternion_rotation_biradial#Dual_and_undual_to_convert_to_and_from_quaternions.
    (no, e1, e2, e3, ni) = basis
    vec = [normal[0]-epnt[0], normal[1]-epnt[1], normal[2]-epnt[2]]  # normal vector
    mag = math.sqrt(vec[0]**2 + vec[1]**2 + vec[2]**2)
    nvec = [ vec[0]/mag, vec[1]/mag, vec[2]/mag ]  # normalize
    print 'nvec: ', nvec
    delta = sum(p*q for p,q in zip(nvec,epnt))   # dot product
    print 'n0: ', nvec[0]*e1
    print 'n1: ', nvec[1]*e2
    dual_p = nvec[0]*e1 + nvec[1]*e2 + nvec[2]*e3 + delta*ni  # dual plane
    #return dual_p*MV.I  # undual bivector -- want: MI
    print 'wedfe: ', no^ni
    print 'DUAL_PLANE: ', dual_p
    print 'PLANE: ', dual_p*MV.I
    
    return -dual(dual_p)
 
def make_conformal_axis(epnt1, epnt2, basis):
    """ A = Pa ^ Pb ^ ni """
    (no, e1, e2, e3, ni) = basis
    
    pa = make_conformal_point(epnt1, basis)
    pb = make_conformal_point(epnt2, basis)
    return pa ^ pb ^ ni

 
def make_conformal_csys_versor(x, i, t, basis):
    """ (1-0.5*d*ni)*(exp(-0.5*theta*B) """
    (no, e1, e2, e3, ni) = basis
    R = cos(t/2) + sin(t/2)*(i[0] + i[1] + i[2])
    d = [(p-q)/2.0 for p,q in zip(i,x)]
    print d
    #T = 1 - d*ni
    T = 1 - (d[0]*(e1^ni) + d[1]*(e2^ni) + d[2]*(e3^ni))
    return T*R

    
#########################  
##  Testing functions  ##
#########################

def test_point(epnt, basis):
    cpnt = make_conformal_point(epnt, basis)
    print '######  Point Test:  ######'
    print 'CPNT: ', cpnt
    assert (cpnt|cpnt == 0)
    print 'Dot product: ', cpnt|cpnt
    print 'PASSED'

def test_plane(plane, epnt, basis):
    cpnt = make_conformal_point(epnt, basis)
    print '######  Plane Test:  ######'
    print 'Euclidean PNT: ', epnt
    print 'Conformal PNT: ', cpnt
    x1 = plane*cpnt*MV.rev(plane)
    x2 = plane*x1*MV.rev(plane)
    print 'a\' After first reflection:  ', x1
    print 'a\' After second reflection: ', x2
    print 'MV diff: ', x2 - cpnt
    assert (x2-cpnt == 0)
    print 'PASSED'

def test_axis(epnt, axis, basis):
    cpnt = make_conformal_point(epnt, basis)
    print '######  Axis Test:  ######'
    print 'Euclidean PNT: ', epnt
    print 'Conformal PNT: ', cpnt
    x1 = axis*cpnt*MV.inv(axis)
    x2 = axis*x1*MV.inv(axis)
    print 'a\' After first reflection:  ', x1
    print 'a\' After second reflection: ', x2
    print 'MV diff: ', x2 - cpnt
    assert (x2-cpnt == 0)
    print 'PASSED'

def test_csys(epnt, csys, basis):
    cpnt = make_conformal_point(epnt, basis)
    print '######  Csys Test:  ######'
    print 'CPNT: ', cpnt
    print 'CSYS:   ', csys
    print 'CSYS\': ', csys*cpnt
    #x1 = csys*cpnt*MV.inv(csys)
    #x2 = csys*x1*MV.inv(csys)
    x1 = csys*cpnt*MV.inv(csys)
    x2 = csys*x1*MV.inv(csys)
    print 'a\' After first reflection:  ', x1
    print 'a\' After second reflection: ', x2
    print 'MV diff: ', x2 - cpnt
    assert (x2-cpnt == 0)
    print 'PASSED'
    
if __name__ == "__main__":
    """ For testing use. Typically functions will be called separately. """
    b = init_conformal_basis()
    plane = make_conformal_plane_versor([1,1,1],[1,1,2],b)
    axis = make_conformal_axis([0,1,1],[1,1,1],b)
    csys = make_conformal_csys_versor([1,1,1],[1,1,2],0,b)
    #csys = make_conformal_csys_versor([0,0,0],[0,0,0],0,b)
    print 'csys: ', csys
    print ''
    print 'Testing versors... '
    print ''
    test_plane(plane, [1,1,5], b)
    print ''
    test_point([1,1,1], b)
    print ''
    test_point([3,8,12], b)
    print ''
    test_axis([1,1,5], axis, b)
    print ''
    test_csys([1,1,5], csys, b)
    print ''
    print 'ALL TESTS PASSED'

    