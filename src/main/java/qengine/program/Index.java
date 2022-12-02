package qengine.program;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Index {

    Map<Integer, Map<Integer, Set<Integer>>> index;

    public Index(Map<Integer, Map<Integer, Set<Integer>>> index) {
        this.index = index;
    }

    public Index() {
        this.index = new HashMap<>();
    }

    public Map<Integer, Map<Integer, Set<Integer>>> getIndex() {
        return index;
    }

    public Set<Integer> getElement(int first, int second){
        return index.get(first).get(second);
    }

    public void addTriple(int firstElement, int secondElement, int thirdElement) {
        //System.out.println("Adding triple: " + firstElement + " " + secondElement + " " + thirdElement);
        if (index.containsKey(firstElement)) {
            if (index.get(firstElement).containsKey(secondElement)){
                index.get(firstElement).get(secondElement).add(thirdElement);
            } else {
                Set<Integer> leaf = new HashSet<>();
                leaf.add(thirdElement);
                index.get(firstElement).put(secondElement, leaf);
            }
        } else {
            Set<Integer> leaf = new HashSet<>();
            leaf.add(thirdElement);
            Map<Integer, Set<Integer>> map = new HashMap<>();
            map.put(secondElement, leaf);
            index.put(firstElement, map);
        }
        //System.out.println("Index: " + index);
    }

    @Override
    public String toString() {
        return "Index{" +
                "index=" + index +
                '}';
    }
}
