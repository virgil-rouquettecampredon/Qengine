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
		System.out.println("Adding triple: " + firstElement + " " + secondElement + " " + thirdElement);
		if (index.containsKey(dico.get(firstElement))){
			System.out.println("first element already in index");
			if (index.get(dico.get(firstElement)).containsKey(dico.get(secondElement))){
				System.out.println("Contenue de l'index: " + index.get(dico.get(firstElement)).containsKey(dico.get(secondElement)));
				index.get(dico.get(firstElement)).get(dico.get(secondElement)).add(dico.get(thirdElement));
			} else {
				Set<Integer> leaf = new HashSet<>();
				leaf.add(dico.get(thirdElement));
				index.get(dico.get(firstElement)).put(dico.get(secondElement), leaf);
			}
		} else {
			Set<Integer> leaf = new HashSet<>();
			leaf.add(dico.get(thirdElement));
			Map<Integer, Set<Integer>> map = new HashMap<>();
			map.put(dico.get(secondElement), leaf);
			index.put(dico.get(firstElement), map);
		}
	}
	@Override
	//TODO: Organiser le dictionnaire dans l'ordre lexicographique
	public void handleStatement(Statement st) {
		//System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t " + st.getObject());
		System.out.println(dico.containsValue(st.getSubject().toString()));
		if (!dico.containsValue(st.getSubject().toString())) {
			dico.put(st.getSubject().toString(), compteur);
			dicoReverse.put(compteur, st.getSubject().toString());
			compteur++;
		}
		if (!dico.containsValue(st.getPredicate().toString())) {
			dico.put(st.getPredicate().toString(), compteur);
			dicoReverse.put(compteur, st.getPredicate().toString());
			compteur++;
		}
		if (!dico.containsValue(st.getObject().toString())) {
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