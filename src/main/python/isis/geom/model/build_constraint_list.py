import os, sys
import xml.etree.ElementTree as ET
from sympy import Symbol, cos, sin
from sympy.galgebra.ga import *
import math
import conformal as c
import json

""" Parses assembly file and populates list of model constraints.
    Data is from CADAssembly.xml and assumes all constraints are coincident. """

def parse_assembly_file(asm_xml):
    """ Parse assembly file to build data structure """
    try:
        eltree = ET.parse(asm_xml)
    except ET.ParseError:
        try: # fallback on us-ascii parser
            eltree = ET.parse(asm_xml, parser=ET.XMLParser(encoding="us-ascii"))
        except ET.ParseError: # can't read the file
            print "Unable to parse CAD assembly xml."
    
    # Populate component data structure
    components = eltree.getroot().find('.//Assembly//CADComponent')
    
    return components


def build_constraints(components):            
    constraints = []
    versor_map = {}
    invariant_map = {}
    basis = c.init_conformal_basis()  # initialize basis
    base_asm_comp = components.get('ComponentID')
    for component in components:
        for pair in component.findall('.//Constraint//Pair'):
            constraints.append(build_constraint(pair, versor_map, invariant_map, 
                                                basis, base_asm_comp))
    constraint_map = { 'versor_map' : versor_map ,
                       'invariant_map' : invariant_map }
    return (constraints, constraint_map)
      
def build_constraint(pair, versor_map, invariant_map, basis, base_asm_comp):
    constraint = {}
    m = 1
    (no, e1, e2, e3, ni) = basis
    type = pair.get('FeatureGeometryType')  # SURFACE/AXIS/CSYS
    for feature in pair:
        data = {}
        component_ref = feature.get('ComponentID')
        feature_ref = feature.get('FeatureName')
        
        # add data to changing maps
        if component_ref not in versor_map:
            versor_map[component_ref] = { 'Versor': 1.0 + 0.0*(no+e1+e2+e3+ni), # default versor
                                          'TDOF':  { 'LocationData': None, 'Value': 3 },
                                          'RDOF':  { 'LocationData': None, 'Value': 3 } }
        port_name = feature_ref + '_' + component_ref
        if port_name not in invariant_map:
            if feature.get('ComponentID') == base_asm_comp:
                invariant_map[port_name] = [True, True, True]   # top level assembly file
            else:
                invariant_map[port_name] = [False, False, False]    # [pos, z-axis, x-axis]       
        # convert marker data to sympy objects to fixed constraint structure
        data['ComponentID'] = feature.get('ComponentID')
        data[feature.get('ComponentID')] = versor_map[component_ref]
        data['FeatureName'] = feature.get('FeatureName')
        data[feature.get('FeatureName')] = invariant_map[port_name]
        markers = feature.findall('.//GeometryMarker')
        if markers is not None: # for now (later versions of XML will have field for all features)
            for marker in markers:
                x = [float(marker.get('x')), float(marker.get('y')), float(marker.get('z'))]
                i = [float(marker.get('i')), float(marker.get('j')), float(marker.get('k'))]
                t = float(marker.get('pi'))
                if type == 'SURFACE':
                    data['xyz'] = c.make_conformal_pont(x,basis)
                    data['ijk']
                    data['Data'] = c.make_conformal_plane_versor(x, i, basis)
                if type == 'POINT':
                    data['Data'] = c.make_conformal_point(x, basis)
                if type == 'AXIS':
                    data['Data'] = c.make_conformal_axis(x, i, t, basis)
                if type == 'CSYS':
                    data['Data'] = c.make_conformal_csys_versor(x, i, t, basis)
        constraint['m'+str(m)] = data
        m += 1
    constraint['type'] = 'coincident'   # fixed to coincident for now
    return constraint


if __name__ == "__main__":
    components = parse_assembly_file('test.xml')
    
    constraints, ikb = build_constraints(components)
    