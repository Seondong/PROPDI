package kr.ac.kaist.dm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.rdf.model.Model;


/**
 * Main class for BBox Property Domain Inference (WiseKB WP6) Environment:
 * Virtuoso triple storage, RESTful API(Spring), Maven Version Control, Github
 *
 * @author Sundong Kim (sundong.kim@kaist.ac.kr)
 * 
 * 
 *         Previous version of Property Domain Inference so called Property
 *         generalization works based on a basic probability model. If a
 *         property is shared by the most instance in certain type (over the
 *         threshold), then we infer that the property has enough evidence to
 *         have that type as a domain. The formula was as follows.
 *         threhold(property has a type t) = 1/(1+logx), x = number of instance
 *         in that class. This formula consider that larger class's data
 *         sparsity. And we also developed property domain deletion equation as
 *         opposed to this.
 * 
 *         However, there are some issues. By looking at the real data, certain
 *         property are too general so that every instance in the class might
 *         contains, and some are too specific that only very few instance
 *         contains even though it is a right answer.
 * 
 *         Class(Type, Domain) Property1: # of instances having p1 Property2: #
 *         of instances having p2 Property3: # of instances having p3 Property4:
 *         # of instances having p4
 * 
 * 
 */

public class PROPDI {
	
	private static final String iniFilePath = "../Config.ini"; // Initial
	static Model outputTTL;
	
	public static void main(String [] args){
		
		PROPDI p = new PROPDI();
		System.out.println("Unique ID : " + p.generateUniqueKey());

		// Check Starting Time
		Calendar startTime = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		System.out.println("start " + dateFormat.format(startTime.getTime()));

//		// Read Configuration File from config.ini
		ReadConfigurationFromINIFile.readConfigurationFromINIFile(iniFilePath);
		System.out.println("Written by Sundong Kim");

		SchemaBased_Initialization si = new SchemaBased_Initialization();
		Model owlModel = si.getOWLModel();
		VirtGraph g = si.getInputKBGraph();
		System.out.println(owlModel.size());
		System.out.println(g.size());
		
		/***
		 * Load Prop:ko-DBO Mapping Information from text file and put it on the
		 * memory(Multimap) - relatively small size
		 ***/
		ArrayListMultimap<String, String> propko_dbp_map = si
				.generatePropkoDBOMap();

		/***
		 * Load Property-Domain Information from the OWL file and put it on the
		 * memory(Multimap)
		 ***/
		ArrayListMultimap<String, String> property_domain_map = si
				.generatePropertyDomainMap(g);
		System.out.println(property_domain_map.size());

		/***
		 * Generate predicate weight & Type distribution table
		 */
		InstanceBased_Initialization ii = new InstanceBased_Initialization();

		HashMap<String, Double> predicate_weight = ii.getWeightTable();
		Table<String, String, Double> type_distribution = HashBasedTable
				.create();
		type_distribution = ii.getDistributionTable();

		outputTTL = ModelFactory.createDefaultModel();

		// Set<String> new_subject = new TreeSet<String>();

		System.out.println("dd");

		p.propDomainInference2(property_domain_map, propko_dbp_map, owlModel, g);

		// VirtGraph outputGraph = si.getOutputGraph();
		//
		// outputGraph.close();

		// int endOfFile =
		// b.ProcessCertainAmountTriplesFromLocal(property_domain_map,
		// propko_dbp_map, predicate_weight, type_distribution);

		// Check Finishing Time
		Calendar endTime = Calendar.getInstance();
		System.out.println("Done " + dateFormat.format(endTime.getTime()));

		// Write & Save Output file (TTL form into Local)
		FileOutputStream fos;
		String OUTPUT_FILE_PATH = ReadConfigurationFromINIFile.OUTPUT_FILE_PATH;

		try {
			fos = new FileOutputStream(OUTPUT_FILE_PATH);
			outputTTL.write(new OutputStreamWriter(fos, "UTF8"), "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outputTTL.close();
		owlModel.close();		
	}
	
	public String generateUniqueKey(){
		
		String id = UUID.randomUUID().toString();
		return id;
		
	}

}
