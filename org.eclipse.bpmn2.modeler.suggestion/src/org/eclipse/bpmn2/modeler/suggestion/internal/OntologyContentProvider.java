package org.eclipse.bpmn2.modeler.suggestion.internal;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.Suggestion;
import org.eclipse.bpmn2.modeler.suggestion.part.OntologyCategory;
import org.eclipse.bpmn2.modeler.suggestion.part.OntologyModel;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class OntologyContentProvider implements ITreeContentProvider {

	private OntologyModel model;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.model = (OntologyModel) newInput;

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return model.getSuggestions().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof OntologyCategory) {
			OntologyCategory category = (OntologyCategory) parentElement;
			return category.getSuggestions().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Suggestion) {
			for (Object o : model.getSuggestions()) {
				if (o instanceof OntologyCategory) {
					OntologyCategory suggestionCategory = (OntologyCategory) o;
					for (Suggestion sug : suggestionCategory.getSuggestions()) {
						if (element.equals(sug)) {
							return suggestionCategory;
						}
					}
				}
			}
			return null;
		} else {
			return null;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof OntologyCategory) {
			return true;
		}
		return false;
	}
}
