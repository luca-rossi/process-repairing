package org.processmining.processrepairing.models;

import java.util.HashSet;
import java.util.Set;

import org.processmining.processrepairing.algorithms.SubnetExtractor;

/**
 * Subprocess modeled with a Petri net (PetrinetModel), a label, its location and its marking;
 * two other nets are associated to it (replacedInitialModel and replacedCurrentModel) in order to calculate the edit distance,
 * they are the subnets of the (initial and updated) main process between the initial and final location
 */
public class Subprocess {

	private PetrinetModel model;
	private String label;
	private boolean included;
	
	private PetrinetModel initialMainProcess;
	private PetrinetModel mainProcess;
	
	private Set<String> initialLocation;
	private Set<String> finalLocation;
	private Set<String> initialMarking;
	private Set<String> finalMarking;
	
	private PetrinetModel replacedInitialModel;
	private PetrinetModel replacedCurrentModel;
	private boolean updatedInitial;
	private boolean updatedCurrent;

	public Subprocess(PetrinetModel model, String label, PetrinetModel initialMainProcess, PetrinetModel mainProcess) {
		this.model = model;
		this.label = label;
		this.included = false;
		this.updatedInitial = false;
		this.updatedCurrent = false;
		this.initialLocation = new HashSet<String>();
		this.finalLocation = new HashSet<String>();
		this.initialMarking = model.getPlacesWithoutEntryNodes();
		this.finalMarking = model.getPlacesWithoutExitNodes();
		this.initialMainProcess = initialMainProcess;
		this.mainProcess = mainProcess;
	}
	
	public PetrinetModel getModel() {
		return model;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isIncluded() {
		return included;
	}
	public void setIncluded(boolean included) {
		this.included = included;
	}

	public Set<String> getInitialLocation() {
		return initialLocation;
	}
	public void setInitialLocation(Set<String> initialLocation) {
		this.initialLocation = initialLocation;
	}
	public void addPlaceToInitialLocation(String place){
		this.initialLocation.add(place);
	}
	public void removePlaceFromInitialLocation(String place){
		this.initialLocation.remove(place);
	}

	public Set<String> getFinalLocation() {
		return finalLocation;
	}
	public void setFinalLocation(Set<String> finalLocation) {
		this.finalLocation = finalLocation;
	}
	public void addPlaceToFinalLocation(String place){
		this.finalLocation.add(place);
	}
	public void removePlaceFromFinalLocation(String place){
		this.finalLocation.remove(place);
	}

	public Set<String> getInitialMarking() {
		return initialMarking;
	}
	public void setInitialMarking(Set<String> initialMarking) {
		this.initialMarking = initialMarking;
	}
	public void addPlaceToInitialMarking(String place) {
		this.initialMarking.add(place);
	}
	public void removePlaceFromInitialMarking(String place) {
		this.initialMarking.remove(place);
	}

	public Set<String> getFinalMarking() {
		return finalMarking;
	}
	public void setFinalMarking(Set<String> finalMarking) {
		this.finalMarking = finalMarking;
	}
	public void addPlaceToFinalMarking(String place) {
		this.finalMarking.add(place);
	}
	public void removePlaceFromFinalMarking(String place) {
		this.finalMarking.remove(place);
	}
	
	public void setUpdatedInitial(boolean updatedInitial) {
		this.updatedInitial = updatedInitial;
	}

	public void setUpdatedCurrent(boolean updatedCurrent) {
		this.updatedCurrent = updatedCurrent;
	}

	public PetrinetModel getReplacedInitialModel() {
		if(!updatedInitial) {
			replacedInitialModel = SubnetExtractor.getReduced(initialMainProcess, initialLocation, finalLocation);
			updatedInitial = true;
		}
		return replacedInitialModel;
	}

	public PetrinetModel getReplacedCurrentModel() {
		if(!updatedCurrent) {
			PetrinetModel net = mainProcess.clone();
			net.removeSubnet(this);
			replacedCurrentModel = SubnetExtractor.getReduced(net, initialLocation, finalLocation);
			updatedCurrent = true;
		}
		return replacedCurrentModel;
	}

}
