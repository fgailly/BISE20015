package org.eclipse.bpmn2.modeler.suggestion.internal;

import org.eclipse.jface.resource.FontRegistry;

public class OntologyValueColumn extends OntologyColumn {

	public OntologyValueColumn(FontRegistry fr) {
		super(fr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String property = (String) element;
			if (property.split("#").length > 1) {
				return property.split("#")[1];
			} else {
				return "";
			}
		}
		return "";
	}
	
	@Override
	public int getWidth() {
		return 800; 
	}

	@Override
	public String getTitle() {
		return "Value";
	}

}
