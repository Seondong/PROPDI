## PROPDI: Property Domain Inference Module,  Ver. Nov-2015  

### How to run PROPDI

1. Download PROPDI
```
git clone https://github.com/Seondong/PROPDI.git 
```

2. Edit config.ini file according to the settings

3. For running PROPDI on your machine, go to the root directory and execute
```
mvn package
```
   then PROPDI-1.0-SNAPSHOR.jar file is generated on ./target directory.

4. Then run the generated .jar file.
```
java cp target/PROPDI-1.0-SNAPSHOT.jar kr.ac.kaist.dm.PROPDI
```
5. You can find intermediate results in ./intermediate and final property-domain result in ./final 
