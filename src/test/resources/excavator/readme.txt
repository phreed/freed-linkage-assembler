
First augment the cad-assembly file by running the assembler.
geometric_assembler>
clear; lein run --
-i .\src\test\reso urces\excavator\cad_assembly_boom_dipper.xml
-o .\src\test\resources\excavator\CADAssembly_aug.xml

Then build the open scad file and display the result.
geometric_assembler\src\test\resources\excavator>
python ..\..\..\main\python\openscad_viewer.py

