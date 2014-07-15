import sys, os

def check_if_invariant(ikb, marker, type):
    """ using marker and knoledge base, look up
        desired invariant list and see if specified 
        type is invariant or not. 
        
        ikb : Knowledge base of versor/invariant maps
        marker: marker name / key in invariant_map (featurename_componentID)
        type : invariant type (indices of array in map)
            0 = Positional invariant
            1 = Z-axis invariant
            2 = X-axis invariant
    """
        
    # if ikb['invariant_map'][marker][type]:
        # return True
    # else:
        # return False    
    
    key = marker['FeatureName'] + '_' + marker['ComponentID']
    return ikb['invariant_map'][key][type]

    
def add_invariant(ikb, marker, type):
    """ Set desired invariant type to True for specified marker """
    
    key = marker['FeatureName'] + '_' + marker['ComponentID']
    ikb['invariant_map'][key][type] = True

    





