package org.eclipse.bpmn2.modeler.suggestion.algorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.bpmn2.modeler.suggestion.Activator;
import org.eclipse.bpmn2.modeler.ui.Bpmn2DiagramEditorInput;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.jena.SDJenaFactory;
import com.hp.hpl.jena.rdf.model.Model;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;


public class BPMNSuggestionEngine {
	
	OWLOntology UFO;
	OWLOntology BPMN;
    OWLOntology BPMN_UFO;
    OWLOntology ESO;
    OWLOntology ESO_UFO;
    OWLOntologyManager m;
    OWLOntology merged;
    OWLOntology modelOntology;
    OWLOntology rules;
    
    File wordnet;
    URL wordnet_url;
    String modelName;
    LexicalizedParser lp;
    final String task = "http://www.mis.ugent.be/ontologies/bpmn.owl#Task";
    final String activity = "http://www.mis.ugent.be/ontologies/bpmn.owl#Activity";
    final String event = "http://www.mis.ugent.be/ontologies/bpmn.owl#Event";
    final String pool = "http://www.mis.ugent.be/ontologies/bpmn.owl#Pool";
    final String message = "http://www.mis.ugent.be/ontologies/bpmn.owl#Message";
    final String gateway = "http://www.mis.ugent.be/ontologies/bpmn.owl#Gateway";
    final String qualityUniversal = "http://www.mis.ugent.be/ontologies/ufo.owl#Quality_Universal";
    
    Map<IRI,Suggestion> sugList;
	
	//Set parameters for types of matching
	boolean StringMatching = true;					//String-Matching-Mechanism
	boolean SynonymMatching = true;					//Synonym-Matching-Mechanism
	boolean ConstructMatching = true;				//Construct-Matching-Mechanism
	boolean NeighborhoodBasedMatching = true;		//Neighborhood-Matching-Mechanism
	
	//Set parameters for jaro-winkler distance:
	double JWDweightThreshold=0.4;
	int JWDnumChars=4;
		
	//Set weights and scores for matching mechanisms
	double weightStringMatching = 1.0;				//String-matching mechanism weight
	
	
	//Jaro-winkler distance							//String-matching mechanism score
	double weightConstructMatching = 1.0;				//Construct-matching mechanism weight
	double scoreConstructMatching = 1.0;				//Construct-matching mechanism score
		
	double weightLocationMatching = 1.0;				//Neighborhood-matching mechanism weight - overal (if needed)
	double weightLocationMechanism = 1;
	double scoreLocationMatching = 1.0;				//Neighborhood-matching mechanism score
	double wordnetMatching = 1.0;						//Synonym-matching mechanism score			
	double weightwordnetMatching = 1.0;				//Synonym-matching mechanism weight
		
	
	public Boolean automaticAnnotation=true;
		
	//Make a new ontology
	//True  =Create new model.owl file
	//False =Load from model.owl file
	boolean makeNewOntology=true;
		
	//Make an automatic annotation in the model ontology when a suggestion is clicked
	public boolean AnnotateWhenSuggestionClicked=true;
		
	//Generate also synonym-based suggestions when AnnotationWindow is opened
	public boolean AnnotateSynonymBasedGenerationSuggestions=true;
		
	//The indication-string for candidate-annotations from the feedback ontology
	public String canAnnQua="*";
		
	//Show candidate annotation possibility
	public boolean CandidateAnnotationPossiblity=true;
	
	//Source ontology files
	public String source = "plug-in";
		
	//Filename core ontology
	public String CoreOntology="";
		
	//Filename istar ontology
	public String BPMNOntology="";
			
	//Filename Enterprise-specific Ontology
	public String ESOntology ="";
		
	//Filename mapping core ontolgy and eso
	public String CoreESOOntology="";
		
	//Filename mapping core ontology and BPMN
	public String CoreBPMNOntology="";
	
	//Filename rulesfile
	public String RulesOntology="rules3.owl";
		
