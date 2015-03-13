package org.eclipse.bpmn2.modeler.suggestion.internal;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.Suggestion;
import org.eclipse.bpmn2.modeler.suggestion.part.OntologyCategory;
import org.eclipse.bpmn2.modeler.suggestion.part.SuggestionModel;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SuggestionContentProvider implements ITreeContentProvider {

	private SuggestionModel model;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.model = (SuggestionModel) newInput;

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement != null)
			return model.getSuggestions().toArray();
		else{
			String[] contents = {"No Suggestions"};
			return contents;
			
		}
			
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
		if(model != null && element instanceof Suggestion) {
			for (Object o : model.getSuggestions()) {
				if (o instanceof OntologyCategory) {
					OntologyCategory suggestionCategory = (OntologyCategory) o;
					for (Suggestion sug : suggestionCategory.getSuggestions()) {
						if (element.equals(sug)) {
							return suggestionCategory;
							}
						}
				}
			return null;
			}
		}
		else {
			return null;
		}
		return null;	
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof OntologyCategory) {
			return true;
		}
		return false;
	}
}


