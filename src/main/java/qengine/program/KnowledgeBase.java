package qengine.program;

import org.eclipse.rdf4j.query.algebra.StatementPattern;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KnowledgeBase {
    private Dictionnaire dictionnaire;
    private Index osp;
    private Index pos;
    private Index sop;
    private Index spo;
    private Index ops;
    private Index pso;

    public KnowledgeBase(Dictionnaire dictionnaire, Index osp, Index pos, Index sop, Index spo, Index ops, Index pso) {
        this.dictionnaire = dictionnaire;
        this.osp = osp;
        this.pos = pos;
        this.sop = sop;
        this.spo = spo;
        this.ops = ops;
        this.pso = pso;
    }

    public Map<String, Integer> getDico() {
        return dictionnaire.getDico();
    }

    public Map<Integer, String> getDicoReverse() {
        return dictionnaire.getDicoReverse();
    }

    public Index getOsp() {
        return osp;
    }

    public Index getPos() {
        return pos;
    }

    public Index getSop() {
        return sop;
    }

    public Index getSpo() {
        return spo;
    }

    public Index getOps() {
        return ops;
    }

    public Index getPso() {
        return pso;
    }

    public Set<Integer> getAnswers(StatementPattern pattern){
        String predicate = pattern.getPredicateVar().getValue().stringValue();
        String object = pattern.getObjectVar().getValue().stringValue();

        //System.out.println("predicate = " + predicate);
        //System.out.println("object = " + object);
        try {
            int predicateId = dictionnaire.getEntry(predicate);
            int objectId = dictionnaire.getEntry(object);
            //System.out.println("predicateId = " + predicateId);
            //System.out.println("objectId = " + objectId);
            Set<Integer> answers = pos.getElement(predicateId, objectId);
            return answers == null? new HashSet<>() : answers;
        } catch (Exception e) {
            return new HashSet<>();
        }
    }
}
