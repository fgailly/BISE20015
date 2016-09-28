package org.eclipse.bpmn2.modeler.suggestion.internal;

import org.eclipse.jface.resource.FontRegistry;

public class OntologyPropertyColumn extends OntologyColumn {

	public OntologyPropertyColumn(FontRegistry fr) {
		super(fr);
		
	}

	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String property = (String) element;
			return property.split("#")[0];
		}
		return null;
	}
	
	@Override
	public int getWidth() {
		return 80; 
	}

	@Override
	public String getTitle() {
		return "Property";
	}

}
