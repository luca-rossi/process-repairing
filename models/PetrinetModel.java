package org.processmining.processrepairing.models;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.processrepairing.algorithms.PetrinetModelConverter;

/**
 * Petri net modeled with two hash tables of nodes (transitions or places),
 * where each (label of a) node is associated to a pair of sets of (labels of) nodes, respectively its entry nodes and exit nodes;
 * this class is an alternative to Petrinet of the PetriNets package and allows to perform graph operations more efficiently
 */
public class PetrinetModel {
	
	private Hashtable<String, Pair<HashSet<String>, HashSet<String>>> transitions;
	private Hashtable<String, Pair<HashSet<String>, HashSet<String>>> places;
	
	private String netLabel;
	private boolean isGenerated;
	private Petrinet generatedPetrinet;
	
	public PetrinetModel(String label){
		this.netLabel = label;
		this.transitions = new Hashtable<String, Pair<HashSet<String>, HashSet<String>>>();
		this.places = new Hashtable<String, Pair<HashSet<String>, HashSet<String>>>();
		this.isGenerated = false;
	}
	
	private Pair<HashSet<String>, HashSet<String>> clonePair(Pair<HashSet<String>, HashSet<String>> pair){
		HashSet<String> left = pair.getLeft();
		HashSet<String> right = pair.getRight();
		HashSet<String> newLeft = new HashSet<String>();
		HashSet<String> newRight = new HashSet<String>();
		for(String s: left)
			newLeft.add(s);
		for(String s: right)
			newRight.add(s);
		return Pair.of(newLeft, newRight);
	}
	
	public PetrinetModel clone() {
		PetrinetModel model = new PetrinetModel(netLabel);
		for(String key: transitions.keySet())
			model.getTransitions().put(key, clonePair(transitions.get(key)));
		for(String key: places.keySet())
			model.getPlaces().put(key, clonePair(places.get(key)));
		return model;
	}
	
	private Hashtable<String, Pair<HashSet<String>, HashSet<String>>> cloneWithSuffix(Hashtable<String, Pair<HashSet<String>, HashSet<String>>> set, String suffix){
		Hashtable<String, Pair<HashSet<String>, HashSet<String>>> newSet = new Hashtable<String, Pair<HashSet<String>, HashSet<String>>>();
        Set<String> keys = set.keySet();
		for(String key: keys)
			newSet.put(key + suffix, cloneWithSuffix(set.get(key), suffix));
		return newSet;
	}
	private Pair<HashSet<String>, HashSet<String>> cloneWithSuffix(Pair<HashSet<String>, HashSet<String>> set, String suffix){
		HashSet<String> left = cloneWithSuffix(set.getLeft(), suffix);
		HashSet<String> right = cloneWithSuffix(set.getRight(), suffix);
		return Pair.of(left, right);
	}
	private HashSet<String> cloneWithSuffix(HashSet<String> set, String suffix){
		HashSet<String> newSet = new HashSet<String>();
		for(String value: set)
			newSet.add(value + suffix);
		return newSet;
	}

	public String getNetLabel() {
		return netLabel;
	}
	
	public Hashtable<String, Pair<HashSet<String>, HashSet<String>>> getTransitions() {
		return transitions;
	}
	public void setTransitions(Hashtable<String, Pair<HashSet<String>, HashSet<String>>> transitions) {
		isGenerated = false;
		this.transitions = transitions;
	}
	public void addTransitions(Hashtable<String, Pair<HashSet<String>, HashSet<String>>> transitions) {
		isGenerated = false;
		this.transitions.putAll(transitions);
	}

	public Hashtable<String, Pair<HashSet<String>, HashSet<String>>> getPlaces() {
		return places;
	}
	public void setPlaces(Hashtable<String, Pair<HashSet<String>, HashSet<String>>> places) {
		isGenerated = false;
		this.places = places;
	}
	public void addPlaces(Hashtable<String, Pair<HashSet<String>, HashSet<String>>> places) {
		isGenerated = false;
		this.places.putAll(places);
	}
	
