package qengine.test;

import org.junit.BeforeClass;
import org.junit.Test;
import qengine.program.KnowledgeBase;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static qengine.program.Main.parseData;
import static qengine.program.Main.parseQueries;

public class MainTest {

    static Map<Integer, Map<Integer, Set<Integer>>> spo;
    static Map<Integer, Map<Integer, Set<Integer>>> pso;
    static Map<Integer, Map<Integer, Set<Integer>>> sop;
    static Map<Integer, Map<Integer, Set<Integer>>> pos;
    static Map<Integer, Map<Integer, Set<Integer>>> ops;
    static Map<Integer, Map<Integer, Set<Integer>>> osp;
    static Map<String, Integer> dico;
    static Map<Integer, String> dicoReverse;


    @BeforeClass
    public static void setUp() throws Exception {
        KnowledgeBase knowledgeBase = parseData();
        parseQueries();

        //Get all indexes from the knowledgeBase
        osp = knowledgeBase.getOsp();
        ops = knowledgeBase.getOps();
        pos = knowledgeBase.getPos();
        pso = knowledgeBase.getPso();
        sop = knowledgeBase.getSop();
        spo = knowledgeBase.getSpo();

        //Get all dictionnaries from the knowledgeBase
        dico = knowledgeBase.getDico();
        dicoReverse = knowledgeBase.getDicoReverse();
    }

    @Test
    public void subjectIsSameFor_OPS_POS() {
        assertEquals(ops.get(2).get(1), pos.get(1).get(2));
    }

    @Test
    public void objectIsSameFor_PSO_SPO() {
        assertEquals(pso.get(1).get(0), spo.get(0).get(1));
    }

    @Test
    public void predicateIsSameFor_SOP_OSP() {
        assertEquals(sop.get(0).get(2), osp.get(2).get(0));
    }

    @Test
    public void dicoIsSameFor_DicoReverse() {
        assertEquals(dico.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), dicoReverse.get(1));
    }



}
