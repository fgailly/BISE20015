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
		//addField(new StringFieldEditor("SDServer", "Stardog Server: ", getFieldEditorParent()));
		//addField(new StringFieldEditor("SDLogin", "Stardog Login: ", getFieldEditorParent()));
		//addField(new StringFieldEditor("SDPassword", "Stardog Password: ", getFieldEditorParent()));
		addField(new StringFieldEditor("CoO", "Core ontology: ", getFieldEditorParent()));
		addField(new StringFieldEditor("BpmnO", "BPMN ontology: ", getFieldEditorParent()));
		addField(new StringFieldEditor("ESO", "Enterprise-specici ontology: ", getFieldEditorParent()));
		addField(new StringFieldEditor("CoO-BpmnO", "CoO-BPMN mappings: ", getFieldEditorParent()));
		addField(new StringFieldEditor("RuleO", "Rules: ", getFieldEditorParent()));
		
	}

	

}
