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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import ugent.mis.cmoeplus.Recommendation;
import ugent.mis.cmoeplus.bpmn.istar.views.OntologyPropertyViewAdvanced;
import ugent.mis.cmoeplus.bpmn.istar.views.OntologyView;
import ugent.mis.cmoeplus.bpmn.istar.views.SuggestionView;

public class LoadSugViewAction implements
		IWorkbenchWindowActionDelegate {
	
	public static final String SuggestionID = "ugent.mis.cmoeplus.bpmn.SuggestionView";
	
	
	


	@Override
	public void run(IAction action) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SuggestionID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		

	}



	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}
	
	
	


}
