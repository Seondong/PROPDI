package kr.ac.kaist.dm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


/**
 * Main class for BBox Property Domain Inference (WiseKB WP6) Environment:
 * Virtuoso triple storage, RESTful API(Spring), Maven Version Control, Github
 *
 * @author Sundong Kim (sundong.kim@kaist.ac.kr)
 * 
 * 
 * Property Domain Inference (PROPDI) 
 * 
 */

public class PROPDI {
	
	private static final String iniFilePath = "./Config.ini"; // Initial
	static Model outputTTL;
	
	public static void main(String [] args) throws IOException{
		PROPDI p = new PROPDI();

		// Check Starting Time
		Calendar startTime = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		System.out.println("start " + dateFormat.format(startTime.getTime()));

		// Read Configuration File from config.ini
		ReadConfigurationFromINIFile.readConfigurationFromINIFile(iniFilePath);
		System.out.println("Written by Sundong Kim (sundong.kim@kaist.ac.kr)");
		
		// Load Knowledge base to perform PROPDI
		System.out.println("--------Loading Knowledge base--------");
		SchemaBased_Initialization si = new SchemaBased_Initialization();
		VirtGraph g = si.getInputKBGraph();
		Model owlModel = si.getOWLModel(g);
		System.out.println("Done!");
		
		outputTTL = ModelFactory.createDefaultModel();

		// Run PROPDI main pipeline
		p.propDomainInference(owlModel, g);

		// Check Finishing Time
		Calendar endTime = Calendar.getInstance();
		System.out.println("Done " + dateFormat.format(endTime.getTime()));

		// Write & Save Output file (TTL form into Local)
		FileOutputStream fos;
		String OUTPUT_FILE_PATH = ReadConfigurationFromINIFile.OUTPUT_FILE_PATH;
		File file = new File("./output/");
		file.mkdirs();
		
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
	

	private void propDomainInference(Model owlModel,
			VirtGraph g) throws IOException {

		String TEST_INPUT_IRI = "http://bboxtestinput2.kaist.ac.kr";
		String HOST = "jdbc:virtuoso://dmserver5.kaist.ac.kr:4004/charset=UTF-8/log_enable=2";
		String USERNAME = "dba";
		String PASSWORD = "dba";
		System.out.println("--------Processing PROPDI--------");

		VirtGraph set = new VirtGraph("http://ko.dbpedia.org", HOST, USERNAME,
				PASSWORD);
		
		String currentDir = System.getProperty("user.dir");
		File intermdir = new File(currentDir+"/intermediateResult/");
		File finaldir = new File(currentDir+"/finalResult/");
		intermdir.mkdirs();
		finaldir.mkdirs();
		
		
		/*** Generating Data ***/
		
		System.out.println("(1/9) Generating Instance-type frequency data - ./intermediateResult/insttypefreq.csv");
		
		File f1 = new File(intermdir+"/insttypefreq.csv");
		if (f1.exists() && !f1.isDirectory()) {
			System.out.println("File already exists - Done!");
		} else {
			HashMap<String, Double> insttypefreq = new HashMap<String, Double>();
			Query sparql3 = QueryFactory
					.create("SELECT ?class (COUNT(?s) AS ?count ) { ?s a ?class } GROUP BY ?class ORDER BY DESC(?count)");
			VirtuosoQueryExecution vqe3 = VirtuosoQueryExecutionFactory.create(
					sparql3, set);
			ResultSet results3 = vqe3.execSelect();
			while (results3.hasNext()) {
				QuerySolution result3 = results3.nextSolution();
				RDFNode class3 = result3.get("class");
				RDFNode instcount = result3.get("count");
				insttypefreq.put(class3.toString(), (double) instcount
						.asLiteral().getInt());
			}
			vqe3.close();
			PrintMapToCSV(insttypefreq, intermdir+"/insttypefreq.csv");
			System.out.println("File is succesfully created!");
		}

		
		
		/*** Generating Data2 ***/
		System.out.println("(2/9) Generating prop-ko list - ./intermediateResult/proptypeinference_propkolist.csv");
		
		File f2 = new File(intermdir+"/proptypeinference_propkolist.csv");
		if (f2.exists() && !f2.isDirectory()) {
			System.out.println("File already exists - Done!");
		} else {
			Query sparql = QueryFactory
					.create("SELECT DISTINCT ?p where { ?s ?p ?o . "
							+ "FILTER regex(str(?p), \"http://ko.dbpedia.org/property\" ). }");

			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
					sparql, set);
			ResultSet results = vqe.execSelect();

			ArrayList<RDFNode> propkolist = new ArrayList<RDFNode>();

			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
				RDFNode propko = result.get("p");
				propkolist.add(propko);
			}
			vqe.close();
			PrintDataToCommaDelimitedFile(propkolist,
					intermdir+"/proptypeinference_propkolist.csv");
			System.out.println("File is succesfully created!");
		}

		
		
