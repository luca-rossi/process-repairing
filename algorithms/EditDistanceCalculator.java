package org.processmining.processrepairing.algorithms;

import org.processmining.processrepairing.models.PetrinetModel;

/**
 * Each class implementing this interface will be added to the list of methods
 * for calculating the edit distance between two processes (represented as PetrinetModel objects)
 */
public interface EditDistanceCalculator {

	public String getName();
	public double compute(PetrinetModel net1, PetrinetModel net2);

}
