package qengine.program;

import org.eclipse.rdf4j.query.algebra.In;

import java.util.HashMap;
import java.util.Map;

public class Dictionnaire {
    private Map<String, Integer> dico;
    private Map<Integer,String> dicoReverse;

    private int size;

    public Dictionnaire(Map<String, Integer> dico, Map<Integer,String> dicoReverse) {
        this.dico = dico;
        this.dicoReverse = dicoReverse;
        this.size = dico.size();
    }

    public Dictionnaire() {
        this.dico = new HashMap<>();
        this.dicoReverse = new HashMap<>();
        this.size = 0;
    }

    public Map<String, Integer> getDico() {
        return dico;
    }

    public Map<Integer, String> getDicoReverse() {
        return dicoReverse;
    }

    public void add(String value) {
        if (!dico.containsKey(value)) {
            dico.put(value, size);
            dicoReverse.put(size, value);
            size++;
        }
    }

    public int getEntry(String value) {
        return dico.get(value);
    }

    public String getEntry(int value) {
        return dicoReverse.get(value);
    }

    @Override
    public String toString() {
        return "Dictionnaire{" +
                "dico=" + dico +
                ", dicoReverse=" + dicoReverse +
                ", size=" + size +
                '}';
    }
}