		/*** Generating Data3 ***/
		System.out.println("(3/9) Finding number of entity for each prop-domain pair - ./intermediateResult/proptypefreq.csv");
		
		File f3 = new File(intermdir+"/proptypefreq.csv");
		if (f3.exists() && !f3.isDirectory()) {
			System.out.println("File already exists - Done!");
		} else {
			CSVReader reader2 = new CSVReader(new FileReader(
					intermdir+"/proptypeinference_propkolist.csv"));
			List<String[]> myEntries = reader2.readAll();
			String[] propkoLine = myEntries.get(0); // Prop-ko List in String
			// vector.
			reader2.close();

			RowSortedTable<String, String, Double> weightedGraph = TreeBasedTable
					.create();

			for (int i = 0; i < propkoLine.length; i++) {

				Query freqsparql = QueryFactory
						.create("SELECT ?class (count(distinct ?s) as ?count) where{?s <"
								+ propkoLine[i]
								+ "> ?o. ?s a ?class.} group by ?class order by DESC(?count)");
				VirtuosoQueryExecution freqvqe = VirtuosoQueryExecutionFactory
						.create(freqsparql, set);
				ResultSet freqresults = freqvqe.execSelect();
				while (freqresults.hasNext()) {
					QuerySolution freqresult = freqresults.nextSolution();
					RDFNode domain = freqresult.get("class");
					RDFNode freqcount = freqresult.get("count");

					weightedGraph.put(propkoLine[i], domain.toString(),
							(double) freqcount.asLiteral().getInt());
				}
			}
			PrintTableToCSV(weightedGraph, intermdir+"/proptypefreq.csv");
			System.out.println("File is succesfully created!");
		}

		HashMap<String, Double> insttypefreq = ReadMapFromCSV(intermdir+"/insttypefreq.csv");

		RowSortedTable<String, String, Double> proptypefreq = ReadTableFromCSV(intermdir+"/proptypefreq.csv");

		
		
		/*** Generating Data4 ***/
		System.out.println("(4/9) Finding apriori probability of prop-domain pair - ./intermediateResult/proptypeprob.csv");
		
		File f4 = new File(intermdir+"/proptypeprob.csv");
		if (f4.exists() && !f4.isDirectory()) {
			System.out.println("File already exists - Done!");
		} else {
			RowSortedTable<String, String, Double> proptypeprob = generatePropTypeProb(
					proptypefreq, insttypefreq, intermdir+"/proptypeprob.csv");
			System.out.println("File is succesfully created!");
		}

		RowSortedTable<String, String, Double> proptypeprob = ReadTableFromCSV(intermdir+"/proptypeprob.csv");

		
		
		/*** Generating Data5 ***/
		System.out.println("(5/9) Calculating baseline prob value for each property - ./intermediateResult/averageValueForProp.csv");
		
		File f5 = new File(intermdir+"/averageValueForProp.csv");
		if (f5.exists() && !f5.isDirectory()) {
			System.out.println("File already exists - Done!");
		} else {
			HashMap<String, Double> averageValueForProp = new HashMap<String, Double>();
			for (String prop : proptypeprob.rowKeySet()) {
				double sum = 0.0;
				for (double d : proptypeprob.row(prop).values()) {
					sum += d;
				}
				double average = sum
						/ (double) proptypeprob.columnKeySet().size();
				averageValueForProp.put(prop, average);
			}

			PrintMapToCSV(averageValueForProp, intermdir+"/averageValueForProp.csv");
			System.out.println("File is succesfully created!");
		}

		HashMap<String, Double> averageValueForProp = ReadMapFromCSV(intermdir+"/averageValueForProp.csv");

		
		
		
		/*** Generating Data6-1 (Recall%, SRDF-like TTL data) ***/
		System.out.println("(6/9) Generating confidence value of property-domain pair - ./intermediateResult/proptypeconf.csv");
		
