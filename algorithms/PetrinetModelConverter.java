package org.processmining.processrepairing.algorithms;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.processrepairing.models.Const;
import org.processmining.processrepairing.models.PetrinetModel;

public class PetrinetModelConverter {

	/**
	 * Convert a Petrinet instance into a PetrinetModel instance,
	 * used when a net is loaded and some computations need to be performed on it
	 * @param net to convert into a PetrinetModel
	 * @return PetrinetModel instance
	 */
	public static PetrinetModel getModelFromPetrinet(Petrinet net) {
		PetrinetModel model = new PetrinetModel(net.getLabel());
		Iterator<Place> places = net.getPlaces().iterator();
		while(places.hasNext()) {
			Place place = places.next();
			String placeLabel = removeSuffix(place.getLabel());
			model.addPlace(placeLabel);
			Iterator<Transition> transitions = net.getTransitions().iterator();
			while(transitions.hasNext()) {
				Transition transition = transitions.next();
				String transitionLabel = removeSuffix(transition.getLabel());
				model.addTransition(transitionLabel);
				if(net.getArc(place, transition) != null)
					model.addTransitionAfterPlace(transitionLabel, placeLabel);
				if(net.getArc(transition, place) != null)
					model.addTransitionBeforePlace(transitionLabel, placeLabel);
			}
		}
		return model;
	}

	/**
	 * Convert a PetrinetModel instance into a Petrinet instance,
	 * used when a model needs to be visualized or used by plug-ins which need Petrinet inputs
	 * @param model to convert into a Petrinet
	 * @return Petrinet instance
	 */
	public static Petrinet getPetrinetFromModel(PetrinetModel model) {
		Petrinet net = PetrinetFactory.newPetrinet(model.getNetLabel());
		Hashtable<String, Pair<HashSet<String>, HashSet<String>>> transitions = model.getTransitions();
		Hashtable<String, Transition> labeledTransitions = new Hashtable<String, Transition>();
		for(String transition: transitions.keySet()) {
			Transition t = net.addTransition(transition);
			labeledTransitions.put(transition, t);
		}
		Hashtable<String, Pair<HashSet<String>, HashSet<String>>> places = model.getPlaces();
		for(String place: places.keySet()) {
			Place p = net.addPlace(place);
			p.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			p.getAttributeMap().put(AttributeMap.LABEL, p.getLabel());
			for(String transition: places.get(place).getLeft())
				net.addArc(labeledTransitions.get(transition), p);
			for(String transition: places.get(place).getRight())
				net.addArc(p, labeledTransitions.get(transition));
		}
		return net;
	}

	/**
	 * Replace the subprocess delimiter in a label with another symbol,
	 * used to avoid to mistakenly associate a label to a subprocess
	 * @param str string to modify
	 * @return string with replaced subprocess delimiters
	 */
	private static String removeSuffix(String str){
		return str.replaceAll(Const.SUBPROCESS_LABEL_DELIMITER, Const.SUBPROCESS_LABEL_DELIMITER_ESCAPE);
	}
	
}
