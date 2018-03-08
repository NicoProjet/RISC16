### Command-line generation of the jar, example for the sequential simulator, from the ```bin/``` directory:

```
jar -c -f ../jar/RiSC16_pipelinev1.1.jar -m MANIFEST.MF risc16_pipeline/*
```

with the manifest simply pointing to the entry point:

```
Manifest-Version: 1.0
Main-Class: risc16_pipeline.Principal
```

- The movi pseudo-instruction is replaced with the combination of lui and addi. The simulator currently requires the user to add a nop instruction after the movi, so that during the assembly step, the couple movi/nop is replaced with lui/addi.
An improvement could be to remove the need for that nop. When the assembler detects a movi, he should shift all the line in the code to insert the lui/addi.


### Installation through Ant apache

- Make sure you have Ant apache up to date and added to your environnement variable 'Path'.
- Open a command prompt in the directory containing 'build.xml' for the simulator you want installed 
  (should be sequential => ...\RISC16-master\RiSC16_seq).
  On windows, you can go to the directory and type 'cmd' in the path entry.
- Type:
```
ant build
```

You now have a jar ready to go.
