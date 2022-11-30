package qengine.program;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;

/**
 * Programme simple lisant un fichier de requête et un fichier de données.
 * 
 * <p>
 * Les entrées sont données ici de manière statique,
 * à vous de programmer les entrées par passage d'arguments en ligne de commande comme demandé dans l'énoncé.
 * </p>
 * 
 * <p>
 * Le présent programme se contente de vous montrer la voie pour lire les triples et requêtes
 * depuis les fichiers ; ce sera à vous d'adapter/réécrire le code pour finalement utiliser les requêtes et interroger les données.
 * On ne s'attend pas forcémment à ce que vous gardiez la même structure de code, vous pouvez tout réécrire.
 * </p>
 * 
 * @author Olivier Rodriguez <olivier.rodriguez1@umontpellier.fr>
 */
public final class Main {
	static final String baseURI = null;
	private static String queryFolder;
	private static String dataFile;
	private static String outputFolder = "output";
	private static boolean useJena = false;
	private static boolean warm = false;
	private static boolean shuffle = false;

	static KnowledgeBase knowledgeBase;



	// ========================================================================

	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet obtenu.
	 */
	public static void processAQuery(ParsedQuery query, FileWriter file) {
		//System.out.println("Query: " + query);
		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

		//TODO: Modif first pattern pour récupérer toutes les branches de la requête, actuellement il prend que la premiere

		Set<Integer> answers = new HashSet<>();
		boolean firstEmpty = true;
		String request = "";
		request += "SELECT ?v0 WHERE {";
		//System.out.println(request);
		for (StatementPattern pattern : patterns) {
			/*System.out.println("Pattern:");
			System.out.print("Subject: " + pattern.getSubjectVar());
			System.out.print("Predicate: " + pattern.getPredicateVar());
			System.out.println("Object: " + pattern.getObjectVar());*/
			//System.out.println("\t" + pattern.getSubjectVar().getName() + " " + pattern.getPredicateVar().getValue() + " " + pattern.getObjectVar().getValue() + " .");
			request += "?" + pattern.getSubjectVar().getName() + " " + pattern.getPredicateVar().getValue() + " " + pattern.getObjectVar().getValue() + " .";

			Set<Integer> localAnswers = knowledgeBase.getAnswers(pattern);
			if(localAnswers.isEmpty()){
				answers = new HashSet<>();
				break;
			}
			//TODO: Si l'intersection des résultats est vide, on peut arrêter la boucle
			else if (firstEmpty) {
				answers.addAll(localAnswers);
				firstEmpty = false;
			}
			else {
				answers.retainAll(localAnswers);
			}

			if(answers.isEmpty() && !firstEmpty){
				break;
			}
		}

		//System.out.println("}");
		request += "}";
		if (answers.isEmpty()) {
			//System.out.println("No answer\n");
			try {
				file.append(dataFile + "  ,  " + request + "  ,  " + "No answer\n");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			//System.out.println("Answers:");
			for (Integer answer : answers) {
				//System.out.println("\t" + knowledgeBase.getDicoReverse().get(answer));
				try {
					file.append(dataFile + "  ,  " + request + "  ,  " + knowledgeBase.getDicoReverse().get(answer) + "\n");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			//System.out.println();
		}
		//System.out.println("first pattern : " + patterns.get(0));



		//Retourne le 3eme élément de la branche d'une requête
		//System.out.println("object of the first pattern : " + patterns.get(0).getObjectVar().getValue());

		//Recupère le sujet de la requête
		//System.out.println("variables to project : ");

		//Affichage des résultats pour chaque requêtes


		// Utilisation d'une classe anonyme
		/*query.getTupleExpr().visit(new AbstractQueryModelVisitor<RuntimeException>() {

			public void meet(Projection projection) {
				//System.out.println(projection.getProjectionElemList().getElements());
			}
		});*/
	}

	private static void printHelp() {
		System.out.println("Usage: java -jar rdfengine --queries <queryfile> --data <datafile>");
		System.out.println("-q --queries <queryfile>\n\tPath to the file containing the queries");
		System.out.println("-d --data <datafile>\n\tPath to the file containing the knowledge base");
		System.out.println("-o, --output <output_folder>\n\tSpecify the output file with --output <outputfile> (default is output)");
		System.out.println("-J, --Jena\n\tUse Jena to verify the answers, and check completeness");
		System.out.println("-w, --warm\n\tPrint this message");
		System.out.println("-s, --shuffle\n\tShuffle the queries before processing them");
		System.out.println("-h, --help\n\tDisplay this message again");
		System.exit(0);
	}

	private static void processArguments(String[] args) {
		if (args.length < 4) {
			System.out.println("Not enough arguments");
			printHelp();
		}
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "-q":
				case "--queries":
					if (i + 1 < args.length) {
						queryFolder = args[i + 1];
					}
					else {
						System.err.println("Missing argument for " + args[i]);
						printHelp();
					}
					break;
				case "-d":
				case "--data":
					if (i + 1 < args.length) {
						dataFile = args[i + 1];
					}
					else {
						System.err.println("Missing argument for " + args[i]);
						printHelp();
					}
					break;
				case "-o":
				case "--output":
					if (i + 1 < args.length) {
						outputFolder = args[i + 1];
					}
					else {
						System.err.println("Missing argument for " + args[i]);
						printHelp();
					}
					break;
				case "-J":
				case "--Jena":
					useJena = true;
					break;
				case "-w":
				case "--warm":
					warm = true;
					break;
				case "-s":
				case "--shuffle":
					shuffle = true;
					break;
				case "-h":
				case "--help":
					printHelp();
					break;
				default:
					if(args[i].startsWith("-")) {
						System.err.println("Unknown argument " + args[i]);
						printHelp();
					}
					break;
			}
		}
	}

	/**
	 * Entrée du programme
	 */
	public static void main(String[] args) throws Exception {
		// Reading parameters
		processArguments(args);

		//Start timer
		long startTime = System.nanoTime();
		//Read the knowledge base
		System.out.println("Loading the knowledge base...");
		parseData();
		System.out.println("Knowledge base loaded");
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Loading the queries...");
		//List all files in the query folder
		File folder = new File(queryFolder);
		File[] listOfFiles = folder.listFiles();
		//Read the queries
		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().endsWith(".queryset")) {
				parseQueries(queryFolder + File.separator + file.getName());
			}
		}
		System.out.println("Queries loaded");
		System.out.println("Time to parse data: " + duration/1000000 + "ms");
		Model model = parseDataJena();
		parseQueriesJena(null, model);
	}

	// ========================================================================

	/**
	 * Traite chaque requête lue dans {@link #queryFolder} avec {@link #processAQuery(ParsedQuery, FileWriter)}.
	 */
	public static void parseQueries(String queryFile) throws FileNotFoundException, IOException {
		/**
		 * Try-with-resources
		 * 
		 * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
		 */
		/*
		 * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
		 * entièrement dans une collection.
		 */
		FileWriter fileTiming = new FileWriter("resultsTiming.csv");
		fileTiming.append("nom du fichier de donnees  ,  nom du dossier des requêtes  ,  nombre de triplets RDF  ,   nombre de requêtes  ,   temps de lecture des données (ms)  ,  temps de lecture des requêtes (ms)  ,   temps création dico (ms)  ,  nombre d’index  ,  temps de création des index (ms)  ,  temps total d’évaluation du workload (ms)  ,  temps total (du début à la fin du programme) (ms)\n");
		// le nombre d'index et le nombre d'index créés donc 6 ici
		//TODO: Parcourir tous les fichiers de requêtes dans le dossier
		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();
			//create a folder to store the results if it doesn't exist
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				try {
					folder.mkdir();
				} catch (Exception se) {
					System.err.println("Could not create the output folder");
					throw se;
				}
			}
			FileWriter file = new FileWriter(outputFolder + File.separator + "results.csv");
			file.append("DataBase  ,  NameRequest  ,  Result\n");
			while (lineIterator.hasNext())
			/*
			 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par un '}'
			 * On considère alors que c'est la fin d'une requête
			 */
			{
				String line = lineIterator.next();
				queryString.append(line);

				if (line.trim().endsWith("}")) {
					ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);

					processAQuery(query, file); // Traitement de la requête, à adapter/réécrire pour votre programme

					queryString.setLength(0); // Reset le buffer de la requête en chaine vide
				}
			}
			file.close();
		}
		//TODO: Calculer le temps total d'évaluation du workload, et le temps total du programme, et les écrire dans le fichier en ms
	}

	//Use jena to create a model from the data file
	public static Model parseDataJena() throws FileNotFoundException, IOException {
		//Create a model
		Model model = ModelFactory.createDefaultModel();
		//Read the file
		model.read(dataFile);

		//display all the statements in the graph
		/*StmtIterator iter = model.listStatements();
		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();  // get next statement
			Resource subject = stmt.getSubject();     // get the subject
			Property predicate = stmt.getPredicate();   // get the predicate
			RDFNode object = stmt.getObject();      // get the object
			System.out.print(subject.toString());
			System.out.print(" " + predicate.toString() + " ");
			if (object instanceof Resource) {
				System.out.print(object.toString());
			} else {
				// object is a literal
				System.out.print(" \"" + object.toString() + "\"");
			}
			System.out.println(" .");
		}*/

		//display the model in format RDF/XML
		//model.write(System.out);
		return model;
	}

	//use Jena to answer a multiply query in a query folder
	public static void parseQueriesJena(String queryFolder, Model model) throws FileNotFoundException, IOException {
		//TODO: Parcourir tous les fichiers de requêtes dans le dossier
		//TODO: Parcourir chaque requête dans le fichier

		String queryString = "SELECT ?v0 WHERE {\n" +
				"\t?v0 <http://schema.org/eligibleRegion> <http://db.uwaterloo.ca/~galuc/wsdbm/Country137> . }";
		Query query = QueryFactory.create(queryString) ;
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect() ;
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution() ;
				System.out.println(soln.get("v0"));
			}
		}

	}


	/**
	 * Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
	 */
	public static KnowledgeBase parseData() throws FileNotFoundException, IOException {

		try (Reader dataReader = new FileReader(dataFile)) {
			//Creation du HashMap<Integer, String> pour les creer le dictionnaire
			Map<String, Integer> dictionnaire = new HashMap<>();
			Map<Integer, String> dictionnaireReverse = new HashMap<>();

			//Creation des index des la bdd
			Map<Integer, Map<Integer, Set<Integer>>> ospMap = new HashMap<>();
			Map<Integer, Map<Integer, Set<Integer>>> opsMap = new HashMap<>();
			Map<Integer, Map<Integer, Set<Integer>>> posMap = new HashMap<>();
			Map<Integer, Map<Integer, Set<Integer>>> psoMap = new HashMap<>();
			Map<Integer, Map<Integer, Set<Integer>>> sopMap = new HashMap<>();
			Map<Integer, Map<Integer, Set<Integer>>> spoMap = new HashMap<>();

			// On va parser des données au format ntriples
			RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

			// On utilise notre implémentation de handler
			rdfParser.setRDFHandler(new MainRDFHandler(dictionnaire,dictionnaireReverse, ospMap, opsMap, posMap, psoMap, sopMap, spoMap));

			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);

			//System.out.println("Dictionnaire : " + dictionnaire);
			//System.out.println("DictionnaireReverse : " + dictionnaireReverse);
			/*System.out.println("SPO : " + spoMap);
			System.out.println("SOP : " + sopMap);
			System.out.println("OSP : " + ospMap);
			System.out.println("OPS : " + opsMap);
			System.out.println("POS : " + posMap);
			System.out.println("PSO : " + psoMap);*/

			knowledgeBase = new KnowledgeBase(dictionnaire, dictionnaireReverse, ospMap, opsMap, posMap, psoMap, sopMap, spoMap);

			return knowledgeBase;

		}

		//TODO: ANALYSER LES QUERIES EN ETOILE (Dans sample_query.queryset), analyser chaque branche et l'enregistrer dans les structures dépolyées, puis faire la jointure des résultats
		// En utilisant les données de la bdd, enregistrer
		// Pour l'interpretation, faire l'interpretation de chaque branche, puis faire la jointure des résultats
	}
}