	//Filename model ontology
	public String ModelOntology="";
  
	
	public BPMNSuggestionEngine(){
		m = OWLManager.createOWLOntologyManager();
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		
		modelName ="model";
		
		try {
			
			if(source.equals("cheetah")) {
				Bundle bundle = Platform.getBundle("org.cheetahplatform.modeler");
				//Bundle bundle = Platform.getBundle("org.eclipse.bpmn2.modeler.suggestion");
				System.out.println(bundle.getLocation());
				System.out.println(System.getProperty("user.dir"));
				
				//wordnet = new File("resource/ugent/ontology/dict");
				wordnet = new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/dict")).toURI());
				
				
				UFO = m.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/ufo.owl")).toURI()));
				//UFO = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/ufo.owl"));
				System.out.println("Loaded ontology: " + UFO);
				BPMN = m.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/bpmn.owl")).toURI()));
				//BPMN = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/bpmn.owl"));
				System.out.println("Loaded ontology: " + BPMN);
				BPMN_UFO = m.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/bpmn_ufo.owl")).toURI()));
				//BPMN_UFO = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/bpmn_ufo.owl"));
				System.out.println("Loaded ontology: " + BPMN_UFO);
				ESO = m.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/bank.owl")).toURI()));
				//ESO = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/bank.owl"));
				System.out.println("Loaded ontology: " + ESO);
				ESO_UFO = m.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/eso_ufo.owl")).toURI()));
				//ESO_UFO = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/eso_ufo.owl"));
				System.out.println("Loaded ontology: " + ESO_UFO);
				
				ESO_UFO = m.loadOntologyFromOntologyDocument(new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/eso_ufo.owl")).toURI()));
				//ESO_UFO = m.loadOntologyFromOntologyDocument(new File("resource/ugent/ontology/eso_ufo.owl"));
				System.out.println("Loaded ontology: " + ESO_UFO);
				
				System.out.println("Done Loading Ontologies");
			}
			else if(source.equals("bizagi")){
				
				Bundle bundle = Platform.getBundle("org.eclipse.bpmn2.modeler.suggestion");
				
				System.out.println("BUNDLE: " + bundle.getLocation());
				System.out.println("USER.DIR: "+ System.getProperty("user.dir"));
				
				wordnet = new File("resource/ugent/ontology/dict");
				
				//wordnet = new File(FileLocator.resolve(bundle.getEntry("resource/ugent/ontology/dict")).toURI());
				
				
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				System.out.println("Loading Ontologies");
				UFO = loadOntologyFromStardogDB(preferenceStore.getString("CORE"));
				System.out.println("Loaded ontology: " + UFO);
				BPMN = loadOntologyFromStardogDB("BPMN");
				System.out.println("Loaded ontology: " + BPMN);
				BPMN_UFO = loadOntologyFromStardogDB("BPMN_UFO");
				System.out.println("Loaded ontology: " + BPMN_UFO);
				ESO = loadOntologyFromStardogDB("Bank");
				System.out.println("Loaded ontology: " + ESO);
				ESO_UFO = loadOntologyFromStardogDB("Bank_UFO");
				System.out.println("Loaded ontology: " + ESO_UFO);
				
				System.out.println("Done Loading Ontologies");
			
				
			}
			
			else if(source.equals("plug-in")){
				
				try {
					
					URL propertiesURL = new URL("platform:/plugin/org.eclipse.bpmn2.modeler.suggestion/suggestionalgorithm.properties");
					FileInputStream fileInput = (FileInputStream) propertiesURL.openConnection().getInputStream();
					ReadPropertiesFile(fileInput);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				URL wordnet_url = new URL("platform:/plugin/org.eclipse.bpmn2.modeler.suggestion/dict");
				wordnet = new File(FileLocator.resolve(wordnet_url).toURI());
				
				URL ufo_url = new URL("platform:/plugin/org.eclipse.bpmn2.modeler.suggestion/ontology/" + CoreOntology);
				InputStream ufoInputStream = ufo_url.openConnection().getInputStream();
			    UFO = m.loadOntologyFromOntologyDocument(ufoInputStream);
				System.out.println("Loaded ontology: " + UFO);
				
				URL bpmn_url = new URL("platform:/plugin/org.eclipse.bpmn2.modeler.suggestion/ontology/" + BPMNOntology);
				InputStream bpmnInputStream = bpmn_url.openConnection().getInputStream();
				BPMN = m.loadOntologyFromOntologyDocument(bpmnInputStream);
				System.out.println("Loaded ontology: " + BPMN);
				
				
				URL bpmn_ufo_url = new URL("platform:/plugin/org.eclipse.bpmn2.modeler.suggestion/ontology/" + CoreBPMNOntology);
				InputStream bpmn_ufoInputStream = bpmn_ufo_url.openConnection().getInputStream();
				BPMN_UFO = m.loadOntologyFromOntologyDocument(bpmn_ufoInputStream);
				System.out.println("Loaded ontology: " + BPMN_UFO);
				
				URL eso_url = new URL("platform:/plugin/org.eclipse.bpmn2.modeler.suggestion/ontology/" + ESOntology);
				InputStream esoInputStream = eso_url.openConnection().getInputStream();
				ESO = m.loadOntologyFromOntologyDocument(esoInputStream);
				System.out.println("Loaded ontology: " + ESO);
				
				URL eso_ufo_url = new URL("platform:/plugin/org.eclipse.bpmn2.modeler.suggestion/ontology/" + CoreESOOntology);
				InputStream eso_ufoInputStream = eso_ufo_url.openConnection().getInputStream();
				ESO_UFO = m.loadOntologyFromOntologyDocument(eso_ufoInputStream);
				System.out.println("Loaded ontology: " + ESO_UFO);
				
				URL rules_url = new URL("platform:/plugin/org.eclipse.bpmn2.modeler.suggestion/ontology/" + RulesOntology);
				InputStream rulesInputStream = rules_url.openConnection().getInputStream();
				rules = m.loadOntologyFromOntologyDocument(rulesInputStream);
				System.out.println("Loaded ontology: " + rules);
				listSWRLRules(rules);
				
				System.out.println("Done Loading Ontologies");
			
				
			}
			
			else if(source.equals("local")){
				
				try {
					File file = new File("suggestionalgorithm.properties");
					FileInputStream fileInput = new FileInputStream(file);
					ReadPropertiesFile(fileInput);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				System.out.println(System.getProperty("user.dir"));
				
				wordnet = new File("dict");
				
				UFO = m.loadOntologyFromOntologyDocument(new File("ontology/" + CoreOntology));
				System.out.println("Loaded ontology: " + UFO);
				BPMN = m.loadOntologyFromOntologyDocument(new File("ontology/" + BPMNOntology));
				System.out.println("Loaded ontology: " + BPMN);
				BPMN_UFO = m.loadOntologyFromOntologyDocument(new File("ontology/" + CoreBPMNOntology ));
				System.out.println("Loaded ontology: " + BPMN_UFO);
				ESO = m.loadOntologyFromOntologyDocument(new File("ontology/" + ESOntology));
				System.out.println("Loaded ontology: " + ESO);
				ESO_UFO = m.loadOntologyFromOntologyDocument(new File("ontology/" + CoreESOOntology));
				System.out.println("Loaded ontology: " + ESO_UFO);
				
				System.out.println("Done Loading Ontologies");
				
			}
			else {
				System.out.println("Ontologies could not be loaded");
			}

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
			
		
		IRI ontologyIRI = IRI.create("http://www.mis.ugent.be/ontologies/"+ modelName);
		try {
			modelOntology = m.createOntology(ontologyIRI);
			OWLDataFactory fac = m.getOWLDataFactory();
			OWLImportsDeclaration importDeclaraton =
				   fac.getOWLImportsDeclaration(BPMN.getOntologyID().getOntologyIRI()); 
			m.applyChange(new AddImport(modelOntology, importDeclaraton));
		} catch (OWLOntologyCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Created ontology: " + modelOntology);
       
		
		
		OWLOntologyMerger merger = new OWLOntologyMerger(m);
		// We merge all of the loaded ontologies. Since an OWLOntologyManager is
		// an OWLOntologySetProvider we just pass this in. We also need to
		// specify the URI of the new ontology that will be created.
		IRI mergedOntologyIRI = IRI.create("http://www.semanticweb.com/mymerge");
        
		try {
			merged = merger.createMergedOntology(m, mergedOntologyIRI);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
			sugList = new HashMap<IRI, Suggestion>();
			Set<OWLClass> clses = ESO.getClassesInSignature();
			printOWLClasses(clses, "ESO OBjects");
			OWLDataFactory fac = m.getOWLDataFactory();
        
			for (OWLClass cls : clses) {
	    	OWLAnnotationProperty description = fac.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
	    	String descriptionValue = "";
	    	Suggestion.Type type = Suggestion.Type.Class;
	    	for (OWLAnnotation annotation : cls.getAnnotations(ESO, description)) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    descriptionValue = val.getLiteral();
                    }
	    	}
	    	
	    
	    	OWLReasoner reasoner = getReasoner(ESO_UFO);
	    	for(OWLClass superCls :reasoner.getSuperClasses(cls, false).getFlattened()){
	    		if(superCls.getIRI().toString().equals(qualityUniversal))
	    		{
	    			type = Suggestion.Type.Datatype;
	    		}
	    	}
	    	Suggestion sug = new Suggestion(cls.getIRI(), type, cls.getIRI().getFragment(),descriptionValue, "");
	        sugList.put(cls.getIRI(), sug);
			}
			
			Set<OWLNamedIndividual> individuals = ESO.getIndividualsInSignature();
	    	for (OWLNamedIndividual ind : individuals) {
		    	OWLAnnotationProperty description = fac.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		    	String descriptionValue = "";
		    	Suggestion.Type type = Suggestion.Type.Class;
		    	for (OWLAnnotation annotation : ind.getAnnotations(ESO, description)) {
	                if (annotation.getValue() instanceof OWLLiteral) {
	                    OWLLiteral val = (OWLLiteral) annotation.getValue();
	                    descriptionValue = val.getLiteral();
	                    }
	            }
		    	Suggestion sug = new Suggestion(ind.getIRI(), type, ind.getIRI().getFragment(),descriptionValue, "");
		        sugList.put(ind.getIRI(), sug);
	    	}
			
			// add owl dataproperties to suggestionlist
	    	Set<OWLDataProperty> dataProperties = ESO.getDataPropertiesInSignature();
	    	printOWLDataProperties(dataProperties, "ESO DataProperties");
	    	for (OWLDataProperty prop : dataProperties) {
	    		OWLAnnotationProperty description = fac.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
	    		String descriptionValue = "";
	    		String domainString = "";
	    		for (OWLAnnotation annotation : prop.getAnnotations(ESO, description)) {
	    			if (annotation.getValue() instanceof OWLLiteral) {
                    OWLLiteral val = (OWLLiteral) annotation.getValue();
                    descriptionValue = val.getLiteral();
                    }
            }
	    	for (OWLClassExpression domainClass : prop.getDomains(ESO)) {
                if (domainClass instanceof OWLClass) {
                    domainString = ((OWLClass)domainClass).getIRI().getFragment() + ", ";
                    }
            }
	    	
			Suggestion sug = new Suggestion(prop.getIRI(), Suggestion.Type.Datatype, prop.getIRI().getFragment(), descriptionValue, domainString);
	        sugList.put(prop.getIRI(), sug);
	    }
        
		
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
			aConn.close();
	    
			return m.loadOntologyFromOntologyDocument(new ByteArrayInputStream(out.toByteArray()));
		
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

	public OWLReasoner getReasoner(OWLOntology ontology){
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		
		OWLReasonerConfiguration config = new SimpleConfiguration();
        return reasonerFactory.createReasoner(ontology, config);
		
	}
	
	public void testConsistency(OWLOntology ontology){
		
        // Ask the reasoner to do all the necessary work now
        OWLReasoner reasoner = getReasoner(ontology);
        reasoner.precomputeInferences();
        // We can determine if the ontology is actually consistent (in this
        // case, it should be).
        boolean consistent = reasoner.isConsistent();
        System.out.println("Consistent: " + consistent);
        }
	
	//Construct Matching Mechanism Class
	
	public Set<OWLClass> constructmatchingMechanismClass(String irimodellingConstruct){
		OWLDataFactory fac = m.getOWLDataFactory();
	    OWLClass owlClass = fac.getOWLClass(IRI
	                .create(irimodellingConstruct));
	    //OWLReasoner reasoner = getReasoner(merged);
	    org.semanticweb.HermiT.Reasoner reasoner = new Reasoner(merged);
	    NodeSet<OWLClass> subClses = reasoner.getSubClasses(owlClass, false);
	    
	    return subClses.getFlattened();
	}
	
	public Set<OWLNamedIndividual> constructmatchingMechanismClass2(String irimodellingConstruct){
		OWLDataFactory fac = m.getOWLDataFactory();
	    OWLClass owlClass = fac.getOWLClass(IRI
	                .create(irimodellingConstruct));
	    //OWLReasoner reasoner = getReasoner(merged);
	    org.semanticweb.HermiT.Reasoner reasoner = new Reasoner(merged);
	    NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(owlClass, false);
	    
	    return individuals.getFlattened();
	}
	
	//Construct Matching Mechanism DataType
	
	public Set<OWLDataProperty> constructmatchingMechanismDataType(String irimodellingConstruct){
		OWLDataFactory fac = m.getOWLDataFactory();
	    OWLDataProperty owlDataProperty = fac.getOWLDataProperty(IRI
	                .create(irimodellingConstruct));
	    //OWLReasoner reasoner = getReasoner(merged);
	    org.semanticweb.HermiT.Reasoner reasoner = new Reasoner(merged);
	    NodeSet<OWLDataProperty> subProps = reasoner.getSubDataProperties(owlDataProperty, false);
	    
	    return subProps.getFlattened();
	}

	
	public static void listSWRLRules(OWLOntology ontology) { 
        OWLObjectRenderer renderer = new DLSyntaxObjectRenderer(); 
        for (SWRLRule rule : ontology.getAxioms(AxiomType.SWRL_RULE)) { 
            System.out.println(renderer.render(rule)); 
        } 
    }
	
	//Neighbourghood base mechanism
	
	public Set<OWLClass> ruleBasedMechanism(String iriPool){
		return null;
	}
	
	public Set<OWLClass> locationMechanismPool(){
		Set<OWLClass> clses = new HashSet<OWLClass>();
		OWLDataFactory fac = m.getOWLDataFactory();
	    OWLClass poolClass = fac.getOWLClass(IRI
	                .create(pool));
	    
	    OWLReasoner reasonerModel = getReasoner(modelOntology);
	    NodeSet<OWLNamedIndividual> individualsNodeSet = reasonerModel.getInstances(poolClass, true);
	    //System.out.println(individualsNodeSet.getFlattened().size());
	    OWLAnnotationProperty label = fac.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI());
	    org.semanticweb.HermiT.Reasoner reasonerUFO_EO = new Reasoner(ESO_UFO);
	    for(OWLNamedIndividual ind : individualsNodeSet.getFlattened())
	    {
	    	for (OWLAnnotation annotation : ind.getAnnotations(modelOntology, label)) 
	    	{
	    		OWLLiteral val = (OWLLiteral) annotation.getValue();
	    		//System.out.println(val.getLiteral());
	    		OWLClass owlClass = fac.getOWLClass(IRI.create(val.getLiteral()));
	    		OWLObjectProperty mediates = fac.getOWLObjectProperty(
	    		IRI.create(UFO.getOntologyID().getOntologyIRI().toString(), "#mediates"));
	    	    
	    	    OWLClassExpression mediatesExp = fac.getOWLObjectSomeValuesFrom(mediates,
	    	                owlClass);
	    	    Set<OWLClass> relators = reasonerUFO_EO.getSubClasses(mediatesExp, false).getFlattened();
				for(OWLClass relator: relators){
					OWLObjectProperty isMediatedBy = fac.getOWLObjectProperty(IRI.create(UFO.getOntologyID().getOntologyIRI().toString(), "#isMediatedBy"));
					OWLClassExpression isMediatedByExp = fac.getOWLObjectSomeValuesFrom(isMediatedBy,
				    	                relator);
					Set<OWLClass> objecttypes = reasonerUFO_EO.getSubClasses(isMediatedByExp, false).getFlattened();
					clses.addAll(objecttypes);
				}
	    	}
	    }	
	  
	        return clses;
	}
	
	private Set<OWLClass> constructLocationgMechanismTaskEvent(String iriPool) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Set<OWLClass> locationgMechanismMessage(String iriSource, String iriTarget){
		Set<OWLClass> clses = new HashSet<OWLClass>();
		OWLDataFactory fac = m.getOWLDataFactory();
	    OWLAnnotationProperty label = fac.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI());
	    org.semanticweb.HermiT.Reasoner reasonerUFO_EO = new Reasoner(ESO_UFO);
	    for(OWLNamedIndividual ind :  modelOntology.getIndividualsInSignature()){
	    	if(ind.getIRI().equals(iriSource) || ind.getIRI().equals(iriTarget)){
	    		for (OWLAnnotation annotation : ind.getAnnotations(modelOntology, label)) 
		    	{
		    		OWLLiteral val = (OWLLiteral) annotation.getValue();
		    		OWLClass owlClass = fac.getOWLClass(IRI.create(val.getLiteral()));
		    		OWLObjectProperty isMediatedBy = fac.getOWLObjectProperty(
		    		IRI.create(UFO.getOntologyID().getOntologyIRI().toString(), "#isMediatedBy"));
		    		OWLClassExpression isMediatedByExp = fac.getOWLObjectSomeValuesFrom(isMediatedBy,
	    	                owlClass);
		    		Set<OWLClass> relators = reasonerUFO_EO.getSubClasses(isMediatedByExp, false).getFlattened();
		    		clses.addAll(relators);
		    	}
		    		
	    	}
	    	
	    }
	        return clses;
	}
	
	
	public Set<OWLClass>  getWordNetSynClass(String label){
		System.setProperty("wordnet.database.dir", wordnet.getAbsolutePath());
		String[] wordForms = null;
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(label);
		OWLDataFactory fac = m.getOWLDataFactory();
		OWLReasoner reasonerESO = getReasoner(ESO);
		//  Display the word forms and definitions for synsets retrieved
		Set<OWLClass> clses = new TreeSet<OWLClass>();
		for (int i = 0; i < synsets.length; i++)
		{
			
			Synset synset = synsets[i];
			System.out.println("SYN: " + synset.getDefinition());
			wordForms = synset.getWordForms();
			for (int j = 0; j < wordForms.length; j++)
			{
				String word = wordForms[j];
				String wordTitleCase = Character.toString(word.charAt(0)).toUpperCase()+word.substring(1);
				IRI classIRI = IRI.create(ESO.getOntologyID().getOntologyIRI().toString(), "#" + wordTitleCase);
				for(OWLClass cls: ESO.getClassesInSignature())
				{
					if(cls.getIRI().toString().equals(classIRI.toString())){
						clses.add(cls);
						clses.addAll(reasonerESO.getSubClasses(cls, false).getFlattened());
					}
				}	
				
			}
		}
				
		return clses;
		
	}
	
	public Set<OWLDataProperty>  getWordNetSynDataProperties(String label){
		System.setProperty("wordnet.database.dir", wordnet.getAbsolutePath());
		String[] wordForms = null;
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(label);
		
		OWLReasoner reasonerESO = getReasoner(ESO);
		//  Display the word forms and definitions for synsets retrieved
		Set<OWLDataProperty> props = new TreeSet<OWLDataProperty>();
		for (int i = 0; i < synsets.length; i++)
		{
			//System.out.println("");
			Synset synset = synsets[i];
			wordForms = synset.getWordForms();
			for (int j = 0; j < wordForms.length; j++)
			{
				String word = wordForms[j];
				IRI propIRI = IRI.create(ESO.getOntologyID().getOntologyIRI().toString(), "#" + word);
				
				for(OWLDataProperty prop: ESO.getDataPropertiesInSignature())
				{
					if(prop.getIRI().toString().equals(propIRI.toString())){
						props.add(prop);
						props.addAll(reasonerESO.getSubDataProperties(prop, false).getFlattened());
					}
				}	
					
				
				
			}
						//System.out.println(": " + synsets[i].getDefinition());
		}
				
		return props;
		
	}
	
	public SortedSet<Suggestion> calculateJaroWinklerDistanceClass(String label){
		
		
		return null;
		
	}
	
	
	public SortedSet<Suggestion> suggestionList(String irimodellingConstruct, String label){
		resetWeightsSuggestions();
		
	
		boolean verbNounPattern = false;
		
		List<String> nouns = new ArrayList<String>();
		List<String> verbs = new ArrayList<String>();
		
		// check if label corresponds to <<verb> <<noun>
		
		if(irimodellingConstruct.equals(task) || irimodellingConstruct.equals(activity)) {
			nouns = parseSentence(label,"NN");
			verbs = parseSentence(label,"VB");
			if(!nouns.isEmpty() && !verbs.isEmpty()){
				label = nouns.get(0);
				verbNounPattern = true;
			}
				
		}
	    
		//CONSTRUCT MATCHING
		if (ConstructMatching){
	    
//			Set<OWLClass> clses1 = filterClass(constructmatchingMechanismClass(irimodellingConstruct));
//			printOWLClasses(clses1, "ESO OBjects construct matching");
//			for (OWLClass cls : clses1) {
//				Suggestion sug = sugList.get(cls.getIRI());
//				sug.setWeightConstructMatching(weightConstructMatching*scoreConstructMatching);
//				sug.setWeight(sug.getWeight() + weightConstructMatching*scoreConstructMatching);
//			}
			
			Set<OWLNamedIndividual> individuals = constructmatchingMechanismClass2(irimodellingConstruct);
			for (OWLNamedIndividual ind : individuals) {
				Suggestion sug = sugList.get(ind.getIRI());
				sug.setWeightConstructMatching(weightConstructMatching*scoreConstructMatching);
				sug.setWeight(sug.getWeight() + weightConstructMatching*scoreConstructMatching);
			}
	    
			Set<OWLDataProperty> props1 = filterDataProperties(constructmatchingMechanismDataType(irimodellingConstruct));
			printOWLDataProperties(props1, "ESO DataProperties construct matching");
			for (OWLDataProperty prop : props1) {
				Suggestion sug = sugList.get(prop.getIRI());
				sug.setWeightConstructMatching(weightConstructMatching*scoreConstructMatching);
				sug.setWeight(sug.getWeight() + weightConstructMatching*scoreConstructMatching);
	    	}
		}
	    
	    //NEIGHBORHOOD-BASED MATCHING
	  	if (NeighborhoodBasedMatching){
	    
	  		Set<OWLClass> clses2 = new HashSet<OWLClass>();
	  		if(irimodellingConstruct.equals(pool)){
	  			clses2 = filterClass(locationMechanismPool());
	  		}
//	    	else if(irimodellingConstruct.equals(message)){
//	    		clses2 = filterClass(locationgMechanismMessage(null, null));
//	    	}
	    
	    
	  		printOWLClasses(clses2,"ESO OBjects location mechanism");
	  		for (OWLClass cls : clses2) {
	  			//System.out.println(cls.getIRI());
	  			Suggestion sug = sugList.get(cls.getIRI());
	  			sug.setWeight(sug.getWeight() + weightLocationMechanism);
	  			sug.setWeightLocationMechanism(weightLocationMechanism);
	  		}
	  	}
		
	
	  //SYNONYM MATCHING
		
		if (SynonymMatching){
			
			Set<OWLClass> clses3 = filterClass(getWordNetSynClass(label));
			printOWLClasses(clses3,"ESO OBjects Wordnet Syn (" + label);
			for (OWLClass cls : clses3) {
				Suggestion sug = sugList.get(cls.getIRI());
				sug.setWeightWordnetSynonyms(wordnetMatching*weightwordnetMatching);
				sug.setWeight(sug.getWeight() + wordnetMatching*weightwordnetMatching);
			}
		
		
			Set<OWLDataProperty> props2 = filterDataProperties(getWordNetSynDataProperties(label));
			printOWLDataProperties(props2,"ESO Dataproperties Wordnet Syn");
			for(OWLDataProperty prop : props2) {
				Suggestion sug = sugList.get(prop.getIRI());
				sug.setWeightWordnetSynonyms(wordnetMatching*weightwordnetMatching);
				sug.setWeight(sug.getWeight() + wordnetMatching*weightwordnetMatching);
			}
		}
		
		
		//STRING MATCHING
		if (StringMatching){
			for (Suggestion sug : sugList.values()) {
				sug.setWeight(sug.getJaroWinklerDistance(label,JWDweightThreshold,JWDnumChars)*weightStringMatching + sug.getWeight());
				sug.setWeightTextMatching(sug.getJaroWinklerDistance(label, JWDweightThreshold,JWDnumChars));
	        }
		}
		
		
		SortedSet<Suggestion> sortedSugList = new TreeSet<Suggestion>(); 
		for (Suggestion sug : sugList.values()) {
			//System.out.println(cls.getIRI());
			if(verbNounPattern)
				sug.setSuggestionString(verbs.get(0) + " " + sug.getIri().getFragment());
        	sortedSugList.add(sug);

        }
		return sortedSugList;
		
	}
	

	
	
	public IRI addModelInstance(String iriConstruct, String id, String label){
		OWLDataFactory fac = m.getOWLDataFactory();
	    OWLClass constructClass = fac.getOWLClass(IRI
                .create(iriConstruct));
	    
	    OWLNamedIndividual element = fac.getOWLNamedIndividual(IRI.create("http://www.semanticweb.org/ontologies/model" + "#" + id));
   
        OWLClassAssertionAxiom classAssertion = fac.getOWLClassAssertionAxiom(constructClass, element);
        AddAxiom addAxiom = new AddAxiom(modelOntology, classAssertion);
        // We now use the manager to apply the change
        m.applyChange(addAxiom);
        
        
        OWLAnnotation labelAnno = fac.getOWLAnnotation(fac.getRDFSLabel(), fac.getOWLLiteral(label));
		OWLAxiom ax = fac.getOWLAnnotationAssertionAxiom(element.getIRI(), labelAnno);
		AddAxiom addAxiom2 = new AddAxiom(modelOntology, ax);
		m.applyChange(addAxiom2);
	    
	    
   
	    
	    System.out.println("Updated ontology: " + modelOntology);
	    return element.getIRI();
		
	}
	
	public IRI addModelRelationship(String iriConstructRelationship, String iriElement1, String iriElement2){
		OWLDataFactory fac = m.getOWLDataFactory();
	    
	    OWLNamedIndividual element = fac.getOWLNamedIndividual(IRI.create("iriElement1"));
	    
   
	    OWLIndividual element1 = fac.getOWLNamedIndividual(IRI.create(iriElement1));
        OWLIndividual element2 = fac.getOWLNamedIndividual(IRI.create(iriElement2));
        // We want to link the subject and object with the hasFather property,
        // so use the data factory to obtain a reference to this object
        // property.
        OWLObjectProperty relationship = fac.getOWLObjectProperty(IRI.create(iriConstructRelationship));
        // Now create the actual assertion (triple), as an object property
        // assertion axiom matthew --> hasFather --> peter
        OWLObjectPropertyAssertionAxiom assertion = fac.getOWLObjectPropertyAssertionAxiom(relationship, element1, element2);
        // Finally, add the axiom to our ontology and save
        AddAxiom addAxiomChange = new AddAxiom(modelOntology, assertion);
        m.applyChange(addAxiomChange);
	    
	    System.out.println("Updated ontology: " + modelOntology);
	    return element.getIRI();
		
	}
	
	public IRI addModelAnnotation(String iriElement, String iriOntologyElement){
		OWLDataFactory fac = m.getOWLDataFactory();
		
		OWLNamedIndividual element = fac.getOWLNamedIndividual(IRI.create(iriElement));
	    
		OWLClass ontologyElementClass = fac.getOWLClass(IRI
	                .create(iriOntologyElement));
		
		OWLAnnotation commentAnno = fac.getOWLAnnotation(fac.getRDFSIsDefinedBy(), fac.getOWLLiteral(iriOntologyElement));
		OWLAxiom ax = fac.getOWLAnnotationAssertionAxiom(element.getIRI(), commentAnno);
	        // Add the axiom to the ontology
	    m.applyChange(new AddAxiom(modelOntology, ax));
	       
	    
	    System.out.println("Updated ontology: " + modelOntology);
	    return element.getIRI();
		
	}
	
	public IRI removeModelAnnotation(String iriElement){
		OWLDataFactory fac = m.getOWLDataFactory();
		
		OWLNamedIndividual element = fac.getOWLNamedIndividual(IRI.create(modelOntology.getOntologyID().getOntologyIRI().toString() + "#" +iriElement));
	    
		OWLEntityRemover remover = new OWLEntityRemover(m,Collections.singleton(modelOntology));
		
		element.accept(remover);
		
		m.applyChanges(remover.getChanges());
		
		OWLAnnotationProperty label = fac.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_IS_DEFINED_BY.getIRI());
		
		for (OWLAnnotation annotation : element.getAnnotations(modelOntology, label))
		{
			
			RemoveOntologyAnnotation roa = new RemoveOntologyAnnotation(modelOntology,annotation);
			System.out.println(roa.toString());
			
		}
		
		
	    System.out.println("Updated ontology: " + modelOntology);
	    return element.getIRI();
		
	}

	public Set<Suggestion> getOntology(){
		
		Set<Suggestion> sugListSet = new HashSet<Suggestion>(); 
//		Set<OWLClass> clses = ESO.getClassesInSignature();
//		OWLDataFactory fac = m.getOWLDataFactory();
//	    for (OWLClass cls : clses) {
//	    	OWLAnnotationProperty description = fac.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
//	    	String descriptionValue = "";
//	    	for (OWLAnnotation annotation : cls.getAnnotations(ESO, description)) {
//                if (annotation.getValue() instanceof OWLLiteral) {
//                    OWLLiteral val = (OWLLiteral) annotation.getValue();
//                    descriptionValue = val.getLiteral();
//                    }
//            }
//			Suggestion sug = new Suggestion(cls.getIRI(), Suggestion.Type.Class, cls.getIRI().getFragment(),descriptionValue, "");
//	        sugList.add(sug);
//	    }
//	    Set<OWLDataProperty> dataProperties = ESO.getDataPropertiesInSignature();
//		printOWLDataProperties(dataProperties, "ESO DataProperties");
//	    for (OWLDataProperty prop : dataProperties) {
//	    	OWLAnnotationProperty description = fac.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
//	    	String descriptionValue = "";
//	    	String domainString = "";
//	    	for (OWLAnnotation annotation : prop.getAnnotations(ESO, description)) {
//                if (annotation.getValue() instanceof OWLLiteral) {
//                    OWLLiteral val = (OWLLiteral) annotation.getValue();
//                    descriptionValue = val.getLiteral();
//                    }
//            }
//	    	for (OWLClassExpression domainClass : prop.getDomains(ESO)) {
//                if (domainClass instanceof OWLClass) {
//                    domainString = ((OWLClass)domainClass).getIRI().getFragment() + ", ";
//                    }
//            }
//	    	
//			Suggestion sug = new Suggestion(prop.getIRI(), Suggestion.Type.Datatype, prop.getIRI().getFragment(), descriptionValue, domainString);
//			sugList.add(sug);
//	    }	
		
		for(Suggestion sug: sugList.values())
			sugListSet.add(sug);
		
		return sugListSet;
		
	}
	
	public void printSugList(SortedSet<Suggestion> sugList){
		System.out.println();
		for(Suggestion sug: sugList)
			System.out.println(sug.getIri() + "   Weight=" + sug.getWeight());
		System.out.println();
		
	}
	
	public void printOWLClasses(Set<OWLClass> clses, String title){
		System.out.println();
		System.out.println(title + "(size=" + clses.size() + ")");
		System.out.println("--------------------------");
		for(OWLClass ocl: clses)
			System.out.println(ocl.getIRI());
		System.out.println();
		
	}
	
	public void printOWLDataProperties(Set<OWLDataProperty> dataProperties, String title){
		System.out.println();
		System.out.println(title + "(size=" + dataProperties.size() + ")");
		System.out.println("--------------------------");
		for(OWLDataProperty prop: dataProperties)
			System.out.println(prop.getIRI());
		System.out.println();
		
	}
	
	public Set<OWLClass> filterClass(Set<OWLClass> entities){
		Set<OWLClass> filteredEntities = new TreeSet<OWLClass>();
		for (OWLClass entity : entities) {
			if(ESO.containsClassInSignature(entity.getIRI()))
				filteredEntities.add(entity);
        }
		return filteredEntities;
     }
	
	public Set<OWLDataProperty> filterDataProperties(Set<OWLDataProperty> entities){
		Set<OWLDataProperty> filteredEntities = new TreeSet<OWLDataProperty>();
		for (OWLDataProperty entity : entities) {
			if(ESO.containsDataPropertyInSignature(entity.getIRI()))
				filteredEntities.add(entity);
        }
		return filteredEntities;
     }
	
		
	
	public List<String> parseSentence(String input, String Tag){
		
		 Tree parse = lp.parse(input.toLowerCase());
		 List<String> taggedWords = new ArrayList<String>(); 
		 for (TaggedWord tw : parse.taggedYield()) {
		   if (tw.tag().startsWith(Tag)) {
			 taggedWords.add(tw.word());
		     System.out.printf("%s/%s%n", tw.word(), tw.tag());
		   }
		 }
		 
		 parse.pennPrint();
		 System.out.println();
		return taggedWords;
	    
	}

	
	 public void saveModelOntology() throws OWLOntologyStorageException, OWLOntologyCreationException, IOException {
		 
		 // Now save a local copy of the ontology. (Specify a path appropriate to
		 // your setup)
		 //Bundle bundle = Platform.getBundle("org.eclipse.bpmn2.modeler.suggestion");
		 
		 IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		 IProject project;
		 if (input instanceof Bpmn2DiagramEditorInput) {
			Bpmn2DiagramEditorInput bpmnInput = (Bpmn2DiagramEditorInput) input;
			URI inputUri = bpmnInput.getModelUri();
			String inputString = inputUri.path();
			String model = inputString.split("/")[2];
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			project = ws.getRoot().getProject(model);
		} else {
			 project = ((IFileEditorInput)input).getFile().getProject();
			 
		}
		 //String uri = ((IFileEditorInput)input).getFile().getProject()
         
		 //System.out.println(uri);
		 
		 //IWorkspace ws = ResourcesPlugin.getWorkspace();
		 String projectDir = project.getLocationURI().getPath();

		 File file = new File(projectDir + "/" + modelName + ".owl");
		 
		 OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
		 m.saveOntology(modelOntology, owlxmlFormat, IRI.create(file.toURI()));
		 
		 System.out.println("Owl file saved: " + file.getAbsolutePath());
		 System.out.println("Owl file saved to: " + projectDir);
		 
		 file.createNewFile();
		 
		 //OWLOntologyDocumentTarget documentTarget = new SystemOutDocumentTarget();
		
		 //ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
		 
		 //m.saveOntology(model, manSyntaxFormat, documentTarget);
		 //file.delete();
	 }
	 
	 public void resetWeightsSuggestions(){
		 for(Suggestion sug: sugList.values()){
			 sug.setWeight(0);
			 sug.setWeightConstructMatching(0);
			 sug.setWeightLocationMechanism(0);
			 sug.setWeightWordnetSynonyms(0);
			 sug.setWeightTextMatching(0);
			 sug.setSuggestionString(sug.getOntologyString());
		 }
		 
			 
	 }
	 /**
		* This function reads in the properties from a properties-file
		*/	
		public void ReadPropertiesFile(FileInputStream fileInput) throws Exception{
			try {
				
				
				Properties properties = new Properties();
				properties.load(fileInput);
				fileInput.close();

				Enumeration enuKeys = properties.keys();
				System.out.println("Read properties-file:");
				while (enuKeys.hasMoreElements()) {
					String key = (String) enuKeys.nextElement();
					String value = properties.getProperty(key);
					System.out.println(key + ": " + value);
					
					switch(key){
						case "StringMatching":
							StringMatching = Boolean.parseBoolean(value);
							break;
						case "SynonymMatching":
							SynonymMatching = Boolean.parseBoolean(value);
							break;
						case "ConstructMatching":
							ConstructMatching = Boolean.parseBoolean(value);
							break;
						case "NeighborhoodBasedMatching":
							NeighborhoodBasedMatching = Boolean.parseBoolean(value);
							break;
						case "JWDweightThreshold":
							JWDweightThreshold = Double.parseDouble(value);
							break;	
						case "JWDnumChars":
							JWDweightThreshold = Integer.parseInt(value);
							break;
						case "weightStringMatching":
							weightStringMatching = Double.parseDouble(value);
							break;	
						case "weightConstructMatching":
							weightConstructMatching = Double.parseDouble(value);
							break;	
						case "scoreConstructMatching":
							scoreConstructMatching = Double.parseDouble(value);
							break;	
						case "weightLocationMatching":
							weightLocationMatching = Double.parseDouble(value);
							break;		
						case "scoreLocationMatching":
							scoreLocationMatching = Double.parseDouble(value);
							break;	
						case "wordnetMatching":
							wordnetMatching = Double.parseDouble(value);
							break;			
						case "weightwordnetMatching":
							weightwordnetMatching = Double.parseDouble(value);
							break;		
						case "makeNewOntology":
							makeNewOntology = Boolean.parseBoolean(value);
							break;	
						case "AnnotateWhenSuggestionClicked":
							AnnotateWhenSuggestionClicked = Boolean.parseBoolean(value);
							break;	
						case "AnnotateSynonymBasedGenerationSuggestions":
							AnnotateSynonymBasedGenerationSuggestions = Boolean.parseBoolean(value);
							break;			
						case "automaticAnnotation":
							automaticAnnotation = Boolean.parseBoolean(value);
							break;			
						case "AnnotationIndication":
							canAnnQua = value.toString();
							break;
						case "CandidateAnnotationPossiblity":
							CandidateAnnotationPossiblity=Boolean.parseBoolean(value);
							break;
						case "source":
							source=value;
							break;
						case "CoreOntology":
							CoreOntology=value;
							break;
						case "BPMNOntology":
							BPMNOntology=value;
							break;
						case "CoreBPMNOntology":
							CoreBPMNOntology=value;
							break;
						case "ESOntology":
							ESOntology=value;
							break;
						case "CoreESOOntology":
							CoreESOOntology=value;
							break;
						case "ModelOntology":
							ModelOntology=value;
							break;
					}

				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	

}