		File f6 = new File(intermdir+"/proptypeconf.csv");
		if (f6.exists() && !f6.isDirectory()) {
			System.out.println("File already exists - Done!");
		} else {
			RowSortedTable<String, String, Double> proptypeconf = TreeBasedTable
					.create();
			for (String type : proptypeprob.columnKeySet()) {
				for (Entry<String, Double> e : proptypeprob.column(type)
						.entrySet()) {
					if (averageValueForProp.containsKey(e.getKey())) {
						double relativeVal = e.getValue()
								/ averageValueForProp.get(e.getKey());
						proptypeconf.put(e.getKey(), type, relativeVal);
					}
				}
			}
			PrintTableToCSV(proptypeconf, intermdir+"/proptypeconf.csv");
			System.out.println("File is succesfully created!");
		}

		RowSortedTable<String, String, Double> proptypeconf = ReadTableFromCSV(intermdir+"/proptypeconf.csv");

//		int numMaxMatches = proptypeconf.size(); // Number of possible
//													// matches(prop-domain)
//		System.out.println(numMaxMatches);
//		double algorithmRecall = 100; // Among numMaxMatches, decide percentages
//		// check it is okay or not (too little - remove)
//
//		// simple recall ver
//		List<Double> conflist = new ArrayList<Double>(proptypeconf.values());
//		Collections.sort(conflist);
//		Collections.reverse(conflist);
//		int index = (int) Math.ceil(algorithmRecall * numMaxMatches / 100);
//		System.out.println(index + "," + conflist.get(index - 1));
//
//		Iterator<Double> it = proptypeconf.values().iterator();
//		while (it.hasNext()) {
//			if (it.next() < conflist.get(index - 1)) {
//				it.remove();
//			}
//		}
//
//		`(proptypeconf.size());
//		
//		Model outputTTL = ModelFactory.createDefaultModel();
//		
//		
//		Property propConfidence = outputTTL
//				.createProperty("http://srdf.kaist.ac.kr/property/confidenceScore");
//		Property rdfsdomain = outputTTL.createProperty("http://www.w3.org/2000/01/rdf-schema#domain");
//		
//		for (Cell<String, String, Double> cell: proptypeconf.cellSet()){
//			outputTTL.createResource(rdfsdomain+"#"+cell.getRowKey()+"#"+cell.getColumnKey()).addLiteral(propConfidence, cell.getValue());
//		
//		}
//
//		// generate rdf result
//		// Write & Save Output file (TTL form into Local)
//		
//		
//		FileOutputStream fos;
//		String OUTPUT_FILE_PATH = "proptypeconfidence2.ttl";
//		try {
//			fos = new FileOutputStream(OUTPUT_FILE_PATH);
//			outputTTL.write(new OutputStreamWriter(fos, "UTF8"), "TURTLE");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		outputTTL.close();
//		
		
		
		
		/*** Generating Data 7 (Find maximum value of each property for reference) ***/
		System.out.println("(7/9) Find maximum value of each property for reference - ./intermediateresult/sortedproptypemaxfreq.csv");
		
		File f7 = new File(intermdir+"/sortedproptypemaxfreq.csv");
		if (f7.exists() && !f7.isDirectory()) {
			System.out.println("File already exists - Done!");
		} else {
			
			Map<String, Double> sortedproplistbyfreq = new HashMap<String, Double>();
			for (String prop : proptypefreq.rowKeySet()){
				sortedproplistbyfreq.put(prop, Collections.max(proptypefreq.row(prop).values()));
			}
			sortedproplistbyfreq = sortByValue(sortedproplistbyfreq);
			PrintMapToCSV(sortedproplistbyfreq , intermdir+"/sortedproptypemaxfreq.csv");
			System.out.println("File is succesfully created!");
		}
		
		Map<String, Double> sortedproplistbyfreq = ReadMapFromCSV(intermdir+"/sortedproptypemaxfreq.csv");
		
		
		
		
		/*** Generating Data 8 (Find maximum value of each class for reference) ***/
		System.out.println("(8/9) Find maximum value of each class for reference - ./intermediateresult/classMaxConf.csv");
		
		File f8 = new File(intermdir+"/classMaxConf.csv");
		if (f8.exists() && !f8.isDirectory()) {
			System.out.println("File already exists - Done!");
		} else {
			
			HashMap<String, Double> classMaxConf = new HashMap<String, Double>();
			Set<String> classSet = proptypeconf.columnKeySet();
			for(String eachClass : classSet){
				classMaxConf.put(eachClass, Collections.max(proptypeconf.column(eachClass).values()));	
			}
			PrintMapToCSV(classMaxConf , intermdir+"/classMaxConf.csv");
			System.out.println("File is succesfully created!");
		}
		
		Map<String, Double> classMaxConf = ReadMapFromCSV(intermdir+"/classMaxConf.csv");
		
		

		
		
