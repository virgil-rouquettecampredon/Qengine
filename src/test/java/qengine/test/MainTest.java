package qengine.test;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.junit.BeforeClass;
import org.junit.Test;
import qengine.program.KnowledgeBase;
import qengine.program.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static qengine.program.Main.*;

public class MainTest {

    static Map<Integer, Map<Integer, Set<Integer>>> spo;
    static Map<Integer, Map<Integer, Set<Integer>>> pso;
    static Map<Integer, Map<Integer, Set<Integer>>> sop;
    static Map<Integer, Map<Integer, Set<Integer>>> pos;
    static Map<Integer, Map<Integer, Set<Integer>>> ops;
    static Map<Integer, Map<Integer, Set<Integer>>> osp;
    static Map<String, Integer> dico;
    static Map<Integer, String> dicoReverse;
    static ArrayList<ParsedQuery> queries;
    static ArrayList<String> queriesString;
    static File[] listOfFiles;
    static int defaultQueriesSize = 0;


    @BeforeClass
    public static void setUp() throws Exception {
        Main main = new Main();
        String queryFolder = "queries";
        Main.main(new String[]{"-q", queryFolder, "-d", "data/100K.nt", "-w", "10", "-s"});
        KnowledgeBase knowledgeBase = main.parseData();
        //List all files in the query folder
        File folder = new File(queryFolder);
        listOfFiles = folder.listFiles();
        queries = new ArrayList<>();
        queriesString = new ArrayList<>();
        //Read the queries
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".queryset")) {
                queriesString.addAll(parseQueries(queryFolder + File.separator + file.getName()));
            }
        }
        queries = transformQueriesIntoParsedQueries(queriesString);
        defaultQueriesSize = queries.size();

        //Get all indexes from the knowledgeBase
        osp = knowledgeBase.getOsp();
        System.out.println("osp: " + osp.size());
        ops = knowledgeBase.getOps();
        System.out.println("ops: " + ops.size());
        pos = knowledgeBase.getPos();
        System.out.println("pos: " + pos.size());
        pso = knowledgeBase.getPso();
        System.out.println("pso: " + pso.size());
        sop = knowledgeBase.getSop();
        System.out.println("sop: " + sop.size());
        spo = knowledgeBase.getSpo();
        System.out.println("spo: " + spo.size());

        //Get all dictionnaries from the knowledgeBase
        dico = knowledgeBase.getDico();
        dicoReverse = knowledgeBase.getDicoReverse();
    }

    @Test
    public void subjectIsSameFor_OPS_POS() {
        boolean subjectIsSame = true;
        // For each a map ops
        for (Map.Entry<Integer, Map<Integer, Set<Integer>>> entry : ops.entrySet()) {
            Integer object = entry.getKey();
            Map<Integer, Set<Integer>> mapPS = ops.get(object);
            for (Map.Entry<Integer, Set<Integer>> entry2 : mapPS.entrySet()) {
                Integer predicate = entry2.getKey();
                Set<Integer> subjectOPS = mapPS.get(predicate);

                Set<Integer> subjectPOS = pos.get(predicate).get(object);
                if (!subjectOPS.equals(subjectPOS)) {
                    subjectIsSame = false;
                }
            }
        }
        assertTrue(subjectIsSame);
    }

    @Test
    public void objectIsSameFor_PSO_SPO() {
        boolean objectIsSame = true;
        // For each a map ops
        for (Map.Entry<Integer, Map<Integer, Set<Integer>>> entry : pso.entrySet()) {
            Integer predicate = entry.getKey();
            Map<Integer, Set<Integer>> mapSO = pso.get(predicate);
            for (Map.Entry<Integer, Set<Integer>> entry2 : mapSO.entrySet()) {
                Integer subject = entry2.getKey();
                Set<Integer> objectPSO = mapSO.get(subject);

                Set<Integer> objectSPO = spo.get(subject).get(predicate);
                if (!objectPSO.equals(objectSPO)) {
                    objectIsSame = false;
                }
            }
        }
        assertTrue(objectIsSame);
    }

    @Test
    public void predicateIsSameFor_SOP_OSP() {
        boolean predicateIsSame = true;
        // For each a map ops
        for (Map.Entry<Integer, Map<Integer, Set<Integer>>> entry : sop.entrySet()) {
            Integer subject = entry.getKey();
            Map<Integer, Set<Integer>> mapOP = sop.get(subject);
            for (Map.Entry<Integer, Set<Integer>> entry2 : mapOP.entrySet()) {
                Integer object = entry2.getKey();
                Set<Integer> predicateSOP = mapOP.get(object);

                Set<Integer> predicateOSP = osp.get(object).get(subject);
                if (!predicateSOP.equals(predicateOSP)) {
                    predicateIsSame = false;
                }
            }
        }
        assertTrue(predicateIsSame);
    }

    @Test
    public void dicoSameSize(){
        assertEquals(dico.size(), dicoReverse.size());
    }
    @Test
    public void dicoIsSameFor_DicoReverse() {
        boolean isSame = true;
        for (int i = 0; i < dicoReverse.size(); i++) {
            if ((!dico.containsKey(dicoReverse.get(i))) || dico.get(dicoReverse.get(i)) != i) {
                isSame = false;
            }
        }
        assertTrue(isSame);
    }

    @Test
    public void checkSoundnessAndCompleteness() {
        assertTrue(checkSoundAndComplete(queriesString,queries));
    }

    @Test
    public void checkCorrectNumberOfQueries() throws IOException {
        String fileString = "";
        int cpt = 0;
        for(File file : listOfFiles){
            if(file.isFile() && file.getName().endsWith(".queryset")){
                //Count how many times the keyword SELECT appears in file
                fileString = new String(Files.readAllBytes(Paths.get(file.getPath())));
                cpt += StringUtils.countMatches(fileString, "SELECT");
            }
        }
        assertEquals(defaultQueriesSize, cpt);
    }

}
