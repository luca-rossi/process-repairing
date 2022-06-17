package org.processmining.processrepairing.algorithms;

import java.util.HashSet;
import java.util.Set;

import org.processmining.processrepairing.models.PetrinetModel;

public class SubnetExtractor {

	/**
	 * Extract a subnet from a Petri net, containing all the paths from the given start places to the given end places,
	 * these paths are found using a depth-first search
	 * @param net from which the subnet will be extracted
	 * @param initialLocation beginning of each path
	 * @param finalLocation end of each path
	 * @return model of the extracted subnet
	 */
	public static PetrinetModel getReduced(PetrinetModel net, Set<String> initialLocation, Set<String> finalLocation) {
		PetrinetModel model = new PetrinetModel(net.getNetLabel());
		Set<String> visitedNodes = new HashSet<String>();
		for(String place: initialLocation)
			addPlaceIfPath(place, finalLocation, visitedNodes, model, net);
		return model;
	}

	/**
	 * Add a given place to the subnet if it belongs to a path by recursively calling
	 * addTransitionIfPath() and this method for each transition/place following the given place/transition.
	 * The place belongs to a new path only if at least one following transitions does.
	 * The recursion ends with the place being either an end place (so it's added to a new path)
	 * or an already visited place (so it's not added to a new path)
	 * @param place to add to the subnet if it belongs to a path
	 * @param finalLocation where paths end (if the place is one of them, it's added to a new path)
	 * @param visitedNodes already visited in the search (if the place is one of them, it's not added to a new path)
	 * @param model of the extracted subnet, where the places are recursively added
	 * @param net from which the subnet is extracted (and the transitions following the place are loaded from)
	 * @return true if the place belongs to a new path
	 */
	private static boolean addPlaceIfPath(String place, Set<String> finalLocation, Set<String> visitedNodes,
										PetrinetModel model, PetrinetModel net) {
		if(finalLocation.contains(place)) {
			model.addPlace(place);
			return true;
		}
		if(visitedNodes.contains(place))
			return model.getPlaces().containsKey(place);
		visitedNodes.add(place);
		boolean toAdd = false;
		for(String transition: net.getPlaces().get(place).getRight()) {
			if(addTransitionIfPath(transition, finalLocation, visitedNodes, model, net)) {
				model.addPlace(place);
				model.addTransitionAfterPlace(transition, place);
				toAdd = true;
			}
		}
		return toAdd;
	}

	/**
	 * Add a given transition to the subnet if it belongs to a path by recursively calling
	 * addPlaceIfPath() and this method for each place/transition following the given transition/place.
	 * The transition belongs to a new path only if at least one following places does.
	 * @param transition to add to the subnet if it belongs to a path
	 * @param finalLocation to pass to the addPlaceIfPath() method
	 * @param visitedNodes to pass to the addPlaceIfPath() method
	 * @param model of the extracted subnet, where the transitions are recursively added
	 * @param net from which the subnet is extracted (and the places following the transitions are loaded from)
	 * @return true if the transition belongs to a new path
	 */
	private static boolean addTransitionIfPath(String transition, Set<String> finalLocation, Set<String> visitedNodes,
												PetrinetModel model, PetrinetModel net) {
		boolean toAdd = false;
		for(String place: net.getTransitions().get(transition).getRight()) {
			if(addPlaceIfPath(place, finalLocation, visitedNodes, model, net)) {
				model.addTransition(transition);
				model.addPlaceAfterTransition(place, transition);
				toAdd = true;
			}
		}
		return toAdd;
	}

}
