
 * **O** : cannot occur?
 * `<?>` : no progress made, only consistency checked
 * *<?>* : not specified as possible progress
 * **X** : no simple application
 * `?`   : numerical solution possible, resulting DOF may differ.


## coincident

| t\r |   0   |   h   |   1   |   2   |   3   |
|:---:|:-----:|:-----:|:-----:|:-----:|:-----:|
|  0  | `0\0` |   =   |  0\0  |  0\0  |  0\1  |
|  h  |   =   |  0\0  |   =   |   =   |   =   |
|  1  |  0\0  |   =   |  0\0  | **O** |  0\1  |
|  2  |  0\0  |   =   | **O** | **O** |  0\2  |
|  3  |  0\0  |   =   |  0\1  | **O** |  0\3  |


## parallel-z

| t\r |   0   |   h   |   1   |   2   |   3   |
|:---:|:-----:|:-----:|:-----:|:-----:|:-----:|
|  0  | `0\0` |   =   |  0\0  |  0\0  |  0\1  |
|  h  |   =   |  0\0  |   =   |   =   |   =   |
|  1  | `1\0` |   =   |  1\0  | **O** |  1\1  |
|  2  | `2\0` |   =   |  2\0  | **O** |  2\1  |
|  3  | `3\0` |   =   |  3\0  | **O** |  3\1  |


## in-plane {fixed & mobile}

| t\r |   0   |   h   |   1   |   2   |   3   |
|:---:|:-----:|:-----:|:-----:|:-----:|:-----:|
|  0  | `0\0` |   =   |  0\0  |  0\1  |  0\2  |
|  h  |   =   |  0\0  |   =   |   =   |   =   |
|  1  |  0\0  |   =   |  h\h  | **O** | **X** |
|  2  |  1\0  |   =   | **X** | **O** | **X** |
|  3  |  2\0  |   =   |  2\1  | **O** |  2\3  |


## in-line {fixed & mobile}

| t\r |   0   |   h   |   1   |   2   |   3   |
|:---:|:-----:|:-----:|:-----:|:-----:|:-----:|
|  0  | `0\0` |   =   |  0\0  |  0\0  |  0\1  |
|  h  |   =   |  0\0  |   =   |   =   |   =   |
|  1  |  0\0  |   =   |  0\0  | **O** | **X** |
|  2  |  0\0  |   =   |  h\h  | **O** | **X** |
|  3  |  1\0  |   =   |  1\1  | **O** |  1\3  |


## angle-axis (offset-z)
Presumes a coincident constraint is met.
Thus there are no translational DoF available
except as part of the helical DoF.


| t\r |   0   |   h   |   1   |   2   |   3   |
|:---:|:-----:|:-----:|:-----:|:-----:|:-----:|
|  0  | `0\0` |   =   |  0\0  |  `?`  |  0\2  |
|  h  |   =   |  0\0  |   =   |   =   |   =   |


## angle-plane (offset-x)
Presumes parallel components, meaning that
only one rotational DoF remains.

| t\r |   0   |   h   |   1   |
|:---:|:-----:|:-----:|:-----:|
|  0  | `0\0` |   =   |  0\0  |
|  h  |   =   |  0\0  |   =   |
|  1  | `1\0` |   =   |  1\0  |
|  2  | `2\0` |   =   |  2\0  |
|  3  | `3\0` |   =   |  3\0  |


## helical-x
Presumes in-line and parallel constraints are in place.
This means there is only one
rotational+translational DoF remaining.

| t\r |   0   |   h   |   1   |
|:---:|:-----:|:-----:|:-----:|
|  0  | `0/0` |   =   |  0/0  |
|  h  |   =   |  0/0  |   =   |
|  1  |  0/0  |   =   |  h/h  |



lf is introduced in a number of places.

lf is used in coincident->t2-r3 to make a decision.
