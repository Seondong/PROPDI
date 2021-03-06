package kr.ac.kaist.dm;

import java.io.File;
import java.io.IOException;

import org.ini4j.Wini;

public class ReadConfigurationFromINIFile {
	
	// [BBOX]
	public static String PropkoDBPMappingPath; // Prop:ko-DBO Mapping file path (.txt)
	public static String srdf; // srdf property prefix
	public static String INPUT_IRI; // Graph IRI of Input triple file
	public static String OUTPUT_IRI; // Graph IRI of Output triple file
	public static String TEST_OUTPUT_IRI; //Graph IRI of 2014 Output triple file
	public static String TEST_INPUT_IRI;
	public static String USERNAME; // Username for Virtuoso Connection
	public static String PASSWORD; // Password for Virtuoso Connection
	public static String HOST; // Virtuoso Server information
	public static String MODEL; // Schema File in Virtuoso (e.g. dbpedia_2014.owl)
	public static String GRAPH; // Virtgraph for model (IRI)
	public static String OUTPUT_FILE_PATH; // BBox output file path (in local)
	public static Integer LOAD_TRIPLE_OFFSET; // Number of triple to load each time
	
	// [SDTYPE]
	public static String SDTYPE_TYPE_PATH;
	public static String SDTYPE_TYPE_DISTRIBUTION_PATH;
	public static String SDTYPE_PREDICATE_WEIGHT_PATH;
	public static String SDTYPE_TESTTXT_PATH;
	
	// [EXPERIMENT]
	public static String GROUNDTRUTH_PATH;
	public static String SAMPLE_OUTPUT_PATH;
	
	//[XBONTO]
	public static String XBONTO_TYPE_DISTRIBUTION_PATH;
	public static String XBONTO_PREDICATE_WEIGHT_PATH;
	public static String XB_INPUT_IRI;
	public static String XB_TEST_INPUT_IRI;
	public static String XB_OUTPUT_IRI;
	public static String XB_TEST_OUTPUT_IRI;
	public static String XB_OUTPUT_FILE_PATH;
	
	
	
	public static void readConfigurationFromINIFile(String filename)
			throws IOException {
		Wini ini = new Wini(new File(filename));
		
		// [BBOX]
		GRAPH = ini.get("BBOX", "ONTOLOGY_GRAPH");
		MODEL = ini.get("BBOX", "KNOWLEDGE_BASE_FOR_BBOX");
		HOST = ini.get("BBOX", "VIRTUOSO_SERVER");
		USERNAME = ini.get("BBOX", "VIRTUOSO_USERNAME");
		PASSWORD = ini.get("BBOX", "VIRTUOSO_PASSWORD");
		TEST_OUTPUT_IRI = ini.get("BBOX", "TEST_OUTPUT_IRI");
		PropkoDBPMappingPath = ini.get("BBOX", "PROPKO_DBP_MAPPING_FILE");
		OUTPUT_FILE_PATH = ini.get("BBOX", "OUTPUT_FILE_PATH");
	
	}
}
