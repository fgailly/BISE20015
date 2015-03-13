package org.eclipse.bpmn2.modeler.suggestion.internal;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.BPMNSuggestionEngine2;
import org.eclipse.bpmn2.modeler.suggestion.algorithm.Suggestion;
import org.eclipse.bpmn2.modeler.suggestion.views.SuggestionView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class SuggestionComparator implements Comparator {
	
	
	public int compare(Object o1, Object o2) {
		if(o1 instanceof Suggestion && o2 instanceof Suggestion) {
			return ((Suggestion) o1).compareTo(((Suggestion) o2));
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	public static Map<String, Set<Suggestion>> getSuggestions(String idUrl, String name){
		
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(SuggestionView.ID);
		assert(view != null);
		SuggestionView sugView = (SuggestionView) view;
		BPMNSuggestionEngine2 engine = sugView.getEngine();
		SortedSet<Suggestion> sugSet = engine.suggestionList(idUrl, name);
		assert(sugSet != null);
		Suggestion[] sugArray = (Suggestion[]) sugSet.toArray();
		Map<String, Set<Suggestion>> suggestions = new TreeMap<String, Set<Suggestion>>();
		for (int i = 0; i < sugArray.length; i++) {
			Suggestion sug = sugArray[i];
			String type = sug.getType().toString();
			Set<Suggestion> typedSug = suggestions.get(type);
			if (typedSug == null) {
				typedSug = new TreeSet<Suggestion>(new SuggestionComparator());
				suggestions.put(type, typedSug);
			}
			typedSug.add(sug);
		 }
		 return suggestions;
	}
	
	

}
