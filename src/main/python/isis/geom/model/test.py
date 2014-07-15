from build_constraint_list import *
components = parse_assembly_file('test.xml')
constraints, ikb = build_constraints(components)
port = constraints[0]['m1']['FeatureName'] + '_' + constraints[0]['m1']['ComponentID']
ikb['invariant_map'][port][0] = True    # fix first component
from coincident_slide import *
constraint = coincident_precondition(ikb, constraints[0])
(m1, m2) = constraint
print ''
print ''
print ''
print constraint
print ''
key = coincident_transform_dispatch(ikb, m1, m2)
print 'Key: ', key
print ''
print ''
transform_coincident(key, ikb, m1, m2)
print 'T3R3 worked'
