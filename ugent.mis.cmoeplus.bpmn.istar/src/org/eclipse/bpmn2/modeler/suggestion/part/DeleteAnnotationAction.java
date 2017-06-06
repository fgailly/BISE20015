package org.eclipse.bpmn2.modeler.suggestion.part;

import java.io.IOException;

import org.eclipse.bpmn2.impl.FlowElementImpl;
import org.eclipse.bpmn2.impl.LaneImpl;
import org.eclipse.bpmn2.impl.MessageFlowImpl;
import org.eclipse.bpmn2.impl.ParticipantImpl;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import ugent.mis.cmoeplus.Recommendation;
import ugent.mis.cmoeplus.bpmn.istar.views.OntologyPropertyViewAdvanced;
import ugent.mis.cmoeplus.bpmn.istar.views.OntologyView;
import ugent.mis.cmoeplus.bpmn.istar.views.SuggestionView;

public class DeleteAnnotationAction implements
		IWorkbenchWindowActionDelegate {
	
	
	private SuggestionView sugView;
	private OntologyPropertyViewAdvanced ontAdvView;
	private boolean suggestions;
	private boolean advanced;
	private EObject selectionEObject;
	private ISelection selection;
	

	private Boolean findViews() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(SuggestionView.ID);
		if (view == null) {

			this.suggestions = false;
			return false;
		} else {
			this.suggestions = true;
			sugView = (SuggestionView) view;
			view = activePage.findView(OntologyPropertyViewAdvanced.ID);
			if(view == null){
				advanced = false;
				return false;
			}
			else{
				this.advanced = true;
				ontAdvView = (OntologyPropertyViewAdvanced) view;
				return true;
			}
		}		
	}

	



	private void setUnknownProperties() {
		sugView.getListener().setType(null);
		sugView.getListener().setName(null);
		sugView.getListener().setId("Error");

	}

	

	private boolean containsModelElementCreationListener() {
		for (Adapter ad : selectionEObject.eAdapters()) {
			if (ad instanceof ModelElementCreationProcessor2) {
				return true;
			}
		}
		return false;
	}

	private boolean containsPropListener() {
		for (Adapter ad : selectionEObject.eAdapters()) {
			if (ad instanceof PropertyChangeProcessor) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void run(IAction action) {
		findViews();
		
		System.out.println("TEST:" + sugView.getListener().getUniqueId());
		
		if(sugView.getListener().getAnnotated()){
			sugView.getEngine().getManager().removeModelAnnotation(sugView.getListener().getUniqueId());
			sugView.showMessage("Annotation Deleted");
			annotateUI(sugView.getListener().getName());
			sugView.getListener().setName(sugView.getListener().getName());
			sugView.getListener().setAnnotated(false);
			
		
			
			try {
				sugView.getEngine().saveModelOntology();
			} catch (OWLOntologyStorageException | OWLOntologyCreationException
					| IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Did not save");
			}
		}
		else
			sugView.showMessage("Element not annotated");
		

		
	}
	
	protected void annotateUI(final String sug) {
		if ((sugView.getListener().getSelection() != null)) {
			// Events, Tasks and Gateways are subclasses of FlowElementImpl (subclass of BaseElementImpl), 
			// which implements setName(). 
			if (sugView.getListener().getSelection() instanceof FlowElementImpl) {
				final FlowElementImpl selectedObj = (FlowElementImpl) sugView.getListener().getSelection();
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(selectedObj);
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
				protected void doExecute() {
					selectedObj.setName(sug);
				}
				});
			}
			// Pools and Lanes are subclasses of BaselementImpl, which does not implement setName().
			// Therefore we cast them as their own business object to manipulate the name
			else if (sugView.getListener().getSelection() instanceof ParticipantImpl) {
				final ParticipantImpl selectedObj = (ParticipantImpl) sugView.getListener().getSelection();
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(selectedObj);
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
				protected void doExecute() {
					selectedObj.setName(sug);
				}
				});
			}
			else if (sugView.getListener().getSelection() instanceof LaneImpl) {
				final LaneImpl selectedObj = (LaneImpl) sugView.getListener().getSelection();
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(selectedObj);
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
				protected void doExecute() {
					selectedObj.setName(sug);
				}
				});
			}
			// MessageFlows is a different subclass of BaseElementImpl
			else if (sugView.getListener().getSelection() instanceof MessageFlowImpl) {
				final MessageFlowImpl selectedObj = (MessageFlowImpl) sugView.getListener().getSelection();
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(selectedObj);
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
				protected void doExecute() {
					selectedObj.setName(sug);
				}
				});
			}
		}
		
	}
				
			


	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		

	}
	
	
	


}
