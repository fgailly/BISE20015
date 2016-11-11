package org.eclipse.bpmn2.modeler.suggestion.algorithm;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ugent.mis.cmoeplus.OntologyManager;

public class CheetahOntologyManager extends OntologyManager {
	public  CheetahOntologyManager(String bundleName, String propertiesFile) 
			throws OWLOntologyCreationException, IOException, URISyntaxException{
		super();
		Bundle bundle = Platform.getBundle(bundleName);
		//Bundle bundle = Platform.getBundle("org.eclipse.bpmn2.modeler.suggestion");
		System.out.println(bundle.getLocation());
		System.out.println(System.getProperty("user.dir"));
		
		owlManager = OWLManager.createOWLOntologyManager();

		CoO = owlManager.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/ufo.owl")).toURI()));
		//UFO = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/ufo.owl"));
		System.out.println("Loaded ontology: " + CoO);
		MLO = owlManager.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/bpmn.owl")).toURI()));
		//BPMN = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/bpmn.owl"));
		System.out.println("Loaded ontology: " + MLO);
		CoO_MLO = owlManager.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/bpmn_ufo.owl")).toURI()));
		//BPMN_UFO = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/bpmn_ufo.owl"));
		System.out.println("Loaded ontology: " + CoO_MLO);
		ESO = owlManager.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/bank2.owl")).toURI()));
		//ESO = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/bank.owl"));
		System.out.println("Loaded ontology: " + ESO);


		System.out.println("Done Loading Ontologies");
	}

}
