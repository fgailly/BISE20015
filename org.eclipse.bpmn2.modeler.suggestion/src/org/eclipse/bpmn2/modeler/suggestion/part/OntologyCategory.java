package org.eclipse.bpmn2.modeler.suggestion.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ugent.mis.cmoeplus.Recommendation;



public class OntologyCategory implements Comparable<OntologyCategory> {
	private String name;
	private int sort;
	private List<Recommendation> sugs = new ArrayList<Recommendation>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public List<Recommendation> getSuggestions() {
		return sugs;
	}

	public void sort() {
		Collections.sort(sugs);
		//Collections.reverse(sugs);	
	}
	
	public int compareTo(OntologyCategory sug) {
		if (this.name == "Delete") {
			return -1;
		} else if (sug.getName() == "Delete") {
			return 1;
		}
		
		return this.name.compareTo(sug.getName());
	}
}
