package org.eclipse.bpmn2.modeler.suggestion.part;

import java.io.IOException;

import org.eclipse.bpmn2.impl.FlowElementImpl;
import org.eclipse.bpmn2.impl.LaneImpl;
import org.eclipse.bpmn2.impl.MessageFlowImpl;
import org.eclipse.bpmn2.impl.ParticipantImpl;
import org.eclipse.bpmn2.modeler.suggestion.views.OntologyView;
import org.eclipse.bpmn2.modeler.suggestion.views.SuggestionView;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import ugent.mis.cmoeplus.Recommendation;

public class AnnotateProcessor extends Action {
	
	private ViewPart viewer;
	private SelectionChangeProcessor sel;
	public Boolean suggestions;
	private EObject selection;
	
	private String owlUrl = "http://www.mis.ugent.be/ontologies/bank2.owl#";

	
	public AnnotateProcessor(SelectionChangeProcessor sel) {
		super();
		this.sel = sel;
		this.selection = sel.getSelection();
		
	}
	
	private Boolean findView() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(SuggestionView.ID);
		if (view == null) {
			view = activePage.findView(OntologyView.ID);
			viewer = (OntologyView) view;
			this.suggestions = false;
			return false;
		} else {
			this.suggestions = true;
			viewer = (SuggestionView) view;
			return true;
		}		
	}
	
	@Override
	public void run() {
		findView();
		this.selection = sel.getSelection();
		ISelection viewSelection;
		if (suggestions) {
			viewSelection = ((SuggestionView)viewer).getSelection();	
		} else {
			viewSelection = ((OntologyView)viewer).getSelection();
		}
		Object obj = ((IStructuredSelection)viewSelection).getFirstElement();
		if (obj instanceof Recommendation) {
			Recommendation sug = (Recommendation) obj;
			if(sug.getScore() == Double.MAX_VALUE) {
				//Delete Annotation
				//deleteAnnotation(sug.getSuggestionString());
				annotateUI(sel.getName());
				refresh(sel.getName(),true);
				if (suggestions) {
					deleteAnnotation(sel.getUniqueId());
					((SuggestionView)viewer).showMessage("Annotation Deleted");
				} else {
					deleteAnnotation(sel.getUniqueId());
					((OntologyView)viewer).showMessage("Annotation Deleted");
				}
			}
			else if (!sel.getAnnotated()) {

				
				annotateSuggestion(sel.getUniqueId(), sug.getOntologyString(), sug);
				annotateUI("@"+sug.getSuggestionString());
				refresh(sug.getSuggestionString(), false);
				if (suggestions) {
					((SuggestionView)viewer).showMessage("Annotation "+ sug.getSuggestionString() + " added to " + sel.getConstructId() + ": " + sel.getName());
				} else {
					((OntologyView)viewer).showMessage("Annotation "+ sug.getSuggestionString() + " added to " + sel.getConstructId() + ": " + sel.getName());
				}
			}
			else {
				if (suggestions) {
					((SuggestionView)viewer).showErrorMessage("Whoa! " + sel.getConstructId() + ": " + sel.getName() + " is already annotated! Delete the annotation first!");
				} else {
					((OntologyView)viewer).showErrorMessage("Whoa! " + sel.getConstructId() + ": " + sel.getName() + " is already annotated! Delete the annotation first!");
				}
			}

		} else if (obj instanceof OntologyCategory) {
			OntologyCategory oCat = (OntologyCategory) obj;
			if (oCat.getName().equals("Delete")) {
				//Delete Annotation
				//deleteAnnotation(sug.getSuggestionString());
				annotateUI(sel.getName());
				refresh(sel.getName(), true);
				if (suggestions) {
					((SuggestionView)viewer).showMessage("Annotation Deleted");
				} else {
					((OntologyView)viewer).showMessage("Annotation Deleted");
				}
			}
		}
		
		try {
			if (suggestions) {
			//	((SuggestionView)viewer).getEngine().saveModelOntology();	
			} else {
				((OntologyView)viewer).getEngine().saveModelOntology();
			}
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	

	private void refresh(String name, Boolean delete) {
		// We need the new name and new annotation flag to build up to date Model
		sel.setName(name);
		if (delete) {
			sel.setAnnotated(false);
		} else {
			sel.setAnnotated(true);
		}
		
		if (suggestions) {
			((SuggestionView)viewer).setInput(new SuggestionModel());
		} else {
			((OntologyView)viewer).setInput(new OntologyModel());
			}
	}

	protected void annotateSuggestion(String id, String label, Recommendation suggestion) {
		if (sel.getType() != "unknown" && sel.getType() != "Error") {
			String constructIRI = sel.createBpmnUrl();
			String ontologyID = createOwlUrl(label);
			if (suggestions) {
				IRI modelIRI = ((SuggestionView)viewer).getEngine().getManager().addModelInstance(constructIRI, id, label );
				((SuggestionView)viewer).getEngine().getManager().addModelAnnotation(modelIRI.toString(), ontologyID, suggestion);
			} else {
				IRI modelIRI = ((OntologyView)viewer).getEngine().addModelInstance(constructIRI, id, label);
				((OntologyView)viewer).getEngine().addModelAnnotation(modelIRI.toString(), ontologyID);
			}
		}	
	}
	
	private void deleteAnnotation(String uniqueId) {
		
		((SuggestionView)viewer).getEngine().getManager().removeModelAnnotation(uniqueId);
		
	}
	
	protected void annotateUI(final String sug) {
		if ((selection != null) && !(sel.getConstructId().equals("unknown")) && !(sel.getConstructId().equals("Error"))) {
			// Events, Tasks and Gateways are subclasses of FlowElementImpl (subclass of BaseElementImpl), 
			// which implements setName(). 
			if (selection instanceof FlowElementImpl) {
				final FlowElementImpl selectedObj = (FlowElementImpl) selection;
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(selectedObj);
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
				protected void doExecute() {
					selectedObj.setName(sug);
				}
				});
			}
			// Pools and Lanes are subclasses of BaselementImpl, which does not implement setName().
			// Therefore we cast them as their own business object to manipulate the name
			else if (selection instanceof ParticipantImpl) {
				final ParticipantImpl selectedObj = (ParticipantImpl) selection;
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(selectedObj);
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
				protected void doExecute() {
					selectedObj.setName(sug);
				}
				});
			}
			else if (selection instanceof LaneImpl) {
				final LaneImpl selectedObj = (LaneImpl) selection;
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(selectedObj);
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
				protected void doExecute() {
					selectedObj.setName(sug);
				}
				});
			}
			// MessageFlows is a different subclass of BaseElementImpl
			else if (selection instanceof MessageFlowImpl) {
				final MessageFlowImpl selectedObj = (MessageFlowImpl) selection;
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(selectedObj);
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
				protected void doExecute() {
					selectedObj.setName(sug);
				}
				});
			}
		}
		
	}
	
	
	private String createOwlUrl(String label) {
		return this.owlUrl + label;
	}


}
