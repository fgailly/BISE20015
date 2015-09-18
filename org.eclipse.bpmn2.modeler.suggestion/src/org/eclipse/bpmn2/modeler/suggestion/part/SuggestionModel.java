package org.eclipse.bpmn2.modeler.suggestion.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine2;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.Suggestion;
import org.eclipse.bpmn2.modeler.suggestion.views.SuggestionView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class SuggestionModel {

	public List<Suggestion> getSuggestions() {

		List<Suggestion> suggestionModel = new ArrayList<Suggestion>();
		
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(SuggestionView.ID);
		assert(view != null);
		SuggestionView sugView = (SuggestionView) view;
		String name = sugView.getListener().getName();
		String idUrl = sugView.getListener().createBpmnUrl();
		String uniqueId = sugView.getListener().getUniqueId();
		BPMNSuggestionEngine2 engine = sugView.getEngine();
		SortedSet<Suggestion> sugSet = engine.suggestionList(idUrl, name);
		//engine.printSugList(sugSet);
		assert(sugSet != null);
		Object[] sugArray = (Object[]) sugSet.toArray();
		for (int i = 0; i < sugArray.length; i++) {
			Suggestion sug = (Suggestion) sugArray[i];
			suggestionModel.add(sug);
		}
		if (sugView.getListener().getAnnotated()) {
			addDeleteOption(suggestionModel);
		}
		sortSuggestionModel(suggestionModel);
		return suggestionModel;
	}

	private void addDeleteOption(List<Suggestion> suggestionModel) {
		Suggestion del = new Suggestion(null, Double.MAX_VALUE, null, "Delete the assigned annotation", null, null);
		suggestionModel.add(del);
	}

	private void sortSuggestionModel(List<Suggestion> suggestionModel) {
		Collections.sort(suggestionModel);
		
	}

}
