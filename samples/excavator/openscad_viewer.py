import time
import xml.etree.ElementTree as ET
from solid import *
from solid.utils import *
import os, sys
import shutil
import math
import re

# Assumes all CAD files are in 'STL' directory
# with name following that of the <CADComponent>
# "Name" attribute (multiple instances will be
# dealt with in this file by making a copy of STL
# to new name and adding that component's data to
# the dictionary under the updated name. 

#####################################################################
# QUATERNION FROM ASSEMBLY_CSYS TO COMPONENT_TO_BE_ADDED LOCAL_CSYS #
#####################################################################

# Dictionary structure:
#       TLA:
#           - Assembly file name
#       Components:
#           Component:
#               - Name
#               - Translation vector
#               - Rotation matrix    


def Parse_Assembly_File(asm_xml):
    """ Parse assembly file to build data structure """
    struct = {}
    try:
        eltree = ET.parse(asm_xml)
    except ET.ParseError:
        try: # fallback on us-ascii parser
            eltree = ET.parse(asm_xml, parser=ET.XMLParser(encoding="us-ascii"))
        except ET.ParseError: # can't read the file
            print "Unable to parse CAD assembly xml."
    
    # Get assembly file name
    assembly = eltree.getroot().find('.//Assembly')
    tla = {}
    for asm in assembly:
        name = asm.get("Name", None)
        if ( name != None and asm.get("Type", None) == "ASSEMBLY" ):
            tla = { 'name': name }
        struct['TLA'] = tla

    # Populate component data structure
    components = eltree.getroot().find('.//Assembly//CADComponent')
    comps = {}
    ii = 1
    for component in components:
        xform = component.findall('.//versor')
        if xform is None:
            msg = "Transform data not found for component {0}.".format(component.get('Name'))
            raise(msg)
        if len(xform) > 1: 
            msg = "More than 1 XForm tag encountered for component {0}; "\
                    "grabbing first transform.".format(component.get('Name'))
        
        """ FOR TESTING - skip if not found (not all components populated for now) """
        try: 
            x = float(xform[0].get("x", None))
        except:
            continue
        y = float(xform[0].get("y", None))
        z = float(xform[0].get("z", None))
        i = float(xform[0].get("i", None))
        j = float(xform[0].get("j", None))
        k = float(xform[0].get("k", None))
        p = float(xform[0].get("pi", None)) 
         
        vars = [x, y, z, i, j, k, p]

        if ( any(v is None for v in vars) ):
            msg = 'One or more transform variables are empty or do not exist '\
                  ' for {0}'.format(component.get('Name'))
            raise Exception(msg)

        # Convert to rotation matrix
        rot_matrix = quaternion_to_rotation(i, j, k, p, component.get('Name'))
        comp = { 'translation': [x, y, z],
                 'rotation': rot_matrix }
        
        # Check for unique name to avoid writing over data with duplicate key

        if component.get('Name') in comps:
            print 'Filename already exists as key, copying file to new name...'
            old_path = os.path.join(os.getcwd(), "STL", component.get('Name') + '.stl')
            new_name = component.get('Name') + '_' + 'Z' + str(ii)
            new_path = os.path.join(os.getcwd(), "STL", new_name + '.stl')
            if (os.path.exists(old_path)):
                shutil.copy(old_path, new_path)
                print 'Copied ', component.get('Name'), ' as ', new_name, '\n'
            else:
                print old_name + ' could not be found.\n'
            ii += 1
            comps[new_name] = comp
        else:
            comps[component.get('Name')] = comp
    struct['Components'] = comps
    return struct


def quaternion_to_rotation(i, j, k, p, name):
    """ Convert quaternion data to rotation matrix """
    # http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm
    # Rotation matrix = [ XX XY XZ YX YY YZ ZX ZY ZZ ] #
    xx = 2.0*i**2
    yy = 2.0*j**2
    zz = 2.0*k**2
    xy = 2.0*i*j
    zp = 2.0*k*p
    xz = 2.0*i*k
    yp = 2.0*j*p
    yz = 2.0*j*k
    xp = 2.0*i*p
    
    matrix = [0] * 9
    matrix[0] = 1.0-yy-zz
    matrix[1] = xy-zp
    matrix[2] = xz+yp
    matrix[3] = xy+zp
    matrix[4] = 1.0-xx-zz
    matrix[5] = yz-xp
    matrix[6] = xz-yp
    matrix[7] = yz+xp
    matrix[8] = 1.0-xx-yy
    return matrix      

def build_assembly(struct):
    stl_loc = os.getcwd() + '\\STL'
    if not (os.path.isdir(stl_loc)):
        raise Exception('STL directory does not exist.')
        
    asm = ''
    for component in struct['Components']:
        print "Adding component " + component
        temp = add_component_to_assembly(struct, component, stl_loc)
        asm = asm + temp
    
    return asm
    
    
def add_component_to_assembly(struct, component, file_loc):
    comp_path = "STL" + r'\\' + component + '.stl'
    
    if not (os.path.isfile(comp_path)):
        raise Exception('{0} does not exist.'.format(comp_path))
    
    stl = import_stl(comp_path)
    rot = struct['Components'][component]['rotation']
    trans = struct['Components'][component]['translation']
    
    comp = multmatrix(m = [ [ rot[0], rot[3], rot[6], trans[0] ],
                            [ rot[1], rot[4], rot[7], trans[1], ],
                            [ rot[2], rot[5], rot[8], trans[2] ],
                            [     0,      0,      0,        1  ] ])( stl)
                           
    return scad_render(comp)
    
def launch_openscad(file):
    print "Launching OpenSCAD..."
    os.startfile(file)
    
if __name__ == "__main__":
    start = time.time()
    struct = Parse_Assembly_File('CADAssembly_aug.xml')
    asm = build_assembly(struct)
    file_path = os.getcwd() + "\\" + struct['TLA']['name'] + ".scad"
    
    with open(file_path, 'w') as f:
        f.write(asm)
    print "\nOpenSCAD object saved to {0}".format(file_path)
    launch_openscad(file_path)
    
    print "Time: ", time.time() - start

    
    