import sys, os
from sympy import *
from sympy.galgebra.ga import *
from invariant import *
from geobj import *
import auxiliary as aux

def coincident_precondition(ikb, constraint):
    """ Associated with each constraint type is a function which
        checks the preconditions and returns the marker which
        is constrained followed by the marker that is underconstrained. """
    
    if check_if_invariant(ikb, constraint['m1'], 0): 
        return (constraint['m1'], constraint['m2'])
    elif check_if_invariant(ikb, constraint['m2'], 0):
        return (constraint['m2'], constraint['m1'])
    else:  
        return False 
        
def coincident_postcondition(ikb, marker):
    """ Associated with each constraint type is a function which
        checks/sets the postconditions for after the constraint has
        been satisfied. m2 was early set to the underconstrained marker. """
        
    add_invariant(ikb, marker, 0)
    
    return 
    
    
def transform_T3R3_coincident(ikb, m1, m2):
    """   PFT entry: (3,3,coincident)

          Initial status:
          3-TDOF(?m2-link)
          3-RDOF(?m2-link)

          Plan fragment:
          translate(?m2-link, vec-diff(gmp(?m1), gmp(?m2));

          New status:
          0-TDOF(?m2-link, gmp(?m2))
          3-RDOF(?m2-link)  <no change>

          Explanation:
          Link ?m2-link is free to translate, so the translation
          vector is measured and the ?m2-link is moved.
          No checks are required.
    """    
    basis = 'no e_1 e_2 e_3 ni'
    metric = '0 0 0 0 -1, 0 1 0 0 0, 0 0 1 0 0, 0 0 0 1 0, -1 0 0 0 0'
    (no, e1, e2, e3, ni) = MV.setup(basis, metric=metric)
    marker_1_gmp = gmp(ikb, m1)
    marker_2_gmp = gmp(ikb, m2)
    vec_diff = marker_1_gmp - marker_2_gmp
    new_m2_gmp = marker_2_gmp + vec_diff
    assert (new_m2_gmp == marker_1_gmp), 'Translation failed!'
    print 'Successful T3R3 translation'
    # translation worked, update maps
    m2_vmap = ikb['versor_map'][m2['ComponentID']]
    m2_vmap['Versor'] =  (1 - 0.5*vec_diff*ni) * m2_vmap['Versor'] # translate component versor
    m2_vmap['TDOF']['Value'] = 0
    m2_vmap['TDOF']['LocationData'] = marker_1_gmp # this is the same as gmp(ikb, m2). 
                                                   # The versor was updated so that 
                                                   # function would return m1_gmp.    
   
   
def transform_T0R3_coincident(ikb, m1, m2):
    """ PFT entry: (0,3,coincident)

        Initial status:
          0-TDOF(?m2-link, ?m2-point)
          3-RDOF(?m2-link)

        Plan fragment:
          3r/p-p(?m2-link, ?m2-point, gmp(?m2), gmp(?m1));
          R[0] = vec-diff(gmp(?m2), ?m2-point);

        New status:
          0-TDOF(?m2-link, ?m2-point) <unchanged>
          1-RDOF(?m2-link, R[0], nil, nil)

        Explanation:
          Geom ?link cannot translate, so the coincident
          constraint is satisfied by a rotation.
          After the constraint is satisfied, ?link can still rotate
          about the line connecting ?m-2 and ?point.
    """
    m2_link = m2['ComponentID']
    m2_point = ikb['versor_map'][m2['ComponentID']]['TDOF']['LocationData']
    
    # Set pos & z invariants
    add_invariant(ikb, m2, 1)
    add_invariant(ikb, m2, 2)
    
    # Rotate
    aux.dof_3r_to_point(m2_link, m2_point, gmp(ikb,m2), gmp(ikb,m1))
    
    m2_vmap = ikb['versor_map'][m2['ComponentID']]
    m2_vmap['RDOF']['Value'] = 1
    m2_vmap['RDOF']['LocationData'] = MV.norm(gmp(ikb,m2), m2_point)
 
 
def transform_T0R1_coincident(ikb, m1, m2):
        return
        
 
def coincident_transform_dispatch(ikb, m1, m2):
    """ Examine the underconstrained marker to determine the dispatch key.
        The key is the [#tdof #rdof] of the m2 link. """
    
    tdof = ikb['versor_map'][m2['ComponentID']]['TDOF']['Value']
    rdof = ikb['versor_map'][m2['ComponentID']]['RDOF']['Value']
    return 'T' + str(tdof) + 'R' + str(rdof)
    
    
def transform_coincident(key, ikb, m1, m2):
    dispatch_map = { 'T3R3' : transform_T3R3_coincident(ikb, m1, m2) ,
                     'T0R3' : transform_T0R3_coincident(ikb, m1, m2) ,
                     'T0R1' : transform_T0R1_coincident(ikb, m1, m2) }  

    if key not in dispatch_map: 
        msg = "Key not found in coincident dispatch map. Error."
        raise(msg)
    
    dispatch_map[key]
    
    
    
    