from sympy import *
from sympy.galgebra.ga import *

def is_near_equal(v1, v2):
    """ A function which takes two sympy multivector arguments
        and compares them making sure that they are within
        the specified tolerance.
    """
    if (v1|v2 <= 0.001):    # if equal, dot product = 0
        return True
    else:
        return False
        
        
def is_near_zero(v1):
    """ A function which takes a scalar argument
        and verifies that its magnitude is sufficiently small.
    """
    if abs(v1) <= 0.001:
        return True
    else:
        return False
        

def are_near_same(set):
    """ A function which compares a set of objects.
        They must all be of the same type and there
        values must be individually in range.
    """
    result = True
    i = 1
    for v in set:
        for s in set:
            result = is_near_equal(v,s)
            if not result: return result  # not near same - result false
    return result