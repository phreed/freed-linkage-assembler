import os, sys
import xml.etree.ElementTree as ET
import shutil

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

    # Get assembly file name
    assembly = eltree.getroot().find('.//Assembly')
    
    # Populate component data structure
    components = eltree.getroot().find('.//Assembly//CADComponent')
    
    build_constraints(components, assembly)


def build_constraints(components, assembly):            
    constraints = []
    names = get_component_names(components, assembly)    
    for component in components:
        for pair in component.findall('.//Constraint//Pair'):
            constraints.append(build_constraint(pair, names))
    
    print constraints
    
def build_constraint(pair, names):
    constraint = {}
    for feature in pair:
        component_ref = names.get(feature.get('ComponentID'))
        if component_ref is None:
            raise()
        feature_ref = feature.get('FeatureName')
        
        marker_name = '__'.join((component_ref, feature_ref))
        print marker_name
            
            
            
def get_component_names(components, assembly):
    names = {}
    for asm in assembly:
        names[asm.get('ComponentID')] = asm.get('Name')
    for component in components:
        names[component.get('ComponentID')] = component.get('Name')
    return names
    
    
        # component_ref = feature.get('ComponentID')
        # if component_ref not in versor_map:
            # versor_map[component_ref] = None        #versor hasn't been calculated yet
        # feature_ref = feature.get('FeatureName')
        # invariant_map['__'.join((component_ref, feature_ref))] = [False, False, False]      # default
        # data[component_ref] = versor_map[component_ref]
            
    
    
if __name__ == "__main__":
    parse_assembly_file('CADAssembly.xml')