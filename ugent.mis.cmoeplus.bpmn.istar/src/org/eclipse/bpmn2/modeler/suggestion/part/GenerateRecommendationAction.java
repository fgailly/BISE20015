package org.eclipse.bpmn2.modeler.suggestion.part;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import ugent.mis.cmoeplus.Recommendation;
import ugent.mis.cmoeplus.bpmn.istar.views.OntologyPropertyViewAdvanced;
import ugent.mis.cmoeplus.bpmn.istar.views.SuggestionView;

public class GenerateRecommendationAction implements
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
		sugView.setInput(new SuggestionModel());
		//sugView.getViewer().getTree().setSelection(sugView.getViewer().getTree().getItem(0));
		Object selected =  sugView.getViewer().getTree().getItem(0);
		if (selected instanceof Recommendation && selected != null) {
			Recommendation sug = (Recommendation) selected;
			if(sug.getType() != null){
				String[] input = ontAdvView.createPropertyArray(sug);
				ontAdvView.getTableViewer().setInput(input);
			}
		}
		else
			System.out.println("NOTHING selected");
		
						

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
