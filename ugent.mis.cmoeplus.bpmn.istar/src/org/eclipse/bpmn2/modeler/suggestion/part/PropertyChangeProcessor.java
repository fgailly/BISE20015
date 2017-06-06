package org.eclipse.bpmn2.modeler.suggestion.part;


import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import ugent.mis.cmoeplus.bpmn.istar.views.SuggestionView;

public class PropertyChangeProcessor extends AdapterImpl {
	
	private SuggestionView viewer;
	private SelectionChangeProcessor selection;
	
	public PropertyChangeProcessor(SelectionChangeProcessor sel) {
		super();
		this.selection = sel;
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(SuggestionView.ID);
		assert(view != null);
		viewer = (SuggestionView) view;
	}

	@Override
	public void notifyChanged(Notification msg) {
		if (msg.getFeature() instanceof EAttributeImpl) {
			EAttributeImpl o = (EAttributeImpl) msg.getFeature();
			if(o.getName().equals("name")){
				if(msg.getNewStringValue() != null)
				{
					selection.setName(msg.getNewStringValue());
					//createSuggestions();
					viewer.setInput(new SuggestionModel());
					//System.out.println("New Suggestion Generated for: " + selection.getType() + " " + selection.getName() + "(" + msg.getOldStringValue() + ")");
			
				}
			}
		}
	}

}
