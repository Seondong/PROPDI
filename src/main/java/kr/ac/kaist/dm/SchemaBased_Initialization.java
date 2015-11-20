package kr.ac.kaist.dm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.google.common.collect.ArrayListMultimap;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class SchemaBased_Initialization {

	private static final String PropkoDBPMappingPath = ReadConfigurationFromINIFile.PropkoDBPMappingPath;
	private static final String GRAPH = ReadConfigurationFromINIFile.GRAPH;
	private static final String USERNAME = ReadConfigurationFromINIFile.USERNAME;
	private static final String PASSWORD = ReadConfigurationFromINIFile.PASSWORD;
	private static final String HOST = ReadConfigurationFromINIFile.HOST;
	private static final String TEST_OUTPUT_IRI = ReadConfigurationFromINIFile.TEST_OUTPUT_IRI;
	
	public VirtGraph inputKBGraph, outputGraph;
	private Model owlModel;
	
	

	public VirtGraph getInputKBGraph() {
		inputKBGraph = new VirtGraph(GRAPH, HOST, USERNAME, PASSWORD);
		inputKBGraph.read("http://dbpedia.org/resource/classes#dbpedia_2014.owl",
				"OWL");
		return inputKBGraph;
	}
	
	public VirtGraph getOutputGraph(){
		VirtGraph outputGraph = new VirtGraph(TEST_OUTPUT_IRI, HOST, USERNAME, PASSWORD);
		return outputGraph;
	}
	
	
	public Model getOWLModel() {
		inputKBGraph = getInputKBGraph();
		Model owlModel = ModelFactory.createModelForGraph(inputKBGraph);
		owlModel.read("http://dbpedia.org/resource/classes#dbpedia_2014.owl");
		return owlModel;
	}
	
	// Read dbo-prop:ko-mapping file and generate hashmap related to it
	public ArrayListMultimap<String, String> generatePropkoDBOMap() {
		System.out.println(" -------- Generate DBP-Prop:ko Map --------");

		ArrayListMultimap<String, String> map = ArrayListMultimap.create();

		try {
			// //////////////////////////////////////////////////////////////
			System.out.println(PropkoDBPMappingPath);
			BufferedReader in = new BufferedReader(new FileReader(
					PropkoDBPMappingPath));
			String s;

			while ((s = in.readLine()) != null) {
				// System.out.println(s);
				StringTokenizer tk = new StringTokenizer(s, "\t");
				while (tk.hasMoreTokens()) {
					String propko = tk.nextToken();
					String dbpprop = tk.nextToken();

					// String remaining = tk.nextToken();
					// dbpprop = dbpprop.substring(1, dbpprop.length()-1);
					// propko = propko.substring(1, propko.length()-1);
					// System.out.println(propko + "--" + dbpprop); //print
					// propko-dbp list

					map.put(propko, dbpprop);
				}
			}
			in.close();
			// //////////////////////////////////////////////////////////////
		} catch (IOException e) {
			System.err.println(e); // 에러가 있다면 메시지 출력
			System.exit(1);
		}
		// System.out.println(map.toString());
		return map;
	}

	public ArrayListMultimap<String, String> generatePropertyDomainMap(VirtGraph graph) {
		// Load Property-Domain Information from the OWL file and put it on the
		// memory(Multimap)

		System.out.println(" -------- Generate Property-Domain Map --------");
	
		Query sparql6 = QueryFactory
				.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ "SELECT ?s ?domain "
						+ "where {?s rdfs:domain ?domain}");

		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
				sparql6, graph);
		ResultSet results = vqe.execSelect();
		ArrayListMultimap<String, String> map = ArrayListMultimap.create();

		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode s1 = result.get("s");
			RDFNode d1 = result.get("domain");
			// System.out.println(s1.toString() + "--" + d1.toString());
			// System.out.println("-");
			map.put(s1.toString(), d1.toString());
		}

		/*
		 * for(java.util.Map.Entry<String, String> e : map.entries()) {
		 * System.out.println(e.getKey()+": "+e.getValue()); }
		 */
		// _graph.close();
		// System.out.println(map.toString());
		return map;
	}



	

}
