package org.eclipse.bpmn2.modeler.suggestion;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class OntologiesPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor("CORE", "Core ontology: ", getFieldEditorParent()));
		addField(new StringFieldEditor("BPMN", "BPMN ontology: ", getFieldEditorParent()));
		addField(new StringFieldEditor("ESO", "ESO: ", getFieldEditorParent()));
		addField(new StringFieldEditor("BPMN_CORE", "BPMN - Core ontology mapings: ", getFieldEditorParent()));
		addField(new StringFieldEditor("ESO_CORE", "ESO - Core ontology mapings: ", getFieldEditorParent()));
		
	}

	

}
