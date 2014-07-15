import sys, os
from sympy import *
from sympy.galgebra.ga import *

def gmp(ikb, marker):
    """ Marker position in global coordinate frame. """
    
    # get marker versor in local coords
    # get versor of component owning marker
    # calculate marker versor in global coords
    # qAq-1 = a'
    
    q = ikb['versor_map'][marker['ComponentID']]['versor']
    a = marker['Data']
    a_p = q*a*inv(q)
    return a_p