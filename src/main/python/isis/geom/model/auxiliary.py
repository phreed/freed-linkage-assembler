import tolerance as tol
from sympy import *
from sympy.galgebra.ga import *

def dof_1r_to_point(link, center, from_pt, to_pt, axis, axis_1, axis_2):
    """ Procedure to rotate body ?link about ?axis.
        If restrictions are imposed by ?axis-1 and ?axis-2, they are honored.
        The procedure keeps the position of point ?center invariant,
        and moves ?from-point on ?link to globally-fixed ?to-point.
    """

    return

def dof_3r_to_point(link, center, from_pt, to_pt):
    """ Procedure to rotate body ?link about ?center,
        moving ?from-point on ?link to globally-fixed ?to-point.
        Done by constructing an arbitrary rotational axis and calling dof/r1:p->p.
    """
    from_diff = from_pt - center
    to_diff = to_pt - center
    if tol.are_near_same(from_diff, to_diff):
        xlate = [0.0, 0.0, 0.0]

        if not (tol.is_near_equal(MV.norm(from_diff), MV.norm(to_diff))):
            msg = 'ERROR: Dimensional over-constraint. From-Point: {0}. '
                  'To-Point: {1}. About-Axis: {2}. '
                  'About-Center: {3}.'.format(from_pt, to_point, center, None)
            raise(msg)
        else:
            dof_1r_to_point(link, center, from_pt, to_pt, from_diff^to_diff, None, None)
    return

