package org.eclipse.bpmn2.modeler.suggestion.algorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.eclipse.jface.preference.IPreferenceStore;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.jena.SDJenaFactory;
import com.hp.hpl.jena.rdf.model.Model;

import ugent.mis.cmoeplus.OntologyManager;

public class BizagiOntologyManager extends OntologyManager {
	public BizagiOntologyManager(IPreferenceStore preferenceStore){
		System.out.println("Loading Ontologies");
		CoO = loadOntologyFromStardogDB(preferenceStore.getString("CORE"));
		System.out.println("Loaded ontology: " + CoO);
		MLO = loadOntologyFromStardogDB("BPMN");
		System.out.println("Loaded ontology: " + MLO);
		CoO_MLO = loadOntologyFromStardogDB("BPMN_UFO");
		System.out.println("Loaded ontology: " + CoO_MLO);
		ESO = loadOntologyFromStardogDB("Bank");
		System.out.println("Loaded ontology: " + ESO);

		System.out.println("Done Loading Ontologies");
	}

	private OWLOntology loadOntologyFromStardogDB(String stardogDB) {
		Connection aConn;
		try {

			aConn = ConnectionConfiguration
					.to(stardogDB)		    // the name of the db to connect 
					.server("http://bizagi.ugent.be:5820/")
					.credentials("admin", "liesbeth1812")// credentials to use while connecting
					.connect();

			Model test = SDJenaFactory.createModel(aConn);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			test.write(out, "RDF/XML");
			//aConn.close();

			return owlManager.loadOntologyFromOntologyDocument(new ByteArrayInputStream(out.toByteArray()));

		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;
		} catch (StardogException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
