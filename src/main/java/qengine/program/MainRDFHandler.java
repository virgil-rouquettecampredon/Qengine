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

	ArrayList<String> tuples = new ArrayList<String>();
	public MainRDFHandler(ArrayList<String> tuples) {
		this.tuples = tuples;
	}

	@Override
	public void handleStatement(Statement st) {
		//System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t " + st.getObject());

		//System.out.println("Adding triple: " + st.getSubject() + " " + st.getPredicate() + " " + st.getObject());
		//System.out.println(dictionnaire);

		String tuple = new String();
		tuple = st.getSubject().toString() + " " + st.getPredicate().toString() + " " + st.getObject().toString();
		tuples.add(tuple);


	};
}