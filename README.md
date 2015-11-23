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

    1. Intermediate output (./intermediateResult/classMaxConf.csv)
    
        This result shows the maximum relatedness score (of property) related to the following class.

        | Domain                            | ConfVal(Max) |
        |-----------------------------------|--------------|
        | http://dbpedia.org/ontology/Place |        22.31 |
        | /ArchitecturalStructure           |        34.49 |
        | /Building                         |        41.96 |
        | /Hotel                            |       105.02 |
        | /Skyscraper                       |       157.76 |	


    2. Output example (./finalResult/proptypeconf.csv)
    
        This result shows the relatedness score of property to relevant domains.

        | Property     | Domain                  | ConfVal |
        |--------------|-------------------------|---------|
        | prop:ko/층수 | /Place                  |    1.31 |
        | prop:ko/층수 | /ArchitecturalStructure |    2.02 |
        | prop:ko/층수 | /Building               |   24.79 |
        | prop:ko/층수 | /Hotel                  |   44.33 |
        | prop:ko/층수 | /Skyscraper             |   90.83 |


    3.  Output example (./finalResult/PROPDI_top1_result.csv)

        By referring 1 and 2, the most relevant domain is categorized for each property.

        | Property                                | Domain                               |
        |-----------------------------------------|--------------------------------------|
        | http://ko.dbpedia.org/property/층수     | http://dbpedia.org/ontology/Building |
        | http://ko.dbpedia.org/property/작곡가   | http://dbpedia.org/ontology/Song     |
        | http://ko.dbpedia.org/property/자매기업 | http://dbpedia.org/ontology/Company  |
