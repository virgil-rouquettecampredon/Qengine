package qengine.program;

import java.util.Map;
import java.util.Set;

public class KnowledgeBase {
    private Map<String, Integer> dico;
    private Map<Integer, String> dicoReverse;
    private Map<Integer, Map<Integer, Set<Integer>>> osp;
    private Map<Integer, Map<Integer, Set<Integer>>> ops;
    private Map<Integer, Map<Integer, Set<Integer>>> pos;
    private Map<Integer, Map<Integer, Set<Integer>>> pso;
    private Map<Integer, Map<Integer, Set<Integer>>> sop;
    private Map<Integer, Map<Integer, Set<Integer>>> spo;

    public KnowledgeBase(Map<String, Integer> dico, Map<Integer, String> dicoReverse, Map<Integer, Map<Integer, Set<Integer>>> osp, Map<Integer, Map<Integer, Set<Integer>>> ops, Map<Integer, Map<Integer, Set<Integer>>> pos, Map<Integer, Map<Integer, Set<Integer>>> pso, Map<Integer, Map<Integer, Set<Integer>>> sop, Map<Integer, Map<Integer, Set<Integer>>> spo) {
        this.dico = dico;
        this.dicoReverse = dicoReverse;
        this.osp = osp;
        this.ops = ops;
        this.pos = pos;
        this.pso = pso;
        this.sop = sop;
        this.spo = spo;
    }

    public Map<String, Integer> getDico() {
        return dico;
    }

    public Map<Integer, String> getDicoReverse() {
        return dicoReverse;
    }

    public Map<Integer, Map<Integer, Set<Integer>>> getOsp() {
        return osp;
    }

    public Map<Integer, Map<Integer, Set<Integer>>> getOps() {
        return ops;
    }

    public Map<Integer, Map<Integer, Set<Integer>>> getPos() {
        return pos;
    }

    public Map<Integer, Map<Integer, Set<Integer>>> getPso() {
        return pso;
    }

    public Map<Integer, Map<Integer, Set<Integer>>> getSop() {
        return sop;
    }

    public Map<Integer, Map<Integer, Set<Integer>>> getSpo() {
        return spo;
    }

}
