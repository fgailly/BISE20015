package org.eclipse.bpmn2.modeler.suggestion.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ugent.mis.cmoeplus.CMOEplusProperties;
import ugent.mis.cmoeplus.OntologyManager;

public class LocalOntologyManager extends OntologyManager {
	public LocalOntologyManager(String platformURL, CMOEplusProperties properties) 
			throws OWLOntologyCreationException, IOException{
		super();
		owlManager = OWLManager.createOWLOntologyManager();
		System.out.println("HELP:" + properties.getCoO_filename());
		URL ufo_url = new URL(platformURL+ "/ontology/" + properties.getCoO_filename());
		InputStream ufoInputStream = ufo_url.openConnection().getInputStream();
		System.out.println(owlManager.toString());
	
		CoO = owlManager.loadOntologyFromOntologyDocument(ufoInputStream);
		//rulesManager.loadOntology(UFO.getOntologyID().getOntologyIRI());
		System.out.println("Loaded ontology: " + CoO);

		URL bpmn_url = new URL(platformURL+ "/ontology/"  + properties.MLO_filename);
		InputStream bpmnInputStream = bpmn_url.openConnection().getInputStream();
		MLO = owlManager.loadOntologyFromOntologyDocument(bpmnInputStream);
		//rulesManager.loadOntology(BPMN.getOntologyID().getOntologyIRI());

		System.out.println("Loaded ontology: " + MLO);


		URL bpmn_ufo_url = new URL(platformURL+ "/ontology/"  + properties.getCoO_MLO_filename());
		InputStream bpmn_ufoInputStream = bpmn_ufo_url.openConnection().getInputStream();
		CoO_MLO = owlManager.loadOntologyFromOntologyDocument(bpmn_ufoInputStream);
		//rulesManager.loadOntology(BPMN_UFO.getOntologyID().getOntologyIRI());

		System.out.println("Loaded ontology: " + CoO_MLO);

		URL eso_url = new URL(platformURL+ "/ontology/"  + properties.getESO_filename());
		InputStream esoInputStream = eso_url.openConnection().getInputStream();
		ESO = owlManager.loadOntologyFromOntologyDocument(esoInputStream);
		System.out.println(ESO.getOntologyID().getOntologyIRI());

		System.out.println("Loaded ontology: " + ESO);

		URL semAnn_url = new URL(platformURL+ "/ontology/" +  properties.getSemAnnO_filename());
		InputStream semAnnInputStream = semAnn_url.openConnection().getInputStream();
		semAnnO = owlManager.loadOntologyFromOntologyDocument(semAnnInputStream);

		System.out.println("Loaded ontology: " + semAnnO);

		URL rules_url = new URL(platformURL+ "/ontology/" + properties.getRulesO_filename());
		InputStream rulesInputStream = rules_url.openConnection().getInputStream();
		rulesO =owlManager.loadOntologyFromOntologyDocument(rulesInputStream);

		System.out.println("Loaded ontology: " + rulesO);

		System.out.println("Done Loading Ontologies");
		
	}

}
