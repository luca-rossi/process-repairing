package org.processmining.processrepairing.algorithms;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.processmining.processrepairing.models.Const;
import org.processmining.processrepairing.models.PetrinetModel;
import org.processmining.processrepairing.models.State;
import org.processmining.processrepairing.models.Subprocess;
import org.reflections.Reflections;

public class EditDistanceHandler {
	
	private static EditDistanceHandler instance = null;
	
	private Hashtable<String, EditDistanceCalculator> algorithms;
	private String currentAlgorithm;
	
	/**
	 * Import the classes implementing the EditDistanceCalculator interface,
	 * each of these classes provides a different algorithm to calculate the edit distance between two processes.
	 * These classes can be directly found in the class Const,
	 * or searched through the packages also defined in Const (slower)
	 */
	public EditDistanceHandler() {
		HashSet<Class<? extends EditDistanceCalculator>> classes = new HashSet<Class<? extends EditDistanceCalculator>>();
		
		for(String path: Const.PACKAGES_TO_SEARCH) {
			Reflections reflections = new Reflections(path);
			classes.addAll(reflections.getSubTypesOf(EditDistanceCalculator.class));
		}
		
		for(Class<? extends EditDistanceCalculator> c: Const.EDIT_DISTANCE_CLASSES)
			classes.add(c);
		
		algorithms = new Hashtable<String, EditDistanceCalculator>();
		for(Class<? extends EditDistanceCalculator> c : classes) {
			try {
				EditDistanceCalculator obj = c.newInstance();
				algorithms.put(obj.getName(), obj);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Compute the edit distance between the given subprocess and the part of the main process it would replace
	 * @param subprocess replacing a part of the main process, from which the edit distance will be calculated
	 * @return edit distance calculated using an instance of the class providing the selected algorithm
	 */
	public double compute(Subprocess subprocess, String replacementMode) {
		PetrinetModel replacedModel = replacementMode.equals(State.REPLACEMENT_MODE_INITIAL)
										? subprocess.getReplacedInitialModel()
										: subprocess.getReplacedCurrentModel();
		return algorithms.get(currentAlgorithm).compute(subprocess.getModel(), replacedModel);
	}
	
	public Set<String> getAlgorithmNames() {
		return algorithms.keySet();
	}

	public String getCurrentAlgorithm() {
		return currentAlgorithm;
	}

	public void setCurrentAlgorithm(String currentAlgorithm) {
		this.currentAlgorithm = currentAlgorithm;
	}
	
	public static EditDistanceHandler getInstance() {
		if(instance == null)
			instance = new EditDistanceHandler();
		return instance;
	}

}
