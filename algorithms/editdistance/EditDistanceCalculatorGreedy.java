package org.processmining.processrepairing.algorithms.editdistance;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.petrinets.analysis.gedsim.algorithms.impl.GraphEditDistanceSimilarityGreedy;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;
import org.processmining.processrepairing.algorithms.EditDistanceCalculator;
import org.processmining.processrepairing.models.PetrinetModel;

public class EditDistanceCalculatorGreedy implements EditDistanceCalculator {
	
	private final String name = "Lexical Greedy";
	private GraphEditDistanceSimilarityGreedy<Petrinet> editDistanceCalculator;

	public EditDistanceCalculatorGreedy() {
		editDistanceCalculator = new GraphEditDistanceSimilarityGreedy<Petrinet>(new GraphEditDistanceSimilarityParameters());
	}

	public String getName() {
		return name;
	}

	public double compute(PetrinetModel net1, PetrinetModel net2) {
		return editDistanceCalculator.compute(net1.generatePetrinet(), net2.generatePetrinet());
	}

}