	public void addPlace(String place) {
		if(places.containsKey(place))
			return;
		isGenerated = false;
		places.put(place, Pair.of(new HashSet<String>(), new HashSet<String>()));
	}
	
	public void addTransition(String transition) {
		if(transitions.containsKey(transition))
			return;
		isGenerated = false;
		transitions.put(transition, Pair.of(new HashSet<String>(), new HashSet<String>()));
	}
	public void addTransition(String transition, HashSet<String> prevPlaces, HashSet<String> succPlaces) {
		isGenerated = false;
		addTransition(transition);
		transitions.put(transition, Pair.of(prevPlaces, succPlaces));
		for(String place: prevPlaces)
			addTransitionAfterPlace(transition, place);
		for(String place: succPlaces)
			addTransitionBeforePlace(transition, place);
	}
	
	public void addPlaceBeforeTransition(String place, String transition) {
		isGenerated = false;
		transitions.get(transition).getLeft().add(place);
		places.get(place).getRight().add(transition);
	}
	public void addPlaceAfterTransition(String place, String transition) {
		isGenerated = false;
		transitions.get(transition).getRight().add(place);
		places.get(place).getLeft().add(transition);
	}
	
	public void addTransitionBeforePlace(String transition, String place) {
		isGenerated = false;
		places.get(place).getLeft().add(transition);
		transitions.get(transition).getRight().add(place);
	}
	public void addTransitionAfterPlace(String transition, String place) {
		isGenerated = false;
		places.get(place).getRight().add(transition);
		transitions.get(transition).getLeft().add(place);
	}

	public void addSubnet(Subprocess subprocess) {
		isGenerated = false;
		String suffix = Const.SUBPROCESS_LABEL_DELIMITER + subprocess.getLabel();
		PetrinetModel model = subprocess.getModel();
		
		addPlaces(cloneWithSuffix(model.getPlaces(), suffix));
		addTransitions(cloneWithSuffix(model.getTransitions(), suffix));
		
		addTransition("t_in" + suffix,
						(HashSet<String>) subprocess.getInitialLocation(),
						cloneWithSuffix((HashSet<String>) subprocess.getInitialMarking(), suffix));
		addTransition("t_out" + suffix,
						cloneWithSuffix((HashSet<String>) subprocess.getFinalMarking(), suffix),
						(HashSet<String>) subprocess.getFinalLocation());
	}

	public void removeSubnet(Subprocess subprocess) {
		isGenerated = false;
		String suffix = Const.SUBPROCESS_LABEL_DELIMITER + subprocess.getLabel();

		places.keySet().removeIf(place -> place.endsWith(suffix));
		transitions.keySet().removeIf(transition -> transition.endsWith(suffix));

		for(String place: places.keySet()) {
			places.get(place).getLeft().removeIf(transition -> transition.endsWith(suffix));
			places.get(place).getRight().removeIf(transition -> transition.endsWith(suffix));
		}
		for(String transition: transitions.keySet()) {
			transitions.get(transition).getLeft().removeIf(place -> place.endsWith(suffix));
			transitions.get(transition).getRight().removeIf(place -> place.endsWith(suffix));
		}
	
	}
	
	public Set<String> getTransitionsFromPlace(String place){
		return places.get(place).getRight();
	}
	
	public Set<String> getTransitionsToPlace(String place){
		return places.get(place).getLeft();
	}

	public Set<String> getPlacesWithoutEntryNodes() {
		HashSet<String> set = new HashSet<String>();
		for(String p: places.keySet())
			if(places.get(p).getLeft().isEmpty())
				set.add(p);
		return set;
	}

	public Set<String> getPlacesWithoutExitNodes() {
		HashSet<String> set = new HashSet<String>();
		for(String p: places.keySet())
			if(places.get(p).getRight().isEmpty())
				set.add(p);
		return set;
	}

	public Petrinet generatePetrinet() {
		if(!isGenerated){
			isGenerated = true;
			generatedPetrinet = PetrinetModelConverter.getPetrinetFromModel(this);
		}
		return generatedPetrinet;
	}
}
