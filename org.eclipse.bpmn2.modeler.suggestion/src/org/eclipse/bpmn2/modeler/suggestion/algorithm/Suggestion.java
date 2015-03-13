package org.eclipse.bpmn2.modeler.suggestion.algorithm;

import org.semanticweb.owlapi.model.IRI;

import com.aliasi.spell.JaroWinklerDistance;

public class Suggestion implements Comparable<Suggestion> {
	private IRI iri;
	private double weight;
	private String suggestionString;
	private String ontologyString;
	private Type type;
	public enum Type {Class, Datatype};
	private String description;
	private String classes;
	private double weightConstructMatching;
	private double weightLocationMechanism;
	private double weightWordnetSynonyms;
	private double weightTextMatching;
	
	
	public Suggestion(IRI iri, double weight, Type type, String suggestionString, String description, String classes){
		this.iri = iri;
		this.weight = weight;
		this.setType(type);
		this.setSuggestionString(suggestionString);
		this.description = description;
		this.classes = classes;
		this.ontologyString = suggestionString;
	}
	
	public Suggestion(IRI iri, Type type, String suggestionString, String description, String classes){
		this.iri = iri;
		this.weight = 0;
		this.setType(type);
		this.setSuggestionString(suggestionString);
		this.description = description;
		this.classes = classes;
		this.ontologyString = suggestionString;
	}

	@Override
	public int compareTo(Suggestion sug) {
		
		if(weight > sug.weight) {
			return -1;
		}
		else if (weight == sug.weight) {
			return getSuggestionString().compareTo(sug.getSuggestionString());
		} else {
			return 1;
		}
	}
	
	public boolean equals(Suggestion sug){
		return iri.equals(sug);
	}

	public IRI getIri() {
		return iri;
	}

	public void setIri(IRI iri) {
		this.iri = iri;
	}

	public double getWeight() {
		return weight;
	}
	
	public double getJaroWinklerDistance(String label, double jWDweightThreshold, int jWDnumChars){
		JaroWinklerDistance jwd = new JaroWinklerDistance(jWDweightThreshold,jWDnumChars);
		return 1- jwd.distance((CharSequence)label.toLowerCase(), (CharSequence)iri.getFragment().toLowerCase());
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getSuggestionString() {
		return suggestionString;
	}

	public void setSuggestionString(String suggestionString) {
		this.suggestionString = suggestionString;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public String getOntologyString() {
		return ontologyString;
	}

	public void setOntologyString(String ontologyString) {
		this.ontologyString = ontologyString;
	}

	public double getWeightConstructMatching() {
		return weightConstructMatching;
	}

	public void setWeightConstructMatching(double weightConstructMatching) {
		this.weightConstructMatching = weightConstructMatching;
	}

	

	public double getWeightLocationMechanism() {
		return weightLocationMechanism;
	}

	public void setWeightLocationMechanism(double weightLocationMechanism) {
		this.weightLocationMechanism = weightLocationMechanism;
	}

	public double getWeightWordnetSynonyms() {
		return weightWordnetSynonyms;
	}

	public void setWeightWordnetSynonyms(double weightWordnetSynonyms) {
		this.weightWordnetSynonyms = weightWordnetSynonyms;
	}

	public double getWeightTextMatching() {
		return weightTextMatching;
	}

	public void setWeightTextMatching(double weightTextMatching) {
		this.weightTextMatching = weightTextMatching;
	}

}
