package org.processmining.processrepairing.algorithms.editdistance;

import org.processmining.processrepairing.algorithms.EditDistanceCalculator;
import org.processmining.processrepairing.models.PetrinetModel;

public class EditDistanceCalculatorGU implements EditDistanceCalculator {
	
	private final String name = "Graph Union";
	private EdgesCounter edgesCounter;
	
	public EditDistanceCalculatorGU() {
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
		
		return 1 - (double) numCommonEdges / (double) (numEdges1 + numEdges2 - numCommonEdges);
	}

}