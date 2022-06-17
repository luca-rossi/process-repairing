package org.processmining.processrepairing.algorithms.editdistance;

import org.processmining.processrepairing.algorithms.EditDistanceCalculator;
import org.processmining.processrepairing.models.PetrinetModel;

public class EditDistanceCalculatorMCS implements EditDistanceCalculator {
	
	private final String name = "Max Common Subgraph";
	private EdgesCounter edgesCounter;
	
	public EditDistanceCalculatorMCS() {
		edgesCounter = new EdgesCounter();
	}

	public String getName() {
		return name;
	}

	public double compute(PetrinetModel net1, PetrinetModel net2) {
		edgesCounter.compute(net1, net2);
		int numEdges1 = edgesCounter.getNumEdges1();
		int numEdges2 = edgesCounter.getNumEdges2();
		int numCommonEdges = edgesCounter.getNumCommonEdges();
		
		return 1 - (double) numCommonEdges / (double) Math.max(numEdges1, numEdges2);
	}

}