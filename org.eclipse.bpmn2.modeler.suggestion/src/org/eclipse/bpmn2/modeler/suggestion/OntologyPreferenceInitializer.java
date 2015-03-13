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
		store.setDefault("ESO", "Bank");
		store.setDefault("CORE", "UFO");
		store.setDefault("BPMN", "BPMN");
		store.setDefault("BPMN_CORE", "BPMN_UFO");
		store.setDefault("ESO_CORE", "Bank_UFO");

	}

}
