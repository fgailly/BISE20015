package org.eclipse.bpmn2.modeler.suggestion.internal;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import ugent.mis.cmoeplus.Recommendation;
import ugent.mis.cmoeplus.bpmn.istar.BPMNSuggestionEngine;
import ugent.mis.cmoeplus.bpmn.istar.views.SuggestionView;

public class SuggestionComparator implements Comparator {
	
	
	public int compare(Object o1, Object o2) {
		if(o1 instanceof Recommendation && o2 instanceof Recommendation) {
			return ((Recommendation) o1).compareTo(((Recommendation) o2));
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	public static Map<String, Set<Recommendation>> getSuggestions(String idUrl, String name){
		
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = activePage.findView(SuggestionView.ID);
		assert(view != null);
		SuggestionView sugView = (SuggestionView) view;
		BPMNSuggestionEngine engine = sugView.getEngine();
		SortedSet<Recommendation> sugSet = engine.suggestionList(idUrl, name);
		assert(sugSet != null);
		Recommendation[] sugArray = (Recommendation[]) sugSet.toArray();
		Map<String, Set<Recommendation>> suggestions = new TreeMap<String, Set<Recommendation>>();
		for (int i = 0; i < sugArray.length; i++) {
			Recommendation sug = sugArray[i];
			String type = sug.getType().toString();
			Set<Recommendation> typedSug = suggestions.get(type);
			if (typedSug == null) {
				typedSug = new TreeSet<Recommendation>(new SuggestionComparator());
				suggestions.put(type, typedSug);
			}
			typedSug.add(sug);
		 }
		 return suggestions;
	}
	
	

}