		System.out.println("(9/9) Finding Top-1 relevant domain based on intermediate results");
		
		
		DecimalFormat newFormat = new DecimalFormat("#.####");
		
//		Set<String> propSet = proptypeconf.rowKeySet();  //prop 이름순서 
		Set<String> propSet = sortedproplistbyfreq.keySet(); //prop 빈도순
		List<String> anotherPropList = new ArrayList<String>();
		anotherPropList.addAll(propSet);
		
		double coverage = 1.0;
		
		double totalfreq = sumFloat(sortedproplistbyfreq.values()).doubleValue();
		double loopSum = 0.0;
		String target = "";
		
		for(Entry<String, Double> entry : sortedproplistbyfreq.entrySet()){
			loopSum += entry.getValue();
			if(loopSum >=  totalfreq * coverage){
				target = entry.getKey();
				break;
			}		
		}
		
		int sizeSubset = anotherPropList.indexOf(target)+1;
		
		
//		System.out.format("Number of property needed for %.4f percent coverage is %d\n", coverage*100, sizeSubset);
		
//		Subset:   ImmutableSet.copyOf(Iterables.limit(propSet, 20))  //Subset
		
		
		
		VirtGraph set5 = new VirtGraph(HOST, USERNAME,
				PASSWORD);
		
		Query sparql5 = QueryFactory
				.create("SELECT ?subclass ?class where{ ?subclass <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?class }");
		VirtuosoQueryExecution vqe5 = VirtuosoQueryExecutionFactory.create(
				sparql5, set5);
		ResultSet results5 = vqe5.execSelect();
		List<String> classaggList = new ArrayList<String>();
		
		while (results5.hasNext()) {
			QuerySolution result5 = results5.nextSolution();
			RDFNode class5 = result5.get("class");
			RDFNode subclass5 = result5.get("subclass");
			classaggList.add(class5.toString()+","+subclass5.toString());
		}
		vqe5.close();
		
		
		LinkedHashMap<String, String> propdomainpreliminaryresult = new LinkedHashMap<String, String>();
		
		
		for (String eachProp : ImmutableSet.copyOf(Iterables.limit(propSet, sizeSubset))){
			HashMap<String, Double> relativeConf = new HashMap<String, Double>();
			for(Entry<String, Double> e : proptypeconf.row(eachProp).entrySet()){
				double roundedVal = Double.valueOf(newFormat.format(e.getValue() / classMaxConf.get(e.getKey())));
				relativeConf.put(e.getKey(), roundedVal);
			}
			
			Set<String> mostRelevantDomainSet = getKeysByValue(relativeConf, Collections.max(relativeConf.values()));
			String top1domain = "";
			
			if (mostRelevantDomainSet.size() > 1){
				double maxVal = 0.0;
				for(String candidate : mostRelevantDomainSet){
					if(insttypefreq.get(candidate) >= maxVal){
						maxVal = insttypefreq.get(candidate);
					}
				}
				
				Set<String> subset = Sets.intersection(mostRelevantDomainSet, getKeysByValue(insttypefreq, maxVal));
				Set<String> rsubset = new HashSet<String>();
				if(String.join(",", subset).contains("http://dbpedia.org/ontology/")){
					String regex = "http://dbpedia.org/ontology/[a-zA-Z]+";
					for (String s : subset){
						if(s.matches(regex)){
							rsubset.add(s);
						}
					}
				}else{
					rsubset.addAll(subset);
				}
				
				// subset이 dbpedia.org/ontology 관련된 class를 가지고 있으면 얘네만 패턴매칭해서 가져오고
				// 가지고 있지 않으면 그냥 그대로 ?
//				System.out.println("Subset: " + subset.size() + "-" + subset);
//				System.out.println(eachProp + "--rSubset: " + rsubset.size() + "-" + rsubset);
				
				if(rsubset.size() == 3){
					top1domain = "http://dbpedia.org/ontology/TopicalConcept";    //임시로 배정 
				}else if (rsubset.size() == 2){
					if(String.join(",", rsubset).contains("Event") && String.join(",", rsubset).contains("SocietalEvent")){
						top1domain = "http://dbpedia.org/ontology/Event";
					}else if(String.join(",", rsubset).contains("SportsFacility") && String.join(",", rsubset).contains("Stadium")){
						top1domain = "http://dbpedia.org/ontology/SportsFacility";
					}else if(String.join(",", rsubset).contains("AdministrativeRegion") && String.join(",", rsubset).contains("Region")){
						top1domain = "http://dbpedia.org/ontology/Region";
					}else
						top1domain = "http://www.w3.org/2002/07/owl#Thing";
				}else if(rsubset.size() == 1){
					top1domain = String.join(",", rsubset);
				}
				
			}else{
				top1domain = mostRelevantDomainSet.iterator().next();
//				System.out.println("Subset: " + "1-" + top1domain);
			}
		
			
			propdomainpreliminaryresult.put(eachProp, top1domain);
		}
		
