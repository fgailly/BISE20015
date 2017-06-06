package org.eclipse.bpmn2.modeler.suggestion.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class BPMNPerspective implements IPerspectiveFactory {

	public static final String ID = "org.eclipse.bpmn2.modeler.suggestion.perspectives.BPMNPerspective";
	public static final String SuggestionID = "org.eclipse.bpmn2.modeler.suggestion.views.SuggestionView";
	public static final String OntologyID = "org.eclipse.bpmn2.modeler.suggestion.views.OntologyView";
	public static final String OntologyPropID = "org.eclipse.bpmn2.modeler.suggestion.views.OntologyPropertyView";
	
	public static final String sugID = "org.eclipse.bpmn2.modeler.suggestion.perspectives.BPMNSuggestionPerspective";
	public static final String ontID = "org.eclipse.bpmn2.modeler.suggestion.perspectives.BPMNOntologyPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		String editorArea = layout.getEditorArea();
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.25f, editorArea);
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.BOTTOM, 0.50f, IPageLayout.ID_PROJECT_EXPLORER);
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.66f, editorArea);
	
		layout.addNewWizardShortcut("UML_ER.diagram.part.UML_ERCreationWizardID");//NON-NLS-1
		layout.addNewWizardShortcut("UML_ER.diagram.Erdiagram");//NON-NLS-1
		layout.addNewWizardShortcut("org.eclipse.bpmn2.modeler.ui.diagram");//NON-NLS-1
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.project"); //$NON-NLS-1$
		
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET); //NON-NLS-1
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE); //NON-NLS-1
		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER); //NON-NLS-1
		layout.addShowViewShortcut(SuggestionID);
		layout.addShowViewShortcut(OntologyID);
		layout.addShowViewShortcut(OntologyPropID);
		
		layout.addPerspectiveShortcut(sugID);
		layout.addPerspectiveShortcut(ontID);
	}
}
