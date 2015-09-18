package org.eclipse.bpmn2.modeler.suggestion;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class OntologyPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public OntologyPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault("SDServer", "http://bizagi.ugent.be:5820/");
		store.setDefault("SDLogin", "admin");
		store.setDefault("SDPassword", "liesbeth1812");
		store.setDefault("ESO", "Bank");
		store.setDefault("CoO", "UFO");
		store.setDefault("BpmnO", "BPMN");
		store.setDefault("CoO-BpmnO", "BPMN_UFO");
		store.setDefault("RuleO", "ExperimentRules");

	}

}
