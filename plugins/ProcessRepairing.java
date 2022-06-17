package org.processmining.processrepairing.plugins;

import java.util.HashSet;
import java.util.Iterator;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.processrepairing.models.State;
import org.processmining.processrepairing.models.Subprocess;

@Plugin(name = "Process Repairing",
		parameterLabels = {"Main Process", "Subprocess", "Initial location", "Final location", "Initial marking", "Final marking"},
		returnLabels = {"Net"},
		returnTypes = {Petrinet.class},
		userAccessible = true,
		help = "This plug-in can be used to connect the model (a Petri net) of a subprocess to the model of a main process, " +
				"adding invisible transitions between a given location (initial and final places belonging the main process) " +
				"and a given marking (initial and final places belonging to the subprocess).")
public class ProcessRepairing {

	@UITopiaVariant(affiliation = "UnivPM",
					author = "Luca Rossi",
					email = "rossiluca711@gmail.com")
	@PluginVariant(variantLabel = "Process Repairing", requiredParameterLabels = { 0, 1, 2, 3, 4, 5 })
	public Petrinet run(UIPluginContext context, Petrinet process, Petrinet subprocess, Place[] initialLocation, Place[] finalLocation, Place[] initialMarking, Place[] finalMarking) {
		Petrinet[] subprocesses = new Petrinet[1];
		subprocesses[0] = subprocess;
		State state = new State(process, subprocesses);
		Subprocess s = state.getSubprocesses().get(0);
		
		for(Place place : initialLocation)
			s.addPlaceToInitialLocation(place.getLabel());
		for(Place place : finalLocation)
			s.addPlaceToFinalLocation(place.getLabel());
		
		s.setInitialMarking(new HashSet<String>());		// remove the default marking
		s.setFinalMarking(new HashSet<String>());		// remove the default marking
		for(Place place : initialMarking)
			s.addPlaceToInitialMarking(place.getLabel());
		for(Place place : finalMarking)
			s.addPlaceToFinalMarking(place.getLabel());
		
		state.setSelectedSubprocess(s);
		state.addSubprocessToMainProcess(s);
		return state.getMainProcess().generatePetrinet();
	}
	
	/**
	 * This plug-in variant can be used to test the plug-in with hard-coded location and marking
	 */
	@UITopiaVariant(affiliation = "UnivPM",
					author = "Luca Rossi",
					email = "rossiluca711@gmail.com")
	@PluginVariant(variantLabel = "Process Repairing Dummy", requiredParameterLabels = { 0, 1 })
	public Petrinet runDummy(UIPluginContext context, Petrinet process, Petrinet subprocess) {
		Place[] initialLocation = new Place[1];
		Place[] finalLocation = new Place[1];
		Place[] initialMarking = new Place[1];
		Place[] finalMarking = new Place[1];
		
		Iterator<Place> places = process.getPlaces().iterator();
		initialLocation[0] = places.next();
		finalLocation[0] = places.next();
		
		places = subprocess.getPlaces().iterator();
		initialMarking[0] = places.next();
		finalMarking[0] = places.next();

		return run(context, process, subprocess, initialLocation, finalLocation, initialMarking, finalMarking);
	}

}
