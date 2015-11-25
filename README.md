## PROPDI: Property Domain Inference Module,  Ver. Nov-2015  

### How to run PROPDI

1. Download __PROPDI__
    ```
    git clone https://github.com/Seondong/PROPDI.git 
    ```

2. Edit `config.ini` file according to the settings

3. For running __PROPDI__ on your machine, go to the root directory and execute this command.
   Since `virt-jena.jar`, `virt-jdbc.jar` cannot be found in maven web repository, we generated in-project repo. 
    ```
    mvn -U package
    ```
   then `PROPDI-1.0-SNAPSHOR.jar` file will be generated in `./target` directory.

4. Then run the generated `.jar` file.
	* Quick check that our `.jar` file is built
		```
		java -cp target/PROPDI-1.0-SNAPSHOT.jar kr.ac.kaist.dm.App
		```
    * Run PROPDI class
		```
		java -cp target/PROPDI-1.0-SNAPSHOT.jar kr.ac.kaist.dm.PROPDI
		```
		
5. You can find intermediate results in `./intermediate` and final property-domain result in `./final` directories.

    1. Intermediary output (./intermediateResult/classMaxConf.csv)
    
        This result shows the __maximum relatedness score__ (of property) related to the __following class__.

        | Domain                            | Score(Max) |
        |-----------------------------------|--------------|
        | http://dbpedia.org/ontology/Place |        22.31 |
        | /ArchitecturalStructure           |        34.49 |
        | /Building                         |        41.96 |
        | /Hotel                            |       105.02 |
        | /Skyscraper                       |       157.76 |	


    2. Intermediary output (./finalResult/proptypeconf.csv)
    
        This result shows the __relatedness score__ of property to relevant domains.

        | Property     | Domain                  | Score |
        |--------------|-------------------------|---------|
        | prop:ko/층수 | /Place                  |    1.31 |
        | prop:ko/층수 | /ArchitecturalStructure |    2.02 |
        | prop:ko/층수 | /Building               |   24.79 |
        | prop:ko/층수 | /Hotel                  |   44.33 |
        | prop:ko/층수 | /Skyscraper             |   90.83 |


    3.  Final __PROPDI__ result (./finalResult/PROPDI_top1_result.csv)

        By referring 1 and 2, __the most relevant domain__ is categorized for __each property__.

        | Property                                | Domain                               |
        |-----------------------------------------|--------------------------------------|
        | http://ko.dbpedia.org/property/층수     | http://dbpedia.org/ontology/Building |
        | http://ko.dbpedia.org/property/작곡가   | http://dbpedia.org/ontology/Song     |
        | http://ko.dbpedia.org/property/자매기업 | http://dbpedia.org/ontology/Company  |
