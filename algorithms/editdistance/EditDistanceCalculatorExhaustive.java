package org.processmining.processrepairing.algorithms.editdistance;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.petrinets.analysis.gedsim.algorithms.impl.GraphEditDistanceSimilarityExhaustive;
import org.processmining.petrinets.analysis.gedsim.params.GraphEditDistanceSimilarityParameters;
import org.processmining.processrepairing.algorithms.EditDistanceCalculator;
import org.processmining.processrepairing.models.PetrinetModel;

public class EditDistanceCalculatorExhaustive implements EditDistanceCalculator {
	
	private final String name = "Lexical Exhaustive";
	private GraphEditDistanceSimilarityExhaustive<Petrinet> editDistanceCalculator;

	public EditDistanceCalculatorExhaustive() {
		editDistanceCalculator = new GraphEditDistanceSimilarityExhaustive<Petrinet>(new GraphEditDistanceSimilarityParameters());
	}

	public String getName() {
		return name;
	}

	public double compute(PetrinetModel net1, PetrinetModel net2) {
		return editDistanceCalculator.compute(net1.generatePetrinet(), net2.generatePetrinet());
	}

}