package qengine.program;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.Var;
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
    private static int warmPercentage = 0;
    private static boolean shuffle = false;
    private static boolean exportQueryResults = false;

    static KnowledgeBase knowledgeBase;

    // ========================================================================

    private static void printHelp() {
        System.out.println("Usage: java -jar rdfengine --queries <queryfile> --data <datafile>");
        System.out.println("-q --queries <queryfile>\n\tPath to the file containing the queries");
        System.out.println("-d --data <datafile>\n\tPath to the file containing the knowledge base");
        System.out.println("-o, --output <output_folder>\n\tSpecify the output file with --output <outputfile> (default is output)");
        System.out.println("-J, --Jena\n\tUse Jena to verify the answers, and check completeness");
        System.out.println("-w, --warm <int between 0 and 100>\n\tProcess the first <int> percent of the queries to warm up the cache");
        System.out.println("-s, --shuffle\n\tShuffle the queries before processing them");
        System.out.println("-e, --export-query-results\n\tExport the query results in the output folder");
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
                    } else {
                        System.err.println("Missing argument for " + args[i]);
                        printHelp();
                    }
                    break;
                case "-d":
                case "--data":
                    if (i + 1 < args.length) {
                        dataFile = args[i + 1];
                    } else {
                        System.err.println("Missing argument for " + args[i]);
                        printHelp();
                    }
                    break;
                case "-o":
                case "--output":
                    if (i + 1 < args.length) {
                        outputFolder = args[i + 1];
                    } else {
                        System.err.println("Missing argument for " + args[i]);
                        printHelp();
                    }
                    break;
                case "-J":
                case "--Jena":
                    System.out.println("Jena enabled");
                    useJena = true;
                    break;
                case "-w":
                case "--warm":
                    System.out.println("Warming up enabled");
                    warm = true;
                    if (i + 1 < args.length) {
                        try {
                            warmPercentage = Integer.parseInt(args[i + 1]);
                            if(warmPercentage < 0 || warmPercentage > 100) {
                                System.err.println("Warm percentage must be between 0 and 100");
                                printHelp();
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid argument for " + args[i]);
                            printHelp();
                        }
                    } else {
                        System.err.println("Missing argument for " + args[i]);
                        printHelp();
                    }
                    break;
                case "-s":
                case "--shuffle":
                    System.out.println("Shuffling enabled");
                    shuffle = true;
                    break;
                case "-e":
                case "--export-query-results":
                    System.out.println("Exporting query results enabled");
                    exportQueryResults = true;
                    break;
                case "-h":
                case "--help":
                    printHelp();
                    break;
                default:
                    if (args[i].startsWith("-")) {
                        System.err.println("Unknown argument " + args[i]);
                        printHelp();
                    }
                    break;
            }
        }
    }

    // ========================================================================

    /**
     * Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
     */
    public static ArrayList<String> parseData() throws FileNotFoundException, IOException {

        try (Reader dataReader = new FileReader(dataFile)) {
            //Creation du HashMap<Integer, String> pour les creer le dictionnaire

            ArrayList<String> tuples = new ArrayList<>();
            // On va parser des données au format ntriples
            RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

            // On utilise notre implémentation de handler
            rdfParser.setRDFHandler(new MainRDFHandler(tuples));

            // Parsing et traitement de chaque triple par le handler
            rdfParser.parse(dataReader, baseURI);

            return tuples;

        }
    }

    public static Dictionnaire creatingDictionnary(ArrayList<String> tuples) {
        Dictionnaire dictionnaire = new Dictionnaire();
        for (String tuple : tuples) {
            String[] split = tuple.split(" ");
            dictionnaire.add(split[0]);
            dictionnaire.add(split[1]);
            dictionnaire.add(split[2]);
        }
        return dictionnaire;
    }

    public static KnowledgeBase creatingIndex(ArrayList<String> tuples, Dictionnaire dictionnaire) {
        Index pso = new Index();
        Index pos = new Index();
        Index osp = new Index();
        Index ops = new Index();
        Index sop = new Index();
        Index spo = new Index();

        for (String tuple : tuples) {
            String[] split = tuple.split(" ");
            //System.out.println(split[0] + " " + split[1] + " " + split[2]);
            String subject = split[0];
            String predicate = split[1];
            String object = split[2];

            spo.addTriple(dictionnaire.getEntry(subject), dictionnaire.getEntry(predicate), dictionnaire.getEntry(object));
            sop.addTriple(dictionnaire.getEntry(subject), dictionnaire.getEntry(object), dictionnaire.getEntry(predicate));
            pso.addTriple(dictionnaire.getEntry(predicate), dictionnaire.getEntry(subject), dictionnaire.getEntry(object));
            pos.addTriple(dictionnaire.getEntry(predicate), dictionnaire.getEntry(object), dictionnaire.getEntry(subject));
            osp.addTriple(dictionnaire.getEntry(object), dictionnaire.getEntry(subject), dictionnaire.getEntry(predicate));
            ops.addTriple(dictionnaire.getEntry(object), dictionnaire.getEntry(predicate), dictionnaire.getEntry(subject));

            }
        return new KnowledgeBase(dictionnaire,osp, pos, sop, spo, ops, pso);
    }

    /**
     * Traite chaque requête lue dans {@link #queryFolder} avec {@link #processAQuery(ParsedQuery)}.
     */
    public static ArrayList<String> parseQueries(String queryFile) throws IOException {
        /**
         * Try-with-resources
         *
         * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
         */
        /*
         * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
         * entièrement dans une collection.
         */

        ArrayList<String> queries = new ArrayList<>();

        // le nombre d'index et le nombre d'index créés donc 6 ici
        try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
            Iterator<String> lineIterator = lineStream.iterator();
            StringBuilder queryString = new StringBuilder();
            {
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    queryString.append(line);
                    if (line.trim().endsWith("}")) {
                        queries.add(queryString.toString());
                        queryString.setLength(0); // Reset le buffer de la requête en chaine vide
                    }
                }
            }
        }

        return queries;
    }

    public static ArrayList<ParsedQuery> transformQueriesIntoParsedQueries(ArrayList<String> queries) {
        ArrayList<ParsedQuery> parsedQueries = new ArrayList<>();
        SPARQLParser sparqlParser = new SPARQLParser();
        for (String query : queries) {
            ParsedQuery parsedQuery = sparqlParser.parseQuery(query, baseURI);
            parsedQueries.add(parsedQuery);
        }
        return parsedQueries;
    }

    /**
     * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet obtenu.
     */
    public static Set<String> processAQuery(ParsedQuery query) {
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

        Set<Integer> answers = new HashSet<>();
        boolean firstEmpty = true;
        for (StatementPattern pattern : patterns) {
            Set<Integer> localAnswers = knowledgeBase.getAnswers(pattern);
            if (localAnswers.isEmpty()) {
                answers = new HashSet<>();
                break;
            }
            else if (firstEmpty) {
                answers.addAll(localAnswers);
                firstEmpty = false;
            } else {
                answers.retainAll(localAnswers);
            }

            if (answers.isEmpty()) {
                break;
            }
        }
        Set<String> answersString = new HashSet<>();
        if (answers.isEmpty()) {
            answersString.add("No answer");
            return answersString;
        } else {
            for (Integer answer : answers) {
                answersString.add(knowledgeBase.getDicoReverse().get(answer));
            }
            return answersString;
        }

    }

    // ========================================================================

    //function that parse a file to call function parseQueriesJena for each query
    //Use jena to create a model from the data file
    public static Model parseDataJena() {
        //Create a model
        Model model = ModelFactory.createDefaultModel();
        //Read the file
        model.read(dataFile);
        return model;
    }

    //use Jena to answer a multiply query in a query folder
    public static Set<String> processAQueryJena(String query, Model model) {
        Set<String> answers = new HashSet<>();

        Query queryJena = QueryFactory.create(query);
        try (QueryExecution qexec = QueryExecutionFactory.create(queryJena, model)) {
            ResultSet results = qexec.execSelect();
            if (!results.hasNext()) {
                answers.add("No answer");
            } else {
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    answers.add(soln.get("v0").toString());
                }
            }
        }
        return answers;
    }

    public static ArrayList<Set<String>> processQueriesJena(ArrayList<String> queries, Model model) {
        ArrayList<Set<String>> answers = new ArrayList<>();
        for (String query : queries) {
            answers.add(processAQueryJena(query, model));
        }
        return answers;
    }

    public static boolean checkSoundAndComplete(ArrayList<String> queries, ArrayList<ParsedQuery> parsedQueries) {
        Model model = parseDataJena();
        Set<String> resultSelf = null;
        Set<String> resultJena = null;
        String request = "";

        for (int i = 0; i < queries.size(); i++) {
            resultSelf = processAQuery(parsedQueries.get(i));
            resultJena = processAQueryJena(queries.get(i), model);
            if (!resultSelf.equals(resultJena)) {
                System.out.println("The query " + queries.get(i) + " is not sound and complete " + i);
                //System.out.println("result of self: " + resultSelf);
                //System.out.println("result of jena: " + resultJena);
                return false;
            }
        }
        return true;
    }

    // ========================================================================

    //function that write into a File the query if the query have an answer
    public static void writeQuery(String query, String queryFile) {
        try {
            File fOutput = new File("QueryModifie");
            if (!fOutput.exists()) {
                try {
                    fOutput.mkdir();
                } catch (Exception se) {
                    System.err.println("Could not create the output folder");
                    throw se;
                }
            }
            FileWriter fw = new FileWriter("QueryModifie" + File.separator + queryFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(query);
            bw.newLine();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function that count the number of queryset of type ParsedQuery
    public static ArrayList<Integer> countQuerySet(ArrayList<ParsedQuery> parsedQueries) {
        ArrayList<Integer> nbConnection = new ArrayList<>();
        for (ParsedQuery query : parsedQueries) {
            List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
            while (nbConnection.size() < patterns.size()) {
                nbConnection.add(0);
            }
            Integer count = nbConnection.get(patterns.size()-1) ;
            count = count + 1;
            nbConnection.set(patterns.size()-1, count);
        }
        return nbConnection;
    }




    // ========================================================================

    public static void countTriplets(Map<Integer, Map<Integer, Set<Integer>>> index, Benchmark benchmark) {
        int nbTriplets = 0;
        for (Map<Integer, Set<Integer>> map : index.values()) {
            for (Set<Integer> set : map.values()) {
                nbTriplets += set.size();
            }
        }
        benchmark.setNbTriplets(nbTriplets);
    }

    // ========================================================================

    /**
     * Entrée du programme
     */
    public static void main(String[] args) throws Exception {
        long startTimeTotal = System.currentTimeMillis();

        // Reading parameters
        processArguments(args);

        //Create the output folder
        File fOutput = new File(outputFolder);
        if (!fOutput.exists()) {
            try {
                fOutput.mkdir();
            } catch (Exception se) {
                System.err.println("Could not create the output folder");
                throw se;
            }
        }

        Benchmark benchmark = new Benchmark();
        benchmark.setNameDataFile(dataFile);
        benchmark.setNameQueryFolder(queryFolder);


        //Read the knowledge base
        System.out.println("Loading the knowledge base...");
        long startTime = System.currentTimeMillis();
        ArrayList<String> tuples = parseData();
        long endTime = System.currentTimeMillis();
        System.out.println("Knowledge base loaded");
        benchmark.setTimeReadingData(endTime - startTime);
        //TODO : Faut-il réellement découpler index et dico ?
        //TODO : Qu'est-ce que c'est timeReadingData ?
        long startTimeDictionnaire = System.currentTimeMillis();
        Dictionnaire dictionnaire = creatingDictionnary(tuples);
        long endTimeDictionnaire = System.currentTimeMillis();
        benchmark.setTimeCreatingDico(endTimeDictionnaire - startTimeDictionnaire);
        System.out.println("Dictionnaire created");
        long startTimeIndex = System.currentTimeMillis();
        knowledgeBase = creatingIndex(tuples, dictionnaire);
        long endTimeIndex = System.currentTimeMillis();
        benchmark.setTimeCreatingIndex(endTimeIndex - startTimeIndex);
        benchmark.setNbIndex(6);

        System.out.println("Loading time : " + (endTime - startTime) + " ms");
        countTriplets(knowledgeBase.getPos().getIndex(), benchmark);
        System.out.println("Loading the queries...");

        //List all files in the query folder
        File folder = new File(queryFolder);
        File[] listOfFiles = folder.listFiles();
        ArrayList<ParsedQuery> queries = new ArrayList<>();
        ArrayList<String> queriesString = new ArrayList<>();

        //Read the queries
        startTime = System.currentTimeMillis();
        //ArrayList<String> queriesName = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".queryset")) {
                queriesString.addAll(parseQueries(queryFolder + File.separator + file.getName()));
                //queriesName.add(file.getName());
            }
        }

        // Count duplicate queries
        Set<String> uniqueQueries = new HashSet<>(queriesString);
        int nbDuplicateQueries = queriesString.size() - uniqueQueries.size();
        benchmark.setNbDuplicateQueries(nbDuplicateQueries);

        endTime = System.currentTimeMillis();
        benchmark.setTimeReadingQueries(endTime - startTime);
        System.out.println("Queries loaded");
        System.out.println("Loading time : " + (endTime - startTime) + " ms");
        System.out.println("Number of queries : " + queriesString.size());

        if(shuffle) {
            Collections.shuffle(queriesString);
        }

        startTime = System.currentTimeMillis();
        queries = transformQueriesIntoParsedQueries(queriesString);
        endTime = System.currentTimeMillis();
        benchmark.setTransformationQueriesTime(endTime - startTime);

        ArrayList<Integer> nbBranchements = countQuerySet(queries);
        benchmark.setNbConnections(nbBranchements);

        if(warm) {
            System.out.println("Warming up...");
            startTime = System.currentTimeMillis();
            //Try the first percentage warmPercentage of query
            int nbQueries = queries.size();
            Random rand = new Random(7);
            for (int i = 0; i < nbQueries * warmPercentage / 100; i++) {
                // generate a random number with a seed
                int randomNum = rand.nextInt(queries.size());
                processAQuery(queries.remove(randomNum));
                queriesString.remove(randomNum);
            }
            endTime = System.currentTimeMillis();
            System.out.println("Warming up done");
            System.out.println("Warming up time : " + (endTime - startTime) + " ms");
        }

        benchmark.setNbQueries(queries.size());

        ArrayList<Set<String>> result = new ArrayList<>();
        startTime = System.currentTimeMillis();
        //Process the queries
        for (ParsedQuery query : queries) {
            //test the number of connection of a request
            Set<String> resultQuery = processAQuery(query);
            result.add(resultQuery);
        }
        endTime = System.currentTimeMillis();
        benchmark.setTimeWorkload(endTime - startTime);
        if(exportQueryResults) {
            startTime = System.currentTimeMillis();
            FileWriter outputFile = new FileWriter(outputFolder + File.separator + startTimeTotal + "_" + queryFolder + "_results.csv");
            outputFile.append("DataBase  ,  NameRequest  ,  NbBranches  ,  Result  ,  NbResults\n");
            for (int i = 0; i < queriesString.size(); i++) {
                int nbAnswer = result.get(i).stream().findFirst().get().equals("No answer")?0:result.get(i).size();
                outputFile.append(dataFile + "  ,  " + queriesString.get(i) + "  ,  " + StatementPatternCollector.process(queries.get(i).getTupleExpr()).size() + "  ,  " + result.get(i) + "  ,  " + nbAnswer +  "\n");
            }
            outputFile.close();
            endTime = System.currentTimeMillis();
            benchmark.setTimeWritingResults(endTime - startTime);
        }

        if (useJena) {
            System.out.println("Verifying the answers...");
            startTime = System.currentTimeMillis();
            System.out.println(checkSoundAndComplete(queriesString, queries)?"The answers are sound and complete":"The answers are incorrect");
            endTime = System.currentTimeMillis();
            System.out.println("Verification time : " + (endTime - startTime) + " ms");
        }



        long endTimeTotal = System.currentTimeMillis();
        benchmark.setTimeTotal(endTimeTotal - startTimeTotal);

        System.out.println("Evaluate time for Jena");

        long startTimeJena = System.currentTimeMillis();
        Model model = parseDataJena();
        long endTimeJena = System.currentTimeMillis();
        benchmark.setTimeParseDataJena(endTimeJena - startTimeJena);

        long startTimeJena2 = System.currentTimeMillis();
        ArrayList<Set<String>> resultJena = new ArrayList<>();
        resultJena = processQueriesJena(queriesString, model);
        long endTimeJena2 = System.currentTimeMillis();
        benchmark.setTimeEvaluateQueriesJena(endTimeJena2 - startTimeJena2);

        FileWriter outputFile = new FileWriter(outputFolder + File.separator + startTimeTotal + "_stats.csv");
        String nbBranchesString = "";
        for (int i = 0; i < nbBranchements.size(); i++) {
            nbBranchesString += "  ,  " + "branchement de taille " + (i+1);
        }
        outputFile.append("nom du fichier de donnees  ,  nom du dossier des requêtes  ,  nombre de triplets RDF  ,   nombre de requêtes  ,   temps de lecture des données (ms)  ,  temps de lecture des requêtes (ms)  ,  temps de transformation des requêtes (ms)  ,  temps création dico (ms)  ,  nombre d’index  ,  temps de création des index (ms)  ,  temps total d’évaluation du workload (ms)  ,  temps total d'écriture des résultats (ms)  ,  temps total (du début à la fin du programme) (ms)  ,  queries dupliquées "+ nbBranchesString + "  ,  Temps parsing data (ms)  ,  Temps évaluation requetes\n");
        outputFile.write(benchmark.toString());
        outputFile.close();
    }
}