		PrintStringMapToCSV(propdomainpreliminaryresult, finaldir+"/PROPDI_top1_result.csv");
		System.out.println("Done! - check ./finalResult directory.");
		
		

	}
	
	
	
	
	public static BigDecimal sumFloat(final Collection<? extends Number> numbers) {
	    BigDecimal sum = BigDecimal.ZERO;
	    for (final Number number : numbers)
	        sum = sum.add(new BigDecimal(number.longValue()));
	    return sum;
	}
	
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	
	
	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
	    Set<T> keys = new HashSet<T>();
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            keys.add(entry.getKey());
	        }
	    }
	    return keys;
	}
	
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private RowSortedTable<String, String, Double> generatePropTypeProb(
			RowSortedTable<String, String, Double> proptypefreq,
			HashMap<String, Double> insttypefreq, String path)
			throws IOException {

		RowSortedTable<String, String, Double> proptypeconf = TreeBasedTable
				.create();
		RowSortedTable<String, String, Double> temp = TreeBasedTable.create();

		// for문으로 돌리면 안될거같아.
		for (String a : proptypefreq.rowKeySet()) {
			for (String b : proptypefreq.columnKeySet()) {
				if (proptypefreq.get(a, b) != null) {
					// System.out.println(a + ", " + b + ", " +
					// proptypefreq.get(a, b) + ", " + insttypefreq.get(b,
					// "count"));
					double c = proptypefreq.get(a, b)
							/ insttypefreq.get(b);
					temp.put(a, b, c);
					// System.out.println(a+b+c);
				}
			}
		}

		proptypeconf = temp;
		PrintTableToCSV(proptypeconf, path);
		return proptypeconf;
	}

	private HashMap<String, Double> ReadMapFromCSV(String path)
			throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, Double> map = new HashMap<String, Double>();
		CSVReader reader = new CSVReader(new FileReader(path));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			map.put(nextLine[0], Double.parseDouble(nextLine[1]));
		}
		reader.close();
		return map;
	}

	private RowSortedTable<String, String, Double> ReadTableFromCSV(String path)
			throws IOException {
		// TODO Auto-generated method stub
		RowSortedTable<String, String, Double> table = TreeBasedTable.create();
		CSVReader reader = new CSVReader(new FileReader(path));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			table.put(nextLine[0], nextLine[1], Double.parseDouble(nextLine[2]));
		}
		reader.close();
		return table;
	}

	private void PrintMapToCSV(Map<String, Double> map, String path)
			throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(path), ',');
		for (String eachprop : map.keySet()) {
			try {
				writer.writeNext(new String[] { eachprop,
						map.get(eachprop).toString()});
			} catch (NullPointerException e) {

			}
		}
		writer.close();
	}
	
	private void PrintStringMapToCSV(Map<String, String> map, String path)
			throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(path), ',');
		for (String eachprop : map.keySet()) {
			try {
				writer.writeNext(new String[] { eachprop,
						map.get(eachprop)});
			} catch (NullPointerException e) {

			}
		}
		writer.close();
	}
	
	

	private void PrintTableToCSV(
			RowSortedTable<String, String, Double> weightedGraph, String path)
			throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(path), ',');
		for (String eachprop : weightedGraph.rowKeySet()) {
			for (String eachtype : weightedGraph.columnKeySet()) {
				try {
					writer.writeNext(new String[] { eachprop, eachtype,
							weightedGraph.get(eachprop, eachtype).toString() });
				} catch (NullPointerException e) {

				}
			}
		}
		writer.close();
	}

	public void PrintDataToCommaDelimitedFile(ArrayList<RDFNode> a, String path) {
		try {
			// Tab delimited file will be written to data with the name
			// tab-file.csv
			FileWriter fos = new FileWriter(path);
			PrintWriter dos = new PrintWriter(fos);
			// loop through all your data and print it to the file
			for (int i = 0; i < a.size() - 1; i++) {
				dos.print(a.get(i) + ",");
			}
			dos.print(a.get(a.size() - 1));
			dos.close();
			fos.close();
		} catch (IOException e) {
			System.out.println("Error Printing Tab Delimited File");
		}
	}
}
