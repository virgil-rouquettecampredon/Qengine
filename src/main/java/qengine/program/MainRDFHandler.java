package qengine.program;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.algebra.In;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

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
	private ArrayList<TripleIndex> spo;
	private ArrayList<TripleIndex> sop;
	private ArrayList<TripleIndex> osp;
	private ArrayList<TripleIndex> ops;
	private ArrayList<TripleIndex> pos;
	private ArrayList<TripleIndex> pso;
	private int compteur;

	public MainRDFHandler(Map<String, Integer> dico, ArrayList<TripleIndex> spo, ArrayList<TripleIndex> sop, ArrayList<TripleIndex> osp, ArrayList<TripleIndex> ops, ArrayList<TripleIndex> pos, ArrayList<TripleIndex> pso) {
		super();
		this.dico = dico;
		this.compteur = 0;
		this.spo = spo;
		this.sop = sop;
		this.osp = osp;
		this.ops = ops;
		this.pos = pos;
		this.pso = pso;

	}

	@Override
	//TODO: Organiser le dictionnaire dans l'ordre lexicographique
	public void handleStatement(Statement st) {
		System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t " + st.getObject());
		if (!dico.containsValue(st.getSubject().toString())) {
			dico.put(st.getSubject().toString(), compteur);
			compteur++;
		}
		if (!dico.containsValue(st.getPredicate().toString())) {
			dico.put(st.getPredicate().toString(), compteur);
			compteur++;
		}
		if (!dico.containsValue(st.getObject().toString())) {
			dico.put(st.getObject().toString(), compteur);
			compteur++;
		}

		spo.add(new SPO(dico.get(st.getSubject().toString()), dico.get(st.getPredicate().toString()), dico.get(st.getObject().toString())));
		sop.add(new SOP(dico.get(st.getSubject().toString()), dico.get(st.getObject().toString()), dico.get(st.getPredicate().toString())));
		osp.add(new OSP(dico.get(st.getObject().toString()), dico.get(st.getSubject().toString()), dico.get(st.getPredicate().toString())));
		ops.add(new OPS(dico.get(st.getObject().toString()), dico.get(st.getPredicate().toString()), dico.get(st.getSubject().toString())));
		pos.add(new POS(dico.get(st.getPredicate().toString()), dico.get(st.getObject().toString()), dico.get(st.getSubject().toString())));
		pso.add(new PSO(dico.get(st.getPredicate().toString()), dico.get(st.getSubject().toString()), dico.get(st.getObject().toString())));

		//TODO CREATE 6 INDEXES
	};
}