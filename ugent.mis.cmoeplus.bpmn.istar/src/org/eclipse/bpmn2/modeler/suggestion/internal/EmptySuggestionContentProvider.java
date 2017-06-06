package org.eclipse.bpmn2.modeler.suggestion.internal;


import org.eclipse.bpmn2.modeler.suggestion.part.OntologyCategory;
import org.eclipse.bpmn2.modeler.suggestion.part.SuggestionModel;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EmptySuggestionContentProvider implements ITreeContentProvider {

	

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		

	}

	@Override
	public Object[] getElements(Object inputElement) {
		String[] contents = {"No Suggestions"};
		return contents;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		
		return null;
	}

	@Override
	public Object getParent(Object element) {

			return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		
		return false;
	}
	
}


