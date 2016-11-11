package org.eclipse.bpmn2.modeler.suggestion.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine;
import org.eclipse.bpmn2.modeler.suggestion.views.OntologyView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import ugent.mis.cmoeplus.Recommendation;
import ugent.mis.cmoeplus.Recommendation.Type;

public class OntologyModel {

	public List<OntologyCategory> getSuggestions() {

		List<OntologyCategory> ontologyModel = new ArrayList<OntologyCategory>();
		
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(OntologyView.ID);
		assert(view != null);
		OntologyView ontView = (OntologyView) view;
		BPMNSuggestionEngine engine = ontView.getEngine();
		Set<Recommendation> sugSet = engine.getOntology();
		assert(sugSet != null);
		Object[] sugArray = (Object[]) sugSet.toArray();
		
		for (int i = 0; i < sugArray.length; i++) {
			Recommendation sug = (Recommendation) sugArray[i];
			String type = sug.getType().toString();
			if (!existingCategory(type, ontologyModel)) {
				OntologyCategory sugCat = new OntologyCategory();
				sugCat.setName(type);
				ontologyModel.add(sugCat);
			}
			OntologyCategory category = getCategory(type, ontologyModel);
			category.getSuggestions().add(sug);
		}
		if (ontView.getListener().getAnnotated()) {
			addDeleteOption(ontologyModel);
		}
		sortontologyModel(ontologyModel);
		return ontologyModel;
	}

	private void addDeleteOption(List<OntologyCategory> ontologyModel) {
		OntologyCategory del = new OntologyCategory();
		del.setName("Delete");
		ontologyModel.add(del);
		// TODO Auto-generated method stub
		
	}

	private void sortontologyModel(List<OntologyCategory> ontologyModel) {
		for (OntologyCategory suggestionCategory : ontologyModel) {
			suggestionCategory.sort();
		}
		Collections.sort(ontologyModel);
		
	}

	private OntologyCategory getCategory(String type, List<OntologyCategory> ontologyModel) {
		for (OntologyCategory suggestionCategory : ontologyModel) {
			if (suggestionCategory.getName() == type) {
				return suggestionCategory;
			}
		}
		return null;
	}

	private boolean existingCategory(String type, List<OntologyCategory> ontologyModel) {
		for (OntologyCategory suggestionCategory : ontologyModel) {
			if (suggestionCategory.getName() == type) {
				return true;
			}
		}
		return false;
	}


}
