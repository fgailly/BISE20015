package org.eclipse.bpmn2.modeler.suggestion.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import ugent.mis.cmoeplus.Recommendation;
import ugent.mis.cmoeplus.bpmn.istar.BPMNSuggestionEngine;
import ugent.mis.cmoeplus.bpmn.istar.views.SuggestionView;

public class SuggestionModel {

	public List<Recommendation> getSuggestions() {

		List<Recommendation> suggestionModel = new ArrayList<Recommendation>();
		
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(SuggestionView.ID);
		assert(view != null);
		SuggestionView sugView = (SuggestionView) view;
		String name = sugView.getListener().getName();
		String idUrl = sugView.getListener().createBpmnUrl();
		String uniqueId = sugView.getListener().getUniqueId();
		BPMNSuggestionEngine engine = sugView.getEngine();
		SortedSet<Recommendation> sugSet = engine.suggestionList(idUrl, name);
		//engine.printSugList(sugSet);
		assert(sugSet != null);
		Object[] sugArray = (Object[]) sugSet.toArray();
		for (int i = 0; i < sugArray.length; i++) {
			Recommendation sug = (Recommendation) sugArray[i];
			suggestionModel.add(sug);
		}
		
		
		sortSuggestionModel(suggestionModel);
		return suggestionModel;
	}

	private void addDeleteOption(List<Recommendation> suggestionModel) {
		Recommendation del = new Recommendation(null, Double.MAX_VALUE, null, "Delete the assigned annotation", null, null);
		suggestionModel.add(del);
	}

	private void sortSuggestionModel(List<Recommendation> suggestionModel) {
		Collections.sort(suggestionModel);
		int i=0;
		for(Recommendation suggestion: suggestionModel){
			suggestion.setOrder(i);
			i++;
		}
		
	}

}
