package qengine.program;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

import java.util.*;

/**
 * Le RDFHandler intervient lors du parsing de données et permet d'appliquer un traitement pour chaque élément lu par le parseur.
 * 
 * <p>
 * Ce qui servira surtout dans le programme est la méthode {@link #handleStatement(Statement)} qui va permettre de traiter chaque triple lu.
 * </p>
 * <p>
 * À adapter/réécrire selon vos traitements.
 * </p>
 */
public final class MainRDFHandler extends AbstractRDFHandler {

	private Map<String, Integer> dico;
	private Map<Integer ,String> dicoReverse;
	private Map<Integer,Map<Integer, Set<Integer>>> osp;
	private Map<Integer,Map<Integer,Set<Integer>>> pos;
	private Map<Integer,Map<Integer,Set<Integer>>> sop;
	private Map<Integer,Map<Integer,Set<Integer>>> pso;
	private Map<Integer,Map<Integer,Set<Integer>>> ops;
	private Map<Integer,Map<Integer,Set<Integer>>> spo;
	private int compteur;
	public MainRDFHandler(Map<String, Integer> dico, Map<Integer, String> dicoReverse, Map<Integer, Map<Integer, Set<Integer>>> osp, Map<Integer, Map<Integer, Set<Integer>>> ops, Map<Integer, Map<Integer, Set<Integer>>> pos, Map<Integer, Map<Integer, Set<Integer>>> pso, Map<Integer, Map<Integer, Set<Integer>>> sop, Map<Integer, Map<Integer, Set<Integer>>> spo) {
		this.dico = dico;
		this.dicoReverse = dicoReverse;
		this.osp = osp;
		this.ops = ops;
		this.pos = pos;
		this.pso = pso;
		this.sop = sop;
		this.spo = spo;
		this.compteur = 0;
	}

	private void addTriple(Map<Integer, Map<Integer, Set<Integer>>> index, String firstElement, String secondElement, String thirdElement){
		//System.out.println("Adding triple: " + firstElement + " " + secondElement + " " + thirdElement);
		int firstElementId = dico.get(firstElement);
		int secondElementId = dico.get(secondElement);
		int thirdElementId = dico.get(thirdElement);

		if (index.containsKey(firstElementId)) {
			if (index.get(firstElementId).containsKey(secondElementId)){
				index.get(firstElementId).get(secondElementId).add(thirdElementId);
			} else {
				Set<Integer> leaf = new HashSet<>();
				leaf.add(thirdElementId);
				index.get(firstElementId).put(secondElementId, leaf);
			}
		} else {
			Set<Integer> leaf = new HashSet<>();
			leaf.add(thirdElementId);
			Map<Integer, Set<Integer>> map = new HashMap<>();
			map.put(secondElementId, leaf);
			index.put(firstElementId, map);
		}
	}
	@Override
	//TODO: Organiser le dictionnaire dans l'ordre lexicographique
	public void handleStatement(Statement st) {
		//System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t " + st.getObject());
		if (!dico.containsKey(st.getSubject().toString())) {
			dico.put(st.getSubject().toString(), compteur);
			dicoReverse.put(compteur, st.getSubject().toString());
			compteur++;
		}
		if (!dico.containsKey(st.getPredicate().toString())) {
			dico.put(st.getPredicate().toString(), compteur);
			dicoReverse.put(compteur, st.getPredicate().toString());
			compteur++;
		}
		if (!dico.containsKey(st.getObject().toString())) {
			dico.put(st.getObject().toString(), compteur);
			dicoReverse.put(compteur, st.getObject().toString());
			compteur++;
		}

		addTriple(spo, st.getSubject().toString(), st.getPredicate().toString(), st.getObject().toString());
		addTriple(sop, st.getSubject().toString(), st.getObject().toString(), st.getPredicate().toString());
		addTriple(pso, st.getPredicate().toString(), st.getSubject().toString(), st.getObject().toString());
		addTriple(pos, st.getPredicate().toString(), st.getObject().toString(), st.getSubject().toString());
		addTriple(ops, st.getObject().toString(), st.getPredicate().toString(), st.getSubject().toString());
		addTriple(osp, st.getObject().toString(), st.getSubject().toString(), st.getPredicate().toString());


	};
}