## PROPDI: Property Domain Inference Module,  Ver. Nov-2015  

### How to run PROPDI

1. Download __PROPDI__
    ```
    git clone https://github.com/Seondong/PROPDI.git 
    ```

2. Edit `config.ini` file according to the settings

3. For running __PROPDI__ on your machine, go to the root directory and execute
    ```
    mvn package
    ```
   then `PROPDI-1.0-SNAPSHOR.jar` file will be generated in `./target` directory.

4. Then run the generated `.jar` file.
    ```
    java cp target/PROPDI-1.0-SNAPSHOT.jar kr.ac.kaist.dm.PROPDI
    ```
5. You can find intermediate results in `./intermediate` and final property-domain result in `./final` directories.

	* Output example (proptypeconf.csv)
    Property | Domain | ConfVal
    ------------------------------------ | ------------------------------------------- | -----------------------------------------
    http://ko.dbpedia.org/property/층수 | http://schema.org/Place  | 1.308368839665762
    http://ko.dbpedia.org/property/층수 | http://dbpedia.org/ontology/ArchitecturalStructure | 2.021948697999311
    http://ko.dbpedia.org/property/층수 | http://dbpedia.org/ontology/Building | 24.79589237729011
    http://ko.dbpedia.org/property/층수 | http://dbpedia.org/ontology/Hotel | 44.332050007882316
    http://ko.dbpedia.org/property/층수 | http://dbpedia.org/ontology/Skyscraper  | 90.83723972203339